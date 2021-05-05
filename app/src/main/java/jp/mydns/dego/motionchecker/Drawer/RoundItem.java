package jp.mydns.dego.motionchecker.Drawer;

import android.graphics.RectF;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class RoundItem extends DrawItemBase {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "RoundItem";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final RectF oval;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * RoundItem
     *
     * @param color color
     */
    public RoundItem(int color) {
        super(color);
        DebugLog.d(TAG, "RoundItem");

        this.oval = new RectF();
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
        this.oval.left = x;
        this.oval.top = y;

        // タッチした瞬間に原点から楕円が描画されないように終点も代入する
        this.oval.right = x;
        this.oval.bottom = y;
    }

    /**
     * end
     *
     * @param x x
     * @param y y
     */
    public void end(float x, float y) {
        DebugLog.d(TAG, "end");
        this.oval.right = (int) x;
        this.oval.bottom = (int) y;
    }

    /**
     * getRound
     *
     * @return rect
     */
    public RectF getRound() {
        return this.oval;
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
