package jp.mydns.dego.zanzo.Drawer;

import android.graphics.Rect;

import jp.mydns.dego.zanzo.Util.DebugLog;

public class RectItem extends DrawItemBase {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "RectItem";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final Rect rect;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * RectItem
     *
     * @param color color
     */
    public RectItem(int color) {
        super(color);
        DebugLog.d(TAG, "RectItem");

        this.rect = new Rect();
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
        this.rect.left = (int) x;
        this.rect.top = (int) y;

        // タッチした瞬間に原点から矩形が描画されないように終点も代入する
        this.rect.right = (int) x;
        this.rect.bottom = (int) y;
    }

    /**
     * end
     *
     * @param x x
     * @param y y
     */
    public void end(float x, float y) {
        DebugLog.d(TAG, "end");
        this.rect.right = (int) x;
        this.rect.bottom = (int) y;
    }

    /**
     * getRect
     *
     * @return rect
     */
    public Rect getRect() {
        return this.rect;
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
