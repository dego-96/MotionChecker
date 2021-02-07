package jp.mydns.dego.motionchecker.VideoPlayer;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Message;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import jp.mydns.dego.motionchecker.InstanceHolder;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoDecoder implements Runnable {

    // ---------------------------------------------------------------------------------------------
    // inner class
    // ---------------------------------------------------------------------------------------------
    private class VideoTimer {
        long startTime;
        long startTimeSys;
        long renderTime;
        long count;
        float speed;
        boolean isInterrupted;

        VideoTimer() {
            this.startTime = 0;
            this.startTimeSys = 0;
            this.renderTime = 0;
            this.count = 0;
            this.speed = 1.0f;
            this.isInterrupted = false;
        }

        void start() {
            this.renderTime = 0;
            this.isInterrupted = false;

            this.startTime = VideoDecoder.this.extractor.getSampleTime();
            this.startTimeSys = System.nanoTime() / 1000;
            this.count = 0;

            DebugLog.v(TAG_THREAD, "-------- start time --------");
            DebugLog.v(TAG_THREAD, "system : " + this.startTimeSys);
            DebugLog.v(TAG_THREAD, "video  : " + this.startTime);
            DebugLog.v(TAG_THREAD, "----------------------------");
        }

        void setRenderTime() {
            this.renderTime = VideoDecoder.this.extractor.getSampleTime();
            this.count++;
        }

        void waitNext() {
            long elapsed;
            long waitTime = this.renderTime - this.startTime;
            do {
                // 再生速度に合わせてシステム時間の経過スピードを変える
                elapsed = (long) ((System.nanoTime() / 1000.0f - (float) this.startTimeSys) * this.speed);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                    this.isInterrupted = true;
                }
            } while (elapsed < waitTime);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // public constant values
    // ---------------------------------------------------------------------------------------------
    /* video status */
    public enum STATUS {
        INIT,
        VIDEO_SELECTED,
        PAUSED,
        PLAYING,
        VIDEO_END,
        SEEKING,
        NEXT_FRAME,
        PREVIOUS_FRAME
    }

    // ---------------------------------------------------------------------------------------------
    // private constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoDecoder";
    private static final String TAG_THREAD = "VideoThread";
    private static final String MIME_VIDEO = "video/";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private STATUS videoStatus;

    private Surface surface;

    private MediaCodec decoder;
    private MediaExtractor extractor;
    private VideoTimer videoTimer;
    private final VideoPlayerHandler handler;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoDecoder
     */
    public VideoDecoder() {
        DebugLog.d(TAG, "VideoDecoder");
        this.videoStatus = STATUS.INIT;
        this.handler = new VideoPlayerHandler();
    }

    // ---------------------------------------------------------------------------------------------
    // package private method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     *
     * @param filePath video file path
     * @param surface  video surface
     * @return initialization result
     */
    boolean init(String filePath, Surface surface) {
        DebugLog.d(TAG, "init");

        this.setStatus(STATUS.INIT, false);
        this.surface = surface;
        this.videoTimer = new VideoTimer();

        return this.prepare(filePath);
    }

    /**
     * prepare
     *
     * @param filePath file path
     * @return prepare result
     */
    boolean prepare(String filePath) {
        DebugLog.d(TAG, "prepare");
        this.extractor = new MediaExtractor();
        try {
            this.extractor.setDataSource(filePath);
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        for (int index = 0; index < this.extractor.getTrackCount(); index++) {
            MediaFormat format = this.extractor.getTrackFormat(index);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime != null && mime.startsWith(MIME_VIDEO)) {
                this.extractor.selectTrack(index);
                try {
                    this.decoder = MediaCodec.createDecoderByType(mime);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                long duration = format.getLong(MediaFormat.KEY_DURATION);
                InstanceHolder.getInstance().getViewController().setDuration((int) (duration / 1000));

                try {
                    DebugLog.d(TAG, "format: " + format);
                    this.decoder.configure(format, this.surface, null, 0);
                } catch (IllegalStateException exception) {
                    exception.printStackTrace();
                    DebugLog.e(TAG, "codec '" + mime + "' failed configuration." + exception);
                    return false;
                }
            }
        }
        if (this.decoder != null) {
            this.decoder.start();
        }

        if (this.getStatus() == STATUS.INIT) {
            this.setStatus(STATUS.VIDEO_SELECTED, true);
        } else if (this.getStatus() == STATUS.VIDEO_END) {
            this.setStatus(STATUS.PAUSED, true);
        }
        return true;
    }

    /**
     * release
     */
    void release() {
        DebugLog.d(TAG, "release");
        this.decoder.stop();
        this.decoder.release();
        this.extractor.release();
    }

    /**
     * setSpeed
     *
     * @param speed video play speed
     */
    void setSpeed(float speed) {
        this.videoTimer.speed = speed;
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * getStatus
     *
     * @return video status
     */
    public STATUS getStatus() {
        return this.videoStatus;
    }

    /**
     * run
     */
    @Override
    synchronized public void run() {
        DebugLog.d(TAG_THREAD, "run");
        DebugLog.d(TAG_THREAD, "status :" + this.getStatus().name());

        switch (this.getStatus()) {
            case INIT:
            case PLAYING:
            default:
                // Nothing to do.
                break;
            case PAUSED:
            case VIDEO_END:
                this.play();
                break;
            case VIDEO_SELECTED:
            case NEXT_FRAME:
            case SEEKING:
                this.nextFrame();
                break;
            case PREVIOUS_FRAME:
//                this.toPreviousFrame();
                break;
        }
    }

    /**
     * seekTo
     *
     * @param progress progress
     */
    public void seekTo(int progress) {
        DebugLog.d(TAG, "seekTo(" + progress + ")");

        this.decoder.flush();
        this.extractor.seekTo(progress * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        this.setStatus(STATUS.SEEKING, true);
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

    /**
     * setStatus
     *
     * @param status status
     */
    private void setStatus(STATUS status, boolean visibility) {
        DebugLog.d(TAG, "setStatus (" + this.videoStatus.name() + " => " + status.name() + ")");
        this.videoStatus = status;

        if (visibility) {
            Context context = InstanceHolder.getInstance();
            if (Thread.currentThread().equals(context.getMainLooper().getThread())) {
                InstanceHolder.getInstance().getViewController().setVisibilities(status);
            } else {
                this.sendMessage();
            }
        }
    }

    /**
     * sendMessage
     */
    private void sendMessage() {
        DebugLog.d(TAG, "sendMessage");

        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putLong(VideoPlayerHandler.MESSAGE_PROGRESS_US, -1);
        bundle.putSerializable(VideoPlayerHandler.MESSAGE_STATUS, this.getStatus());
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    /**
     * sendMessage
     *
     * @param time_us time (microsecond)
     */
    private void sendMessage(long time_us) {
        DebugLog.d(TAG, "sendMessage(" + time_us + ")");

        Bundle bundle = new Bundle();
        bundle.putLong(VideoPlayerHandler.MESSAGE_PROGRESS_US, time_us);
        Message message = Message.obtain();
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    /**
     * play
     */
    private void play() {
        DebugLog.d(TAG_THREAD, "play");
        this.setStatus(STATUS.PLAYING, true);

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean isEos = false;
        this.videoTimer.start();

        while (!Thread.currentThread().isInterrupted() && !this.videoTimer.isInterrupted) {
            if (!isEos) {
                isEos = queueInput();
            }

            queueOutput(info);

            // All decoded frames have been rendered, we can stop playing now
            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                DebugLog.d(TAG_THREAD, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                this.setStatus(STATUS.VIDEO_END, true);
                return;
            }
        }
        this.setStatus(STATUS.PAUSED, true);
    }

    /**
     * nextFrame
     */
    private void nextFrame() {
        DebugLog.d(TAG_THREAD, "nextFrame");
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean isEos = false;
        this.videoTimer.start();

        while (!Thread.currentThread().isInterrupted() && !this.videoTimer.isInterrupted) {
            if (!isEos) {
                isEos = queueInput();
            }

            boolean render = queueOutput(info);

            // 最後まで再生した場合
            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                DebugLog.d(TAG_THREAD, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                this.setStatus(STATUS.VIDEO_END, true);
                return;
            }
            if (render) {
                this.setStatus(STATUS.PAUSED, true);
                return;
            }
        }
    }

    /**
     * queueInput
     *
     * @return is EOS
     */
    private boolean queueInput() {
        int inIndex = this.decoder.dequeueInputBuffer(10000);
        DebugLog.d(TAG_THREAD, "Input Buffer Index : " + inIndex);
        if (inIndex >= 0) {
            ByteBuffer buffer = this.decoder.getInputBuffer(inIndex);
            int sampleSize = (buffer != null) ? this.extractor.readSampleData(buffer, 0) : -1;
            if (sampleSize >= 0) {
                long time = (long) ((float) this.extractor.getSampleTime() / this.videoTimer.speed);
                this.decoder.queueInputBuffer(inIndex, 0, sampleSize, time, 0);
                this.extractor.advance();
            } else {
                DebugLog.d(TAG_THREAD, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
                this.decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                return true;
            }
        }
        return false;
    }

    /**
     * queueOutput
     *
     * @param info Buffer Information
     * @return is rendered
     */
    private boolean queueOutput(MediaCodec.BufferInfo info) {
        int outIndex = this.decoder.dequeueOutputBuffer(info, 10000);
        DebugLog.d(TAG_THREAD, "Output Buffer Index : " + outIndex);

        if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            DebugLog.d(TAG_THREAD, "INFO_OUTPUT_FORMAT_CHANGED");
        } else if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            DebugLog.d(TAG_THREAD, "INFO_TRY_AGAIN_LATER");
        } else {
            if (outIndex >= 0) {
                this.videoTimer.waitNext();

                this.decoder.releaseOutputBuffer(outIndex, true);
                this.videoTimer.setRenderTime();

                DebugLog.v(TAG_THREAD, "presentationTimeUs : " + info.presentationTimeUs);
                this.sendMessage((long) ((float) info.presentationTimeUs * this.videoTimer.speed));
                return true;
            }
        }
        return false;
    }

}
