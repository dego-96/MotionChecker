package jp.mydns.dego.motionchecker.Motion;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import jp.mydns.dego.motionchecker.R;
import jp.mydns.dego.motionchecker.Util.BitmapHelper;
import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.Util.PixelHelper;

public class MotionGenerator {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MotionGenerator";
    private static final int DEFAULT_FRAME_NUM = 20;
    private static final int FRAME_NUM_MIN = 5;
    private static final int FRAME_NUM_MAX = 60;

    public static final int FRAME_NUM_OFFSET = 5;

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
    private Activity activity;
    private int count;
    private int[] averagePixels;
    private int[] distanceMax;
    private int[] distant;
    private int[] resultPixels;
    private Step step;
    private int startTime;
    private int frameNum;

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
                this.count < this.frameNum);
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
        DebugLog.d(TAG, "init (private)");
        this.count = 0;
        this.averagePixels = null;
        this.distanceMax = null;
        this.distant = null;
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
