package jp.mydns.dego.motionchecker.View;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;

import jp.mydns.dego.motionchecker.R;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class PaintViewController {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "PaintViewController";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private SparseArray<View> views;
    private final int[] viewIdList = {
        R.id.draw_surface_view,
        R.id.button_player,
        R.id.button_paint_undo,
        R.id.button_paint_redo,
        R.id.button_paint_erase,
        R.id.button_paint_grid,
        R.id.button_paint_line,
        R.id.button_paint_rect,
        R.id.button_paint_round,
        R.id.button_paint_path,
        R.id.button_color_white,
        R.id.button_color_red,
        R.id.button_color_green,
        R.id.button_color_blue,
        R.id.button_color_yellow,
        R.id.button_color_black,
    };

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * PaintViewController
     */
    public PaintViewController() {
        DebugLog.d(TAG, "PaintViewController");

        this.views = null;
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

        // Activityが再生成された時はViewも再取得する
        // ここで更新しないとSurfaceViewのcreateSurfaceが呼び出されない
        this.views = new SparseArray<>();
        for (int id : this.viewIdList) {
            DebugLog.v(TAG, "view id : " + id);
            View view = activity.findViewById(id);
            if (view != null) {
                this.views.put(id, view);
            } else {
                DebugLog.e(TAG, "Can not get view. (" + id + ")");
            }
        }
    }

    /**
     * changeDrawable
     *
     * @param drawable is drawable
     */
    public void changeDrawable(boolean drawable) {
        DebugLog.d(TAG, "changeDrawable");
        DrawSurfaceView surfaceView = (DrawSurfaceView) this.views.get(R.id.draw_surface_view);
        if (surfaceView != null) {
            surfaceView.changeDrawable(drawable);
        }
    }

    /**
     * clear
     */
    public void clear() {
        DebugLog.d(TAG, "clear");
        DrawSurfaceView drawSurfaceView = (DrawSurfaceView) this.views.get(R.id.draw_surface_view);
        drawSurfaceView.clear();
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
