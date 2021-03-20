package jp.mydns.dego.motionchecker.VideoPlayer;

import android.media.MediaCodec;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoTimer {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoTimer";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private long startTime;
    private long startTimeSys;
    private long renderTime;
    private long lastKeyTime;
    private float speed;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    public VideoTimer(float speed) {
        this.startTime = -1;
        this.startTimeSys = 0;
        this.renderTime = 0;
        this.lastKeyTime = 0;
        this.speed = speed;
    }

    // ---------------------------------------------------------------------------------------------
    // package private method
    // ---------------------------------------------------------------------------------------------

    /**
     * setSpeed
     *
     * @param speed video speed
     */
    void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * getSpeed
     *
     * @return video speed
     */
    float getSpeed() {
        return this.speed;
    }

    /**
     * getRenderTime
     *
     * @return last render time
     */
    long getRenderTime() {
        return this.renderTime;
    }

    /**
     * getKeyFrameTime
     *
     * @return last keyframe time
     */
    long getKeyFrameTime() {
        return this.lastKeyTime;
    }

    /**
     * timerStart
     *
     * @param info buffer info
     */
    void timerStart(MediaCodec.BufferInfo info) {
        if (this.startTimeSys == 0) {
            this.renderTime = 0;
            this.startTime = info.presentationTimeUs;
            this.startTimeSys = System.nanoTime() / 1000;

            DebugLog.v(TAG, "-------- start time --------");
            DebugLog.v(TAG, "system : " + this.startTimeSys);
            DebugLog.v(TAG, "video  : " + this.startTime);
            DebugLog.v(TAG, "----------------------------");
        }
    }

    /**
     * timerStop
     */
    void timerStop() {
        this.startTimeSys = 0;
    }

    /**
     * setRenderTime
     *
     * @param info buffer info
     */
    void setRenderTime(MediaCodec.BufferInfo info) {
        this.renderTime = info.presentationTimeUs;

        if ((info.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
            this.lastKeyTime = this.renderTime;
        }
    }

    /**
     * waitNext
     *
     * @return is waited until the end
     */
    boolean waitNext() {
        DebugLog.v(TAG, "waitNext");

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
