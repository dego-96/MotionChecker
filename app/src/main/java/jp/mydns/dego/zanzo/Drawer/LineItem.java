package jp.mydns.dego.zanzo.Drawer;

import jp.mydns.dego.zanzo.Util.DebugLog;

public class LineItem extends DrawItemBase {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "LineItem";
    public static final int INDEX_START_X = 0;
    public static final int INDEX_START_Y = 1;
    public static final int INDEX_END_X = 2;
    public static final int INDEX_END_Y = 3;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final float[] points;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * LineItem
     *
     * @param color color
     */
    public LineItem(int color) {
        super(color);
        DebugLog.d(TAG, "LineItem");

        this.points = new float[4];
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * start
     *
     * @param x x
     * @param y y
     */
    public void start(float x, float y) {
        DebugLog.d(TAG, "start");
        this.points[INDEX_START_X] = x;
        this.points[INDEX_START_Y] = y;

        // タッチした瞬間に原点から直線が引かれないように終点も代入する
        this.points[INDEX_END_X] = x;
        this.points[INDEX_END_Y] = y;
    }

    /**
     * end
     *
     * @param x x
     * @param y y
     */
    public void end(float x, float y) {
        DebugLog.d(TAG, "end");
        this.points[INDEX_END_X] = x;
        this.points[INDEX_END_Y] = y;
    }

    /**
     * getPoints
     *
     * @return line points
     */
    public float[] getPoints() {
        return this.points;
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
