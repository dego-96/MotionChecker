package jp.mydns.dego.motionchecker.Drawer;

import android.graphics.Path;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class PathItem extends DrawItemBase {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "PathItem";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final Path path;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * PathItem
     *
     * @param color color
     */
    public PathItem(int color) {
        super(color);
        DebugLog.d(TAG, "PathItem");

        this.path = new Path();
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * moveTo
     *
     * @param x x
     * @param y y
     */
    public void moveTo(float x, float y) {
        DebugLog.d(TAG, "moveTo");
        this.path.moveTo(x, y);
    }

    /**
     * lineTo
     *
     * @param x x
     * @param y y
     */
    public void lineTo(float x, float y) {
        DebugLog.d(TAG, "lineTo");
        this.path.lineTo(x, y);
    }

    /**
     * getPath
     *
     * @return path
     */
    public Path getPath() {
        DebugLog.d(TAG, "getPath");
        return this.path;
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
