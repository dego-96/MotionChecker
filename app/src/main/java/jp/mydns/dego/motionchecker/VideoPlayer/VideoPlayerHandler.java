package jp.mydns.dego.motionchecker.VideoPlayer;

import android.os.Handler;
import android.os.Message;

import jp.mydns.dego.motionchecker.InstanceHolder;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoPlayerHandler extends Handler {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoPlayerHandler";

    static final String MESSAGE_PROGRESS_US = "MESSAGE_PROGRESS_US";
    static final String MESSAGE_FRAME_POSITION = "MESSAGE_FRAME_POSITION";
    static final String MESSAGE_STATUS = "MESSAGE_STATUS";


    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoPlayerHandler
     */
    VideoPlayerHandler() {
        super();
        DebugLog.d(TAG, "VideoPlayerHandler");
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * handleMessage
     *
     * @param message message
     */
    @Override
    public void handleMessage(Message message) {
        DebugLog.d(TAG, "handleMessage");

        VideoController videoController = InstanceHolder.getInstance().getVideoController();

        VideoDecoder.FramePosition position = (VideoDecoder.FramePosition) message.getData().getSerializable(MESSAGE_FRAME_POSITION);
        if (position != null) {
            DebugLog.v(TAG, "frame position : " + position.name());
            videoController.setViewEnable(position);

            long time_us = message.getData().getLong(MESSAGE_PROGRESS_US);
            DebugLog.v(TAG, "progress time (us) : " + time_us);
            if (time_us >= 0 && position != VideoDecoder.FramePosition.LAST) {
                videoController.setProgress((int) (time_us / 1000));
            }
        }

        VideoDecoder.DecoderStatus status = (VideoDecoder.DecoderStatus) message.getData().getSerializable(MESSAGE_STATUS);
        if (status != null) {
            DebugLog.d(TAG, "status : " + status.name());
            videoController.setVisibilities(status);
        }
    }
}
