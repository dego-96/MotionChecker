package jp.mydns.dego.motionchecker.View;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class CaptureImageView extends AppCompatImageView {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "CaptureImageView";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * CaptureImageView
     *
     * @param context context
     */
    public CaptureImageView(Context context) {
        super(context);
        init();
    }

    /**
     * CaptureImageView
     *
     * @param context context
     * @param attrs   attribute set
     */
    public CaptureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * CaptureImageView
     *
     * @param context      context
     * @param attrs        attribute set
     * @param defStyleAttr default style attribute
     */
    public CaptureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * layout
     *
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    @Override
    public void layout(int l, int t, int r, int b) {
        DebugLog.d(TAG, "layout(" + l + ", " + t + ", " + r + ", " + b + ")");
    }

    /**
     * layout
     *
     * @param rect layout
     */
    public void layout(Rect rect) {
        DebugLog.d(TAG, "layout");
        super.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     */
    private void init() {
        DebugLog.d(TAG, "init");
    }
}
