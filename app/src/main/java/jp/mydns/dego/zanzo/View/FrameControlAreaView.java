package jp.mydns.dego.zanzo.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import jp.mydns.dego.zanzo.InstanceHolder;
import jp.mydns.dego.zanzo.Util.DebugLog;
import jp.mydns.dego.zanzo.VideoPlayer.VideoController;

public class FrameControlAreaView extends AppCompatImageView {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "FrameControlAreaView";
    private static final int TOUCH_DIFF_SIZE = 50;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private float startX;
    private int lastDiffLevel;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * FrameControlAreaView
     *
     * @param context context
     */
    public FrameControlAreaView(@NonNull Context context) {
        super(context);
        DebugLog.d(TAG, "FrameControlAreaView 1");
    }

    /**
     * FrameControlAreaView
     *
     * @param context context
     * @param attrs   attribute set
     */
    public FrameControlAreaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DebugLog.d(TAG, "FrameControlAreaView 2");
    }

    /**
     * FrameControlAreaView
     *
     * @param context      context
     * @param attrs        attribute set
     * @param defStyleAttr default style attribute
     */
    public FrameControlAreaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DebugLog.d(TAG, "FrameControlAreaView 3");
    }
    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * onTouchEvent
     *
     * @param event motion event
     * @return touch event result
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DebugLog.d(TAG, "onTouchEvent");

        if (event.getPointerCount() != 1) {
            DebugLog.i(TAG, "Multi touch is not support.");
            return false;
        }

        VideoController videoController = InstanceHolder.getInstance().getVideoController();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.startX = event.getX();
            this.lastDiffLevel = 0;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float diff = event.getX() - this.startX;
            int level = (int) (diff / TOUCH_DIFF_SIZE);
            if (this.lastDiffLevel < level) {
                videoController.nextFrame();
            } else if (this.lastDiffLevel > level) {
                videoController.previousFrame();
            }
            this.lastDiffLevel = level;
        }
        this.performClick();
        return true;
    }

    /**
     * performClick
     *
     * @return True there was an assigned OnClickListener that was called, false
     * otherwise is returned.
     */
    @Override
    public boolean performClick() {
        // WARNING対策
        return super.performClick();
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

}
