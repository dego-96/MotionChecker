package jp.mydns.dego.zanzo.Motion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;

import jp.mydns.dego.zanzo.R;
import jp.mydns.dego.zanzo.Util.BitmapHelper;
import jp.mydns.dego.zanzo.Util.DebugLog;
import jp.mydns.dego.zanzo.Util.PixelHelper;

public class MotionGenerator {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MotionGenerator";
    private static final int DEFAULT_FRAME_NUM = 20;
    private static final int FRAME_NUM_MIN = 5;
    private static final int FRAME_NUM_MAX = 60;

    public static final int FRAME_NUM_OFFSET = 5;
    public static final String INTENT_LAST_SAVED_IMAGE = "LastMotionImage";

    private static final int COLOR_VARIANCE_THRESHOLD_SQ = 100 * 100;
    private static final int COLOR_DISTANCE_THRESHOLD_SQ = 20 * 20;

    public enum Step {
        NONE,
        AVERAGE,
        VARIANCE,
        BACKGROUND,
        SUPERPOSE,
        END,
    }

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private Activity activity;
    private int count;
    private int[] average;
    private long[] variance;
    private int[] background;
    private int[] sumR;
    private int[] sumG;
    private int[] sumB;
    private int[] resultPixels;
    private Step step;
    private int startTime;
    private int frameNum;

//    private int[][] frontPixels;

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
     * init
     *
     * @param activity activity
     * @param listener frame count seek bar change listener
     */
    public void init(@NonNull Activity activity, SeekBar.OnSeekBarChangeListener listener) {
        DebugLog.d(TAG, "init");
        this.init();

        this.activity = activity;
        this.setFrameNumText(DEFAULT_FRAME_NUM);

        SeekBar seekBar = (SeekBar) activity.findViewById(R.id.seek_bar_frame_count);
        seekBar.setOnSeekBarChangeListener(listener);
        seekBar.setMax(FRAME_NUM_MAX - FRAME_NUM_OFFSET);
        seekBar.setProgress(DEFAULT_FRAME_NUM - FRAME_NUM_OFFSET);
    }

    /**
     * setFrameNum
     *
     * @param frameNum video frame number
     */
    public void setFrameNum(int frameNum) {
        DebugLog.d(TAG, "setFrameNum");
        if (FRAME_NUM_MIN <= frameNum && frameNum <= FRAME_NUM_MAX) {
            this.frameNum = frameNum;
        } else {
            this.frameNum = DEFAULT_FRAME_NUM;
        }

        this.setFrameNumText(frameNum);
    }

