package jp.mydns.dego.motionchecker.VideoPlayer;

import android.graphics.Bitmap;

import jp.mydns.dego.motionchecker.Util.BitmapHelper;
import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.Util.PixelHelper;

public class MotionGenerator {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MotionGenerator";
    private static final int SUPERPOSE_FRAME_NUM = 20;

    public enum Step {
        NONE,
        AVERAGE,
        DISTANCE,
        SUPERPOSE,
        END,
    }

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private int count;
    private int[] averagePixels;
    private int[] distanceMax;
    private int[] distant;
    private int[] resultPixels;
    private Step step;
    private int startTime;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * MotionGenerator
     */
    public MotionGenerator() {
        DebugLog.d(TAG, "MotionGenerator");

        init();
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * start
     */
    public void start(int time_ms) {
        DebugLog.d(TAG, "start (" + time_ms + ")");
        this.count = 0;
        this.averagePixels = null;
        this.distanceMax = null;
        this.distant = null;
        this.resultPixels = null;
        this.step = Step.AVERAGE;
        this.startTime = time_ms;
    }

    /**
     * clear
     */
    public void clear() {
        DebugLog.d(TAG, "clear");
        this.init();
    }

    /**
     * isStarted
     *
     * @return is started
     */
    public boolean isStarted() {
        return (this.startTime > 0);
    }

    /**
     * needNextFrame
     *
     * @return need next frame
     */
    public boolean needNextFrame() {
        return (
            (this.step == Step.AVERAGE || this.step == Step.DISTANCE || this.step == Step.SUPERPOSE) &&
                this.count < SUPERPOSE_FRAME_NUM);
    }

    /**
     * nextStep
     *
     * @return is next step
     */
    public boolean nextStep() {
        DebugLog.d(TAG, "nextStep (count:" + this.count + ", step:" + this.step + ")");
        if (this.count == SUPERPOSE_FRAME_NUM) {
            if (this.step == Step.AVERAGE) {
                this.step = Step.DISTANCE;
                this.count = 0;
            } else if (this.step == Step.DISTANCE) {
                this.step = Step.SUPERPOSE;
                this.count = 0;
            } else if (this.step == Step.SUPERPOSE) {
                this.step = Step.END;
                this.count = 0;
            } else {
                return false;
            }
            DebugLog.v(TAG, "step: " + this.step);
            return true;
        } else {
            return false;
        }
    }

    /**
     * isEnd
     *
     * @return is end
     */
    public boolean isEnd() {
        DebugLog.d(TAG, "isEnd");
        return (this.step == Step.END);
    }

    /**
     * getStartTime
     *
     * @return start time
     */
    public int getStartTime() {
        return this.startTime;
    }

    /**
     * superpose
     *
     * @param captureImage capture image bitmap
     */
    public void superpose(Bitmap captureImage) {
        DebugLog.d(TAG, "superpose : " + this.step + " (" + this.count + "/" + SUPERPOSE_FRAME_NUM + ")");

        if (this.count >= SUPERPOSE_FRAME_NUM) {
            DebugLog.e(TAG, "superpose count error.");
            return;
        }
        if (this.step == Step.NONE) {
            DebugLog.e(TAG, "invalidate step.");
            return;
        }

        int width = captureImage.getWidth();
        int height = captureImage.getHeight();
        int length = width * height;
        int[] pixels = new int[length];
        captureImage.getPixels(pixels, 0, width, 0, 0, width, height);

        if (this.step == Step.AVERAGE) {
            if (this.averagePixels == null) {
                this.initArrays(length);
            }

            for (int index = 0; index < length; index++) {
                this.averagePixels[index] = PixelHelper.average(
                    this.averagePixels[index],
                    pixels[index],
                    this.count
                );
            }
        } else if (this.step == Step.DISTANCE) {
            for (int index = 0; index < length; index++) {
                int distance = PixelHelper.distanceSq(this.averagePixels[index], pixels[index]);
                if (this.distanceMax[index] < distance) {
                    this.distanceMax[index] = distance;
                    this.distant[index] = this.count;
                }
            }
        } else if (this.step == Step.SUPERPOSE) {
            for (int index = 0; index < length; index++) {
                if (this.count == this.distant[index]) {
                    int pixel = PixelHelper.average(this.averagePixels[index], pixels[index]);
                    this.resultPixels[index] = pixel;
                }
            }
//        } else if (this.step == Step.END) {
//            // nothing to do.
        }
        this.count++;
    }

    /**
     * createBitmap
     *
     * @return bitmap
     */
    public Bitmap createBitmap(int width, int height) {
        DebugLog.d(TAG, "createBitmap");
        return BitmapHelper.createBitmapFromPixels(this.resultPixels, width, height);
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     */
    private void init() {
        DebugLog.d(TAG, "init");
        this.count = 0;
        this.averagePixels = null;
        this.distanceMax = null;
        this.distant = null;
        this.resultPixels = null;
        this.step = Step.NONE;
        this.startTime = -1;
    }

    /**
     * initArrays
     *
     * @param length length
     */
    private void initArrays(int length) {
        DebugLog.d(TAG, "initArrays");

        this.averagePixels = new int[length];
        this.distanceMax = new int[length];
        this.distant = new int[length];
        this.resultPixels = new int[length];

        for (int index = 0; index < length; index++) {
            this.averagePixels[index] = 0x00000000;
            this.distanceMax[index] = 0x00000000;
            this.distant[index] = 0x00000000;
            this.resultPixels[index] = 0x00000000;
        }
    }
}
