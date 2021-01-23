package jp.mydns.dego.motionchecker.VideoPlayer;

import jp.mydns.dego.motionchecker.InstanceHolder;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoController {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
//    public static final double[] VIDEO_SPEEDS = {
//        1.000,  // 1/1
//        0.500,  // 1/2
//        0.250,  // 1/4
//        0.125,  // 1/8
//        0.100,  // 1/10
//    };

    private static final String TAG = "VideoController";
//    private static final int SPEED_SLOWEST = VIDEO_SPEEDS.length - 1;
//    private static final int SPEED_FASTEST = 0;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private String filePath;
//    private Thread videoThread;
//    private int speedLevel;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoController
     */
    public VideoController() {
        DebugLog.d(TAG, "VideoController");
        this.filePath = null;
//        this.videoThread = null;
//        this.speedLevel = 0;
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * setVideoPath
     *
     * @param path video file path
     */
    public void setVideoPath(String path) {
        DebugLog.d(TAG, "setVideoPath");
        this.filePath = path;
        DebugLog.v(TAG, "path : " + this.filePath);
    }

    /**
     * isStandby
     *
     * @return video controller is standby.
     */
    public boolean isStandby() {
        return (this.filePath != null && !"".equals(this.filePath));
    }

    /**
     * play
     */
    public void play() {
        DebugLog.d(TAG, "play");

    }

//    /**
//     * pause
//     */
//    public void pause() {
//        DebugLog.d(TAG, "pause");
//
//    }

    /**
     * stop
     */
    public void stop() {
        DebugLog.d(TAG, "play");

    }

//    /**
//     * seek
//     */
//    public void seek(long seekTo) {
//        DebugLog.d(TAG, "seek");
//
//    }

    /**
     * speedUp
     */
    public void speedUp() {
        DebugLog.d(TAG, "speedUp");

    }

    /**
     * speedDown
     */
    public void speedDown() {
        DebugLog.d(TAG, "speedDown");

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

}
