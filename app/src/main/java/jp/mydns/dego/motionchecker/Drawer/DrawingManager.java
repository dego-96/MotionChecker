package jp.mydns.dego.motionchecker.Drawer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;

import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.View.PaintViewController;

public class DrawingManager {
    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "DrawingManager";

//    public enum DrawType {
//        Line,
//        Rect,
//        Round,
//        Path,
//    }

    public enum ColorType {
        White,
        Red,
        Green,
        Blue,
        Yellow,
        Black,
    }

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final PaintViewController viewController;
    private ColorType currentColor;

    private final int[] colorTable = {
        Color.argb(0x50, 0xFF, 0xFF, 0xFF), /* white */
        Color.argb(0x50, 0xFF, 0x00, 0x00), /* red */
        Color.argb(0x50, 0x00, 0xFF, 0x00), /* green */
        Color.argb(0x50, 0x00, 0x00, 0xFF), /* blue */
        Color.argb(0x50, 0xFF, 0xFF, 0x00), /* yellow */
        Color.argb(0x50, 0x00, 0x00, 0x00), /* black */
    };

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * DrawingManager
     */
    public DrawingManager() {
        DebugLog.d(TAG, "DrawingManager");

        this.viewController = new PaintViewController();
        this.currentColor = ColorType.Red;
    }

// ---------------------------------------------------------------------------------------------
// public method
// ---------------------------------------------------------------------------------------------

    /**
     * setViews
     *
     * @param activity activity
     */
    public void setViews(Activity activity) {
        DebugLog.d(TAG, "setViews");
        this.viewController.setViews(activity);
    }

    /**
     * viewSetup
     *
     * @param surfaceViewSize video surface view size
     */
    public void viewSetup(Rect surfaceViewSize) {
        DebugLog.d(TAG, "viewSetup");

        this.viewController.setCaptureImage(surfaceViewSize);
    }

    /**
     * setColor
     *
     * @param type color type
     */
    public void setColor(ColorType type) {
        DebugLog.d(TAG, "setColor");
        this.currentColor = type;
    }

    /**
     * getColor
     *
     * @return color
     */
    public int getColor() {
        DebugLog.d(TAG, "getColor");
        return colorTable[this.currentColor.ordinal()];
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
