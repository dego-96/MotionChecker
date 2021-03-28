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
     * @param speed   video speed
     * @return initialization result
     */
    boolean init(Video video, Surface surface, float speed) {
        DebugLog.d(TAG, "init");

        this.setStatus(DecoderStatus.INIT, false);
        this.surface = surface;

        return this.prepare(video, speed, false);
    }

    /**
     * prepare
     *
     * @param video   video
     * @param speed   video speed
     * @param restart restart
     * @return prepare result
     */
    public boolean prepare(Video video, float speed, boolean restart) {
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

        this.videoTimer = new VideoTimer(speed);

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
        this.videoTimer.setSpeed(speed);
    }

    /**
     * seekTo
     *
     * @param progress progress
     */
    void seekTo(int progress) {
        DebugLog.d(TAG, "seekTo(" + progress + ")");

        if (progress == 0) {
            this.position = FramePosition.FIRST;
        } else if (progress == this.duration) {
            this.position = FramePosition.LAST;
        } else {
            this.position = FramePosition.MID;
        }

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
        if (this.videoTimer.getKeyFrameTime() < 1 ||
            this.videoTimer.getKeyFrameTime() < this.videoTimer.getRenderTime()) {
            this.extractor.seekTo(this.videoTimer.getKeyFrameTime(), MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        } else {
            this.extractor.seekTo(this.videoTimer.getKeyFrameTime() - 1000, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
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
                this.play(this.position == FramePosition.INIT);
                break;
            case NEXT_FRAME:
            case SEEKING:
                this.play(true);
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
    private void play(boolean oneFrame) {
        DebugLog.d(TAG_THREAD, "play");
        if (!oneFrame) {
            this.setStatus(DecoderStatus.PLAYING, true);
        }

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean isEos = false;

        this.isDecoding = true;
        while (!Thread.currentThread().isInterrupted() && this.isDecoding) {
            if (!isEos) {
                isEos = this.queueInput();
            }

            boolean render = this.queueOutput(info);

            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                DebugLog.d(TAG_THREAD, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                this.position = FramePosition.LAST;
                this.isDecoding = false;
            }

            if (oneFrame && render) {
                this.videoTimer.timerStop();
                this.isDecoding = false;
            }

            DebugLog.v(TAG_THREAD, "presentationTimeUs : " + info.presentationTimeUs);
            this.sendMessage((long) ((float) info.presentationTimeUs * this.videoTimer.getSpeed()));
        }
        this.setStatus(DecoderStatus.PAUSED, true);
        this.videoTimer.timerStop();
        this.isDecoding = false;
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
            targetTime = this.videoTimer.getRenderTime() - (long) (1500000 / this.frameRate);
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
                this.videoTimer.timerStop();
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
                long time = (long) ((float) this.extractor.getSampleTime() / this.videoTimer.getSpeed());
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
        DebugLog.d(TAG_THREAD, "queueOutput");
        int outIndex = this.decoder.dequeueOutputBuffer(info, 10000);
        DebugLog.d(TAG_THREAD, "Output Buffer Index : " + outIndex);

        this.videoTimer.timerStart(info);

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
                    this.sendMessage((long) ((float) info.presentationTimeUs * this.videoTimer.getSpeed()));

                    return true;
                }
            }
        }
        return false;
    }
}
