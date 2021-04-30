package jp.mydns.dego.motionchecker.VideoPlayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.PixelCopy;
import android.view.Surface;

import jp.mydns.dego.motionchecker.Util.BitmapHelper;
import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.View.VideoSurfaceView;
import jp.mydns.dego.motionchecker.View.VideoViewController;

public class VideoController {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoController";
    private static final int MOVE_TIME = 10000;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final VideoViewController viewController;
    private final VideoDecoder decoder;
    private final PlaySpeedManager speedManager;
    private final MotionGenerator motionGenerator;
    private Thread videoThread;

    private Video video;
    private Surface surface;

    private Bitmap videoCapture;
    private final PixelCopy.OnPixelCopyFinishedListener pixelCopyFinishedListener;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoController
     */
    public VideoController() {
        DebugLog.d(TAG, "VideoController");
        this.viewController = new VideoViewController();
        this.decoder = new VideoDecoder();
        this.speedManager = new PlaySpeedManager();
        this.motionGenerator = new MotionGenerator();

        this.decoder.setOnVideoChangeListener(new OnVideoChangeListener() {
            @Override
            public void onDurationChanged(int duration) {
                DebugLog.d(TAG, "onDurationChanged");
                DebugLog.v(TAG, "duration : " + duration);
                if (video != null) {
                    video.setDuration(duration);
                    viewController.setDuration(video.getDuration());
                }
            }

            @Override
            public void setVisibilities(VideoDecoder.DecoderStatus status) {
                viewController.setVisibilities(status);
            }
        });

        this.pixelCopyFinishedListener = new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                DebugLog.d(TAG, "onPixelCopyFinished");
                if (copyResult == PixelCopy.SUCCESS) {
                    pixelCopyFinished();
                } else {
                    DebugLog.w(TAG, "PixelCopy Error. (" + copyResult + ")");
                }
            }
        };
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * setViews
     *
     * @param activity activity
     */
    public void setViews(Activity activity) {
        DebugLog.d(TAG, "setViews");

        this.viewController.setViews(activity);
        this.viewController.bindDisplay(activity.getWindowManager().getDefaultDisplay());
        if (!this.isVideoStandby()) {
            this.setVisibilities(VideoDecoder.DecoderStatus.INIT);
        }
    }

    /**
     * setVisibilities
     *
     * @param status video status
     */
    public void setVisibilities(VideoDecoder.DecoderStatus status) {
        this.viewController.setVisibilities(status);
    }

    /**
     * setViewEnable
     *
     * @param position video position
     */
    public void setViewEnable(VideoDecoder.FramePosition position) {
        DebugLog.d(TAG, "setViewEnable");
        this.viewController.updateNextPreviousViews(position);
    }

    /**
     * setProgress
     *
     * @param progress video progress
     */
    public void setProgress(int progress) {
        DebugLog.d(TAG, "setProgress");
        this.viewController.setProgress(progress);

        if (progress == 0) {
            this.setViewEnable(VideoDecoder.FramePosition.FIRST);
        } else if (this.video != null && this.video.getDuration() == progress) {
            this.setViewEnable(VideoDecoder.FramePosition.LAST);
        }
    }

    /**
     * statusChanged
     *
     * @param status video status
     */
    public void statusChanged(VideoDecoder.DecoderStatus status) {
        DebugLog.d(TAG, "statusChanged");
        this.setVisibilities(status);
    }

    /**
     * progressChanged
     *
     * @param progress video progress
     */
    public void progressChanged(int progress, VideoDecoder.FramePosition position) {
        DebugLog.d(TAG, "progressChanged");
        this.setProgress(progress);
        this.setViewEnable(position);
    }

    /**
     * join
     */
    public void join() {
        DebugLog.d(TAG, "join");
        if (this.videoThread != null && this.videoThread.isAlive()) {
            try {
                this.videoThread.interrupt();
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * setVideo
     *
     * @param uri video uri
     * @return is set OK?
     */
    public boolean setVideo(Uri uri) {
        DebugLog.d(TAG, "setVideo");

        if (uri == null) {
            this.video = null;
            return false;
        }

        try {
            this.video = new Video(uri);
        } catch (Exception exception) {
            this.video = null;
            exception.printStackTrace();
            return false;
        }

        this.viewController.setSurfaceViewSize(this.video);
        this.viewController.setVisibilities(VideoDecoder.DecoderStatus.PAUSED);

        this.speedManager.init();
        return true;
    }

    /**
     * setSurface
     *
     * @param surface surface
     */
    public void setSurface(Surface surface) {
        DebugLog.d(TAG, "setSurface");

        if (surface != null && surface.isValid()) {
            this.surface = surface;
        } else {
            DebugLog.w(TAG, "surface is invalid.");
            this.surface = null;
            return;
        }

        this.videoSetup();
    }

    /**
     * isVideoStandby
     *
     * @return video controller is standby.
     */
    public boolean isVideoStandby() {
        return (this.video != null && this.surface != null);
    }

    /**
     * playOrPause
     */
    public void playOrPause() {
        DebugLog.d(TAG, "playOrPause");

        VideoDecoder.DecoderStatus status = this.decoder.getStatus();
        if (status == VideoDecoder.DecoderStatus.PLAYING) {
            this.pause();
        } else if (status == VideoDecoder.DecoderStatus.PAUSED) {
            this.play();
        }
    }

    /**
     * pause
     */
    public void pause() {
        DebugLog.d(TAG, "pause");

        if (this.videoThread != null && this.videoThread.isAlive()) {
            this.videoThread.interrupt();
        }
    }

    /**
     * stop
     */
    public void stop() {
        DebugLog.d(TAG, "stop");

        if (this.videoThread != null && this.videoThread.isAlive()) {
            try {
                this.videoThread.interrupt();
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        this.decoder.release();
        this.decoder.prepare(this.video, this.speedManager.getSpeed(), false);

        this.threadStart();
    }

    /**
     * speedUp
     */
    public void speedUp() {
        DebugLog.d(TAG, "speedUp");

        if (this.decoder.getStatus() == VideoDecoder.DecoderStatus.PAUSED) {
            this.speedManager.speedUp();
            this.viewController.updateSpeedUpDownViews();
            this.decoder.setSpeed(this.speedManager.getSpeed());
        }
    }

    /**
     * speedDown
     */
    public void speedDown() {
        DebugLog.d(TAG, "speedDown");

        if (this.decoder.getStatus() == VideoDecoder.DecoderStatus.PAUSED) {
            this.speedManager.speedDown();
            this.viewController.updateSpeedUpDownViews();
            this.decoder.setSpeed(this.speedManager.getSpeed());
        }
    }

    /**
     * nextFrame
     */
    public void nextFrame() {
        DebugLog.d(TAG, "nextFrame");

        if (this.videoThread != null && this.videoThread.isAlive()) {
            DebugLog.i(TAG, "can not start thread. (decode thread is running.)");
            return;
        }

        if (this.decoder.getStatus() == VideoDecoder.DecoderStatus.PAUSED) {
            this.decoder.next();
            this.threadStart();
        }
    }

    /**
     * previousFrame
     */
    public void previousFrame() {
        DebugLog.d(TAG, "previousFrame");

        if (this.videoThread != null && this.videoThread.isAlive()) {
            DebugLog.i(TAG, "can not start thread. (decode thread is running.)");
            return;
        }

        if (this.decoder.getStatus() == VideoDecoder.DecoderStatus.PAUSED) {
            this.decoder.previous();
            this.threadStart();
        }
    }

    /**
     * seekTo
     *
     * @param progress  video progress
     * @param precisely precisely
     */
    public void seekTo(int progress, boolean precisely) {
        DebugLog.d(TAG, "seekTo");
        if (decoder.getStatus() == VideoDecoder.DecoderStatus.SEEKING) {
            return;
        }
        if (this.videoThread != null && this.videoThread.isAlive()) {
            try {
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        this.decoder.seekTo(progress, precisely);
        this.threadStart();
    }

    /**
     * moveAfter
     */
    public void moveAfter() {
        DebugLog.d(TAG, "moveAfter");
        if (decoder.getStatus() == VideoDecoder.DecoderStatus.SEEKING) {
            return;
        }
        if (this.videoThread != null && this.videoThread.isAlive()) {
            try {
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        int progress = this.viewController.getProgress();
        int duration = this.viewController.getDuration();
        if (progress + MOVE_TIME < duration) {
            this.decoder.seekTo(progress + MOVE_TIME, false);
            this.threadStart();
        }
    }

    /**
     * moveBefore
     */
    public void moveBefore() {
        DebugLog.d(TAG, "moveBefore");
        if (decoder.getStatus() == VideoDecoder.DecoderStatus.SEEKING) {
            return;
        }
        if (this.videoThread != null && this.videoThread.isAlive()) {
            try {
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        int progress = this.viewController.getProgress();
        if (progress - MOVE_TIME > 0) {
            this.decoder.seekTo(progress - MOVE_TIME, false);
            this.threadStart();
        }
    }

    /**
     * getSpeedLevel
     *
     * @return speedLevel
     */
    public int getSpeedLevel() {
        return this.speedManager.getSpeedLevel();
    }

    /**
     * animFullscreenPreview
     */
    public void animFullscreenPreview() {
        this.viewController.animFullscreenPreview();
    }

    /**
     * viewLock
     */
    public void viewLock() {
        DebugLog.d(TAG, "viewLock");
        this.viewController.changeViewLock();
    }

    /**
     * generateMotionImage
     */
    public void generateMotionImage() {
        DebugLog.d(TAG, "generateMotionImage");

        if (!this.motionGenerator.isStarted()) {
            int progress = this.viewController.getProgress();
            this.motionGenerator.start(progress);
            this.pixelCopyRequest();
        } else {
            DebugLog.w(TAG, "Motion image generation is already started.");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

    /**
     * videoSetup
     */
    private void videoSetup() {
        DebugLog.d(TAG, "videoSetup");

        if (this.video == null) {
            DebugLog.w(TAG, "video is null.");
            return;
        }

        if (surface != null) {
            if (this.decoder.init(this.video, surface, this.speedManager.getSpeed())) {
                this.threadStart();
            }
        }
    }

    /**
     * play
     */
    private void play() {
        DebugLog.d(TAG, "play");

        if (this.decoder.getFramePosition() == VideoDecoder.FramePosition.LAST) {
            this.decoder.release();
            if (this.decoder.prepare(this.video, this.speedManager.getSpeed(), true)) {
                this.threadStart();
            }
        } else {
            this.threadStart();
        }
    }

    /**
     * threadStart
     */
    private void threadStart() {
        DebugLog.d(TAG, "threadStart");
        this.videoThread = new Thread(this.decoder);
        this.videoThread.start();
    }

    /**
     * pixelCopyRequest
     */
    private void pixelCopyRequest() {
        DebugLog.d(TAG, "pixelCopyRequest");

        VideoSurfaceView videoSurfaceView = this.viewController.getVideoSurfaceView();
        int width = videoSurfaceView.getWidth();
        int height = videoSurfaceView.getHeight();
        this.videoCapture = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        PixelCopy.request(
            videoSurfaceView,
            this.videoCapture,
            this.pixelCopyFinishedListener,
            new Handler()
        );
    }

    /**
     * pixelCopyFinished
     */
    private void pixelCopyFinished() {
        DebugLog.d(TAG, "pixelCopyFinished");

        if (this.videoCapture != null) {
            this.motionGenerator.superpose(this.videoCapture);
        }

        if (this.motionGenerator.needNextFrame()) {
            // 次のフレームを描画する
            this.nextFrame();
            DebugLog.v(TAG, "join start");
            try {
                this.videoThread.join(1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            DebugLog.v(TAG, "join end");
            if ((this.videoThread != null && this.videoThread.isAlive()) ||
                this.decoder.getStatus() != VideoDecoder.DecoderStatus.PAUSED) {
                DebugLog.e(TAG, "motion image generate error");
            } else {
                this.pixelCopyRequest();
            }
        } else if (this.motionGenerator.nextStep()) {
            if (this.motionGenerator.isEnd()) {
                // モーション画像の生成終了
                DebugLog.v(TAG, "check");
                int width = this.viewController.getVideoSurfaceView().getWidth();
                int height = this.viewController.getVideoSurfaceView().getHeight();
                Bitmap bitmap = this.motionGenerator.createBitmap(width, height);
                if (BitmapHelper.saveBitmapToExternal(bitmap)) {
                    DebugLog.v(TAG, "bitmap is saved in external storage.");
                } else {
                    DebugLog.e(TAG, "bitmap save error.");
                }

                this.motionGenerator.clear();
            } else {
                // モーション画像の先頭のフレームに移動
                int progress = this.motionGenerator.getStartTime();
                this.seekTo(progress, true);

                DebugLog.v(TAG, "join start");
                try {
                    this.videoThread.join(1000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                DebugLog.v(TAG, "join end");
                if ((this.videoThread != null && this.videoThread.isAlive()) ||
                    this.decoder.getStatus() != VideoDecoder.DecoderStatus.PAUSED) {
                    DebugLog.e(TAG, "motion image generate error");
                } else {
                    this.pixelCopyRequest();
                }
            }
        }
    }

}
