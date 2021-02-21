package jp.mydns.dego.motionchecker.VideoPlayer;

import android.media.MediaMetadataRetriever;
import android.view.Display;
import android.view.Surface;
import android.view.View;

import jp.mydns.dego.motionchecker.BuildConfig;
import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.View.ViewController;

public class VideoController {

    // ---------------------------------------------------------------------------------------------
    // inner class
    // ---------------------------------------------------------------------------------------------
    public static class VideoInfo {
        int width;
        int height;
        int duration;
        int rotation;

        private VideoInfo() {
            this.width = 0;
            this.height = 0;
            this.duration = 0;
            this.rotation = 0;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoController";
    private VideoInfo info;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final ViewController viewController;
    private final VideoDecoder decoder;
    private final PlaySpeedManager speedManager;
    private String filePath;
    private Thread videoThread;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoController
     */
    public VideoController() {
        DebugLog.d(TAG, "VideoController");
        this.filePath = null;
        this.viewController = new ViewController();
        this.decoder = new VideoDecoder();
        this.speedManager = new PlaySpeedManager();

        this.decoder.setOnVideoChangeListener(new OnVideoChangeListener() {
            @Override
            public void onDurationChanged(int duration) {
                viewController.setDuration(duration);
            }

            @Override
            public void setVisibilities(VideoDecoder.DecoderStatus status) {
                viewController.setVisibilities(status);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * viewSetup
     *
     * @param rootView root view
     * @param display  display
     */
    public void viewSetup(View rootView, Display display) {
        DebugLog.d(TAG, "viewSetup");
        this.viewController.bindRootView(rootView);
        this.viewController.bindDisplay(display);
        this.viewController.setSurfaceViewSize(this.info.width, this.info.height, this.info.rotation);
    }

    /**
     * setVisibilities
     *
     * @param status video status
     */
    public void setVisibilities(VideoDecoder.DecoderStatus status) {
        DebugLog.d(TAG, "setVisibilities");
        this.viewController.setVisibilities(status);
    }

    /**
     * setVisibilities
     *
     * @param rootView root view
     * @param status   decoder status
     */
    public void setVisibilities(View rootView, VideoDecoder.DecoderStatus status) {
        DebugLog.d(TAG, "setVisibilities");
        this.viewController.bindRootView(rootView);
        this.setVisibilities(status);
    }

    /**
     * setProgress
     *
     * @param progress video progress
     */
    public void setProgress(int progress) {
        DebugLog.d(TAG, "setProgress");
        this.viewController.setProgress(progress);
    }

    /**
     * videoSelecting
     */
    public void videoSelecting() {
        DebugLog.d(TAG, "videoSelecting");
        if (this.videoThread != null && this.videoThread.isAlive()) {
            try {
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * setVideoPath
     *
     * @param path video file path
     */
    public void setVideoPath(String path) {
        DebugLog.d(TAG, "setVideoPath");
        this.filePath = path;
        DebugLog.v(TAG, "path : " + this.filePath);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        if (BuildConfig.DEBUG) {
            logMetaData(retriever);
        }

        this.info = new VideoInfo();
        this.info.width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        this.info.height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        this.info.rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));

        this.speedManager.init();
    }

    /**
     * videoSetup
     *
     * @param surface surface
     */
    public void videoSetup(Surface surface) {
        DebugLog.d(TAG, "videoSetup");

        if (this.videoThread != null && this.videoThread.isAlive()) {
            try {
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            return;
        }

        if (this.filePath != null && surface != null) {
            if (this.decoder.init(this.filePath, surface)) {
                this.threadStart();
            }
        }
    }

    /**
     * hasVideoPath
     *
     * @return video controller is standby.
     */
    public boolean hasVideoPath() {
        return (this.filePath != null && !"".equals(this.filePath));
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
     * stop
     */
    public void stop() {
        DebugLog.d(TAG, "stop");

    }

    /**
     * speedUp
     */
    public void speedUp() {
        DebugLog.d(TAG, "speedUp");

        this.speedManager.speedUp();
        this.viewController.updateSpeedUpDownViews();
        this.decoder.setSpeed(this.speedManager.getSpeed());
    }

    /**
     * speedDown
     */
    public void speedDown() {
        DebugLog.d(TAG, "speedDown");

        this.speedManager.speedDown();
        this.viewController.updateSpeedUpDownViews();
        this.decoder.setSpeed(this.speedManager.getSpeed());
    }

    /**
     * nextFrame
     */
    public void nextFrame() {
        DebugLog.d(TAG, "nextFrame");

    }

    /**
     * previousFrame
     */
    public void previousFrame() {
        DebugLog.d(TAG, "previousFrame");
    }

    /**
     * seekTo
     *
     * @param progress video progress
     */
    public void seekTo(int progress) {
        DebugLog.d(TAG, "seekTo");
        if (decoder.getStatus() == VideoDecoder.DecoderStatus.SEEKING) {
            return;
        }
        if (this.videoThread.isAlive()) {
            try {
                this.videoThread.join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        this.decoder.seekTo(progress);
        this.threadStart();
    }

    /**
     * getSpeedLevel
     *
     * @return speedLevel
     */
    public int getSpeedLevel() {
        return this.speedManager.getSpeedLevel();
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

    /**
     * play
     */
    private void play() {
        DebugLog.d(TAG, "play");

        if (this.decoder.getFramePosition() == VideoDecoder.FramePosition.END) {
            this.decoder.release();
            if (this.decoder.prepare(this.filePath)) {
                this.threadStart();
            }
        } else {
            threadStart();
        }
    }

    /**
     * pause
     */
    private void pause() {
        DebugLog.d(TAG, "pause");

        if (this.videoThread != null && this.videoThread.isAlive()) {
            this.videoThread.interrupt();
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
     * logMetaData
     *
     * @param retriever media meta data retriever
     */
    private void logMetaData(MediaMetadataRetriever retriever) {
        DebugLog.d(TAG, "logMetaData");

        DebugLog.d(TAG, "==================================================");
        DebugLog.d(TAG, "has audio  :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO));
        DebugLog.d(TAG, "has video  :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO));
        DebugLog.d(TAG, "date       :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
        DebugLog.d(TAG, "width      :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        DebugLog.d(TAG, "height     :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        DebugLog.d(TAG, "duration   :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        DebugLog.d(TAG, "rotation   :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        DebugLog.d(TAG, "num tracks :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS));
        DebugLog.d(TAG, "title      :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        DebugLog.d(TAG, "==================================================");
    }
}