    /**
     * start
     *
     * @param time_ms time (millis)
     */
    public void start(int time_ms) {
        DebugLog.d(TAG, "start (" + time_ms + ")");
        this.count = 0;
        this.average = null;
        this.variance = null;
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
            (this.step == Step.AVERAGE
                || this.step == Step.VARIANCE
                || this.step == Step.BACKGROUND
                || this.step == Step.SUPERPOSE
            )
                && this.count < this.frameNum
        );
    }

    /**
     * nextStep
     *
     * @return is next step
     */
    public boolean nextStep() {
        DebugLog.d(TAG, "nextStep (count:" + this.count + ", step:" + this.step + ")");
        if (this.count == this.frameNum) {
            if (this.step == Step.AVERAGE) {
                this.step = Step.VARIANCE;
                this.count = 0;
            } else if (this.step == Step.VARIANCE) {
                this.step = Step.BACKGROUND;
                this.count = 0;
            } else if (this.step == Step.BACKGROUND) {
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
        DebugLog.d(TAG, "superpose : " + this.step + " (" + this.count + "/" + this.frameNum + ")");

        if (this.count >= this.frameNum) {
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

        if (this.step == Step.AVERAGE) {    // 平均を計算
            if (this.average == null) {
                this.initArrays(length);
            }

            for (int index = 0; index < length; index++) {
                this.average[index] = PixelHelper.average(
                    this.average[index],
                    pixels[index],
                    this.count
                );
            }
        } else if (this.step == Step.VARIANCE) {   // 分散を計算
            for (int index = 0; index < length; index++) {
                this.variance[index] += PixelHelper.distanceSq(this.average[index], pixels[index]);
            }
            if (this.count == this.frameNum - 1) {
                for (int index = 0; index < length; index++) {
                    this.variance[index] = this.variance[index] / this.frameNum;
                }
            }
        } else if (this.step == Step.BACKGROUND) {  // 背景画像
            for (int index = 0; index < length; index++) {
                int distanceSq = PixelHelper.distanceSq(this.average[index], pixels[index]);
                if (distanceSq < this.variance[index]) {
                    this.sumR[index] += PixelHelper.getR(pixels[index]);
                    this.sumG[index] += PixelHelper.getG(pixels[index]);
                    this.sumB[index] += PixelHelper.getB(pixels[index]);
                    this.background[index]++;
                }
            }
            if (this.count == this.frameNum - 1) {
                for (int index = 0; index < length; index++) {
                    if (this.background[index] > 0) {
                        int r = this.sumR[index] / this.background[index];
                        int g = this.sumG[index] / this.background[index];
                        int b = this.sumB[index] / this.background[index];
                        this.background[index] = (0xFF000000) | (r << 16) | (g << 8) | (b);
                    } else {
                        this.background[index] = pixels[index];
                    }
                }
                System.arraycopy(this.background, 0, this.resultPixels, 0, this.resultPixels.length);
            }
        } else if (this.step == Step.SUPERPOSE) {
            for (int index = 0; index < length; index++) {
                if (this.variance[index] < COLOR_VARIANCE_THRESHOLD_SQ) {
                    // ばらつきが少ないところは背景or前景
                    int distanceSq = PixelHelper.distanceSq(this.background[index], pixels[index]);
                    if (distanceSq > COLOR_DISTANCE_THRESHOLD_SQ) {
//                        this.resultPixels[index] = 0xFFFF0000;  // 前景
                        this.resultPixels[index] = pixels[index];
                    }
                } else {
                    // ばらつきが大きいところは平均値を使用
                    this.resultPixels[index] = this.average[index];
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

    /**
     * showMotionImage
     */
    public void showMotionImage() {
        DebugLog.d(TAG, "showMotionImage");

        Intent intent = new Intent(this.activity, ImageViewerActivity.class);
        File file = BitmapHelper.getLastSavedFile();
        DebugLog.v(TAG, "filename: " + file.getName());
        intent.putExtra(INTENT_LAST_SAVED_IMAGE, file.getName());

        this.activity.startActivity(intent);
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     */
    private void init() {
        DebugLog.d(TAG, "init (private)");
        this.count = 0;
        this.average = null;
        this.variance = null;
        this.resultPixels = null;
        this.step = Step.NONE;
        this.startTime = -1;
        this.frameNum = DEFAULT_FRAME_NUM;
    }

    /**
     * initArrays
     *
     * @param length length
     */
    private void initArrays(int length) {
        DebugLog.d(TAG, "initArrays");

        this.average = new int[length];
        this.variance = new long[length];
        this.background = new int[length];
        this.sumR = new int[length];
        this.sumG = new int[length];
        this.sumB = new int[length];
        this.resultPixels = new int[length];

        for (int index = 0; index < length; index++) {
            this.average[index] = 0x00000000;
            this.variance[index] = 0;
            this.background[index] = 0;
            this.sumR[index] = 0;
            this.sumG[index] = 0;
            this.sumB[index] = 0;
            this.resultPixels[index] = 0x00000000;
        }
    }

    /**
     * setFrameNumText
     */
    private void setFrameNumText(int frameNum) {
        DebugLog.d(TAG, "setFrameNumText");

        if (this.activity == null) {
            DebugLog.e(TAG, "activity is null.");
        }

        TextView textView = (TextView) this.activity.findViewById(R.id.text_motion_frame_count);
        String textFrameNum = this.activity.getString(R.string.text_frame_count) + frameNum;
        textView.setText(textFrameNum);
    }
}
