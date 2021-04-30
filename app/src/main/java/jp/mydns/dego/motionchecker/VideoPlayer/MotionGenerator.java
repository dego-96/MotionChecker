package jp.mydns.dego.motionchecker.VideoPlayer;

import android.graphics.Bitmap;

import jp.mydns.dego.motionchecker.Util.BitmapHelper;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class MotionGenerator {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MotionGenerator";
    private static final int SUPERPOSE_FRAME_NUM = 10;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private int superposeCount;
    private int[] averagePixels;
    private int width;
    private int height;
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
        DebugLog.d(TAG, "start");
        DebugLog.v(TAG, "start time : " + time_ms);
        this.superposeCount = 0;
        this.averagePixels = null;
        this.width = 0;
        this.height = 0;
        this.startTime = time_ms;
    }

    /**
     * reset
     */
    public void reset() {
        DebugLog.d(TAG, "reset");
        this.init();
    }

    /**
     * isStarted
     *
     * @return is started
     */
    public boolean isStarted() {
        return (
            this.averagePixels != null &&
                this.superposeCount > 0 &&
                this.startTime <= 0
        );
    }

    /**
     * needNextFrame
     *
     * @return need next frame
     */
    public boolean needNextFrame() {
        return (this.averagePixels != null && this.superposeCount < SUPERPOSE_FRAME_NUM);
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
        DebugLog.d(TAG, "superpose");

        this.width = captureImage.getWidth();
        this.height = captureImage.getHeight();
        int[] pixels = new int[width * height];
        captureImage.getPixels(pixels, 0, this.width, 0, 0, this.width, this.height);

        int index;
        if (this.averagePixels == null) {
            this.averagePixels = new int[this.width * this.height];
            for (index = 0; index < this.width * this.height; index++) {
                this.averagePixels[index] = 0x00000000;
            }
        }

        for (index = 0; index < this.width * this.height; index++) {
            int pixel = this.averagePixels[index];
            this.averagePixels[index] = this.calcAveragePixel(pixel, pixels[index], this.superposeCount);
        }

        this.superposeCount++;
    }

    /**
     * createBitmap
     *
     * @return bitmap
     */
    public Bitmap createBitmap() {
        DebugLog.d(TAG, "createBitmap");
        return BitmapHelper.createBitmapFromPixels(this.averagePixels, this.width, this.height);
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     */
    private void init() {
        DebugLog.d(TAG, "init");
        this.superposeCount = 0;
        this.averagePixels = null;
        this.width = 0;
        this.height = 0;
        this.startTime = 0;
    }

    /**
     * calcAveragePixel
     *
     * @param basePixel      base pixel
     * @param newPixel       new pixel
     * @param superposeCount superpose count
     * @return average pixel
     */
    private int calcAveragePixel(int basePixel, int newPixel, int superposeCount) {
        int baseR = (basePixel & 0x00FF0000) >> 16;
        int baseG = (basePixel & 0x0000FF00) >> 8;
        int baseB = (basePixel & 0x000000FF);

        int newR = (newPixel & 0x00FF0000) >> 16;
        int newG = (newPixel & 0x0000FF00) >> 8;
        int newB = (newPixel & 0x000000FF);

        int r = ((baseR * superposeCount + newR) / (superposeCount + 1));
        int g = ((baseG * superposeCount + newG) / (superposeCount + 1));
        int b = ((baseB * superposeCount + newB) / (superposeCount + 1));

        return (0xFF000000) | ((int) r << 16) | ((int) g << 8) | ((int) b);
    }
}
