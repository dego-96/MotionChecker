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
    private static class VideoTimer {
        boolean isStarted;
        long startTime;
        long startTimeSys;
        long renderTime;
        long lastKeyTime;
        long count;
        float speed;

        VideoTimer() {
            this.isStarted = false;
            this.startTime = -1;
            this.startTimeSys = 0;
            this.renderTime = 0;
            this.lastKeyTime = 0;
            this.count = 0;
            this.speed = 1.0f;
        }

        void start(MediaCodec.BufferInfo info) {
            this.renderTime = 0;

            if (!this.isStarted) {
                this.startTime = info.presentationTimeUs;
                this.isStarted = true;
            }
            this.startTimeSys = System.nanoTime() / 1000;
            this.count = 0;

            DebugLog.v(TAG_THREAD, "-------- start time --------");
            DebugLog.v(TAG_THREAD, "system : " + this.startTimeSys);
            DebugLog.v(TAG_THREAD, "video  : " + this.startTime);
            DebugLog.v(TAG_THREAD, "----------------------------");
        }

        void pause() {
            this.isStarted = false;
        }

        void setRenderTime(MediaCodec.BufferInfo info) {
            this.renderTime = info.presentationTimeUs;
            this.count++;

            if ((info.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                this.lastKeyTime = this.renderTime;
            }
        }

        boolean waitNext() {
            DebugLog.d(TAG, "waitNext");
            if (this.startTime < 0) {
                return true;
            }
            long elapsed;
            long waitTime = this.renderTime - this.startTime;
            do {
                // 再生速度に合わせてシステム時間の経過スピードを変える
                elapsed = (long) ((System.nanoTime() / 1000.0f - (float) this.startTimeSys) * this.speed);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                    return false;
                }
            } while (elapsed < waitTime);
            DebugLog.v(TAG, "end wait");
            return true;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // public constant values
    // ---------------------------------------------------------------------------------------------
    /* video status */
    public enum DecoderStatus {
        INIT,
        PAUSED,
        PLAYING,
        SEEKING,
        NEXT_FRAME,
        PREVIOUS_FRAME
    }

    public enum FramePosition {
        INIT,
        FIRST,
        MID,
        LAST,
        RESTART
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
    private DecoderStatus status;
    private FramePosition position;
    private OnVideoChangeListener videoListener;

    private Surface surface;

    private MediaCodec decoder;
    private MediaExtractor extractor;
    private VideoTimer videoTimer;
    private final VideoPlayerHandler handler;
    private boolean isDecoding;
    private int frameRate;
    private long duration;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoDecoder
     */
    public VideoDecoder() {
        DebugLog.d(TAG, "VideoDecoder");
        this.status = DecoderStatus.INIT;
        this.position = FramePosition.INIT;
        this.handler = new VideoPlayerHandler();
    }

    // ---------------------------------------------------------------------------------------------
    // package private method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     *
     * @param video   video
     * @param surface surface
     * @return initialization result
     */
    boolean init(Video video, Surface surface) {
        DebugLog.d(TAG, "init");

        this.setStatus(DecoderStatus.INIT, false);
        this.surface = surface;

        return this.prepare(video, false);
    }

    /**
     * prepare
     *
     * @param video   video
     * @param restart restart
     * @return prepare result
     */
    public boolean prepare(Video video, boolean restart) {
        DebugLog.d(TAG, "prepare");
        this.extractor = new MediaExtractor();
        try {
            Context context = InstanceHolder.getInstance().getApplicationContext();
            this.extractor.setDataSource(context, video.getUri(), null);
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

                this.duration = format.getLong(MediaFormat.KEY_DURATION);
                this.videoListener.onDurationChanged((int) (this.duration / 1000));

                this.frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE);

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
        } else {
            return false;
        }

        this.videoTimer = new VideoTimer();
        this.isDecoding = false;

        if (restart) {
            this.position = FramePosition.RESTART;
        } else {
            this.position = FramePosition.INIT;
        }

        this.setStatus(DecoderStatus.PAUSED, true);
        return true;
    }

    /**
     * getStatus
     *
     * @return video status
     */
    DecoderStatus getStatus() {
        return this.status;
    }

    /**
     * FramePosition
     *
     * @return frame position
     */
    FramePosition getFramePosition() {
        return this.position;
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

    /**
     * seekTo
     *
     * @param progress progress
     */
    void seekTo(int progress) {
        DebugLog.d(TAG, "seekTo(" + progress + ")");

        this.setStatus(DecoderStatus.SEEKING, true);
        this.decoder.flush();
        this.extractor.seekTo(progress * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
    }

    /**
     * next
     */
    void next() {
        DebugLog.d(TAG, "next");
        this.setStatus(DecoderStatus.NEXT_FRAME, false);
    }

    /**
     * previous
     */
    void previous() {
        DebugLog.d(TAG, "previous");
        this.setStatus(DecoderStatus.PREVIOUS_FRAME, false);

        this.decoder.flush();
        if (this.videoTimer.lastKeyTime < 1 ||
            this.videoTimer.lastKeyTime < this.videoTimer.renderTime) {
            this.extractor.seekTo(this.videoTimer.lastKeyTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        } else {
            this.extractor.seekTo(this.videoTimer.lastKeyTime - 1, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        }
    }

    /**
     * setOnVideoChangeListener
     *
     * @param listener video change listener
     */
    void setOnVideoChangeListener(OnVideoChangeListener listener) {
        DebugLog.d(TAG, "setOnVideoChangeListener");
        this.videoListener = listener;
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

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
                if (this.position == FramePosition.INIT) {
                    this.nextFrame();
                } else {
                    this.play();
                }
                break;
            case NEXT_FRAME:
            case SEEKING:
                this.nextFrame();
                break;
            case PREVIOUS_FRAME:
                this.previousFrame();
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

    /**
     * setStatus
     *
     * @param status status
     */
    private void setStatus(DecoderStatus status, boolean visibility) {
        DebugLog.d(TAG, "setStatus (" + this.status.name() + " => " + status.name() + ")");
        this.status = status;

        if (visibility) {
            Context context = InstanceHolder.getInstance();
            if (Thread.currentThread().equals(context.getMainLooper().getThread())) {
                this.videoListener.setVisibilities(status);
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
        DebugLog.d(TAG_THREAD, "sendMessage(" + time_us + ")");

        Bundle bundle = new Bundle();
        bundle.putLong(VideoPlayerHandler.MESSAGE_PROGRESS_US, time_us);
        bundle.putSerializable(VideoPlayerHandler.MESSAGE_FRAME_POSITION, this.position);
        Message message = Message.obtain();
        message.setData(bundle);
        this.handler.sendMessage(message);
    }

    /**
     * play
     */
    private void play() {
        DebugLog.d(TAG_THREAD, "play");
        this.setStatus(DecoderStatus.PLAYING, true);
        this.isDecoding = true;

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean isEos = false;

        while (!Thread.currentThread().isInterrupted() && this.isDecoding) {
            if (!isEos) {
                isEos = this.queueInput();
            }

            this.queueOutput(info);

        }
        this.setStatus(DecoderStatus.PAUSED, true);
        this.videoTimer.pause();
    }

    /**
     * nextFrame
     */
    private void nextFrame() {
        DebugLog.d(TAG_THREAD, "nextFrame");
        this.isDecoding = true;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean isEos = false;

        while (!Thread.currentThread().isInterrupted() && this.isDecoding) {
            if (!isEos) {
                isEos = this.queueInput();
            }

            if (this.queueOutput(info)) {
                this.setStatus(DecoderStatus.PAUSED, true);
                this.videoTimer.pause();
                return;
            }
        }
    }

    /**
     * previousFrame
     */
    private void previousFrame() {
        DebugLog.d(TAG_THREAD, "previousFrame");

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean isEos = false;

        long targetTime;
        if (this.position == FramePosition.LAST) {
            targetTime = this.duration - (long) (1500000 / this.frameRate);
        } else {
            targetTime = this.videoTimer.renderTime - (long) (1500000 / this.frameRate);
        }
        DebugLog.v(TAG_THREAD, "target time : " + targetTime);

        while (true) {
            if (isEos) {
                DebugLog.e(TAG_THREAD, "End of stream when move to previous frame");
                return;
            } else {
                isEos = this.queueInput();
            }

            if (this.queueOutput(info, targetTime)) {
                this.setStatus(DecoderStatus.PAUSED, true);
                this.videoTimer.pause();
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
        DebugLog.d(TAG, "queueOutput");
        int outIndex = this.decoder.dequeueOutputBuffer(info, 10000);
        DebugLog.d(TAG_THREAD, "Output Buffer Index : " + outIndex);

        this.videoTimer.start(info);

        if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            DebugLog.d(TAG_THREAD, "INFO_OUTPUT_FORMAT_CHANGED");
        } else if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            DebugLog.d(TAG_THREAD, "INFO_TRY_AGAIN_LATER");
        } else {
            if (outIndex >= 0) {
                if (!this.videoTimer.waitNext()) {
                    this.isDecoding = false;
                    return false;
                }

                this.decoder.releaseOutputBuffer(outIndex, true);
                this.videoTimer.setRenderTime(info);

                if (this.position == FramePosition.INIT ||
                    this.position == FramePosition.RESTART) {
                    this.position = FramePosition.FIRST;
                } else if (this.position == FramePosition.FIRST ||
                    this.position == FramePosition.LAST) {
                    this.position = FramePosition.MID;
                }

                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    DebugLog.d(TAG_THREAD, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                    this.position = FramePosition.LAST;
                    this.setStatus(DecoderStatus.PAUSED, true);
                    this.isDecoding = false;
                }

                DebugLog.v(TAG_THREAD, "presentationTimeUs : " + info.presentationTimeUs);
                this.sendMessage((long) ((float) info.presentationTimeUs * this.videoTimer.speed));

                return true;
            }
        }
        return false;
    }

    /**
     * queueOutput
     *
     * @param info   buffer info
     * @param target render target time
     * @return is rendered
     */
    private boolean queueOutput(MediaCodec.BufferInfo info, long target) {
        DebugLog.d(TAG, "queueOutput");
        int outIndex = this.decoder.dequeueOutputBuffer(info, 10000);
        DebugLog.d(TAG_THREAD, "Output Buffer Index : " + outIndex);

        if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            DebugLog.d(TAG_THREAD, "INFO_OUTPUT_FORMAT_CHANGED");
        } else if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            DebugLog.d(TAG_THREAD, "INFO_TRY_AGAIN_LATER");
        } else {
            if (outIndex >= 0) {
                if (info.presentationTimeUs < target) {
                    this.decoder.releaseOutputBuffer(outIndex, false);
                } else {
                    this.decoder.releaseOutputBuffer(outIndex, true);
                    DebugLog.v(TAG_THREAD, "previous frame rendered.");

                    if (this.position == FramePosition.LAST) {
                        this.position = FramePosition.MID;
                    } else if (info.presentationTimeUs < (1000 / this.frameRate)) {
                        this.position = FramePosition.FIRST;
                    }

                    this.videoTimer.setRenderTime(info);
                    DebugLog.v(TAG_THREAD, "presentationTimeUs : " + info.presentationTimeUs);
                    this.sendMessage((long) ((float) info.presentationTimeUs * this.videoTimer.speed));

                    return true;
                }
            }
        }
        return false;
    }
}
