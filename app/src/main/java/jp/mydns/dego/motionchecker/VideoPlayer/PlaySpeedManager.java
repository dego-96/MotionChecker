package jp.mydns.dego.motionchecker.VideoPlayer;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class PlaySpeedManager {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "PlaySpeedManager";
    private static final float[] playSpeed = {
        0.1f,   // 1/10
        0.125f, // 1/8
        0.25f,  // 1/4
        0.5f,   // 1/2
        1.0f    // 1/1
    };

    public static final int SPEED_LEVEL_MAX = PlaySpeedManager.playSpeed.length - 1;
    public static final int SPEED_LEVEL_MIN = 0;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private int speedLevel;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * PlaySpeedManager
     */
    public PlaySpeedManager() {
        DebugLog.d(TAG, "PlaySpeedManager");
        this.init();
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     */
    public void init() {
        DebugLog.d(TAG, "init");
        this.speedLevel = SPEED_LEVEL_MAX;
    }

    /**
     * getSpeed
     *
     * @return video play speed
     */
    public float getSpeed() {
        DebugLog.d(TAG, "getSpeed");
        return PlaySpeedManager.playSpeed[this.speedLevel];
    }

    /**
     * getSpeedLevel
     *
     * @return speed level
     */
    public int getSpeedLevel() {
        return this.speedLevel;
    }

    /**
     * speedUp
     */
    public void speedUp() {
        DebugLog.d(TAG, "speedUp");
        if (this.speedLevel < PlaySpeedManager.SPEED_LEVEL_MAX) {
            this.speedLevel++;
        }
    }

    /**
     * speedDown
     */
    public void speedDown() {
        DebugLog.d(TAG, "speedDown");
        if (this.speedLevel > PlaySpeedManager.SPEED_LEVEL_MIN) {
            this.speedLevel--;
        }
    }
}
