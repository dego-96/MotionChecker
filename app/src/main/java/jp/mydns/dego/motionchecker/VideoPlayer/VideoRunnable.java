package jp.mydns.dego.motionchecker.VideoPlayer;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoRunnable implements Runnable {

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
        FORWARD,
        BACKWARD
    }

    // ---------------------------------------------------------------------------------------------
    // private constant values
    // ---------------------------------------------------------------------------------------------
//    private static VideoRunnable instance;

    private static final String TAG = "VideoRunnable";
    private static final String TAG_THREAD = "VideoThread";
//    private static final String MIME_VIDEO = "video/";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private STATUS videoStatus;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoRunnable
     */
    public VideoRunnable() {
        DebugLog.d(TAG, "VideoRunnable");
//        this.handler = null;
        this.videoStatus = STATUS.INIT;
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
            case VIDEO_END:
//                this.play();
                break;
            case VIDEO_SELECTED:
            case FORWARD:
            case SEEKING:
//                this.oneFrame();
                break;
            case BACKWARD:
//                this.toPreviousFrame();
                break;
        }
    }

    /**
     * getStatus
     *
     * @return video status
     */
    private STATUS getStatus() {
        return this.videoStatus;
    }

}
