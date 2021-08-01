package jp.mydns.dego.zanzo.Drawer;

import jp.mydns.dego.zanzo.Util.DebugLog;

public class DrawItemBase {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "DrawItemBase";

    public enum DrawType {
        Line,
        Rect,
        Round,
        Path,
    }

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
    private int color;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * DrawItemBase
     *
     * @param color color
     */
    protected DrawItemBase(int color) {
        this.color = color;
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * setColor
     *
     * @param color color
     */
    public void setColor(int color) {
        DebugLog.d(TAG, "setColor");
        this.color = color;
    }

    /**
     * getColor
     *
     * @return color
     */
    public int getColor() {
        DebugLog.d(TAG, "getColor");
        return this.color;
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
