package jp.mydns.dego.zanzo.View;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import jp.mydns.dego.zanzo.Drawer.DrawItemBase;
import jp.mydns.dego.zanzo.R;
import jp.mydns.dego.zanzo.Util.DebugLog;

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
    };
    private final int[] drawTypeViewIdList = {
        R.id.button_paint_line,
        R.id.button_paint_rect,
        R.id.button_paint_round,
        R.id.button_paint_path,
    };
    private final int[] colorViewIdList = {
        R.id.button_color_white,
        R.id.button_color_red,
        R.id.button_color_green,
        R.id.button_color_blue,
        R.id.button_color_yellow,
        R.id.button_color_black,
    };

    private final int[] drawableList = {
        R.drawable.button_paint_line,
        R.drawable.button_paint_rect,
        R.drawable.button_paint_round,
        R.drawable.button_paint_path,
    };
    private final int[] drawableActList = {
        R.drawable.button_paint_line_act,
        R.drawable.button_paint_rect_act,
        R.drawable.button_paint_round_act,
        R.drawable.button_paint_path_act,
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

        int length = this.viewIdList.length + this.drawTypeViewIdList.length + this.colorViewIdList.length;
        int[] idList = new int[length];

        int offset = 0;
        length = this.viewIdList.length;
        System.arraycopy(this.viewIdList, 0, idList, offset, length);

        offset = this.viewIdList.length;
        length = this.drawTypeViewIdList.length;
        System.arraycopy(this.drawTypeViewIdList, 0, idList, offset, length);

        offset = this.viewIdList.length + this.drawTypeViewIdList.length;
        length = this.colorViewIdList.length;
        System.arraycopy(this.colorViewIdList, 0, idList, offset, length);

        for (int id : idList) {
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
     * changeDrawTypeImageResource
     *
     * @param type color type
     */
    public void changeDrawTypeImageResource(DrawItemBase.DrawType type) {
        DebugLog.d(TAG, "changeDrawTypeImageResource");

        for (int index = 0; index < this.drawTypeViewIdList.length; index++) {
            int id = this.drawTypeViewIdList[index];
            ImageView imageView = (ImageView) this.views.get(id);
            if (index == type.ordinal()) {
                imageView.setImageResource(this.drawableActList[index]);
            } else {
                imageView.setImageResource(this.drawableList[index]);
            }
        }
    }

    /**
     * changeColorImageViewSize
     *
     * @param type color type
     */
    public void changeColorImageResource(DrawItemBase.ColorType type) {
        DebugLog.d(TAG, "changeColorImageViewSize");

        for (int index = 0; index < this.colorViewIdList.length; index++) {
            int id = this.colorViewIdList[index];
            ImageView imageView = (ImageView) this.views.get(id);
            if (index == type.ordinal()) {
                imageView.setImageResource(R.drawable.paint_color_target);
            } else {
                imageView.setImageDrawable(null);
            }
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

    /**
     * redraw
     */
    public void redraw() {
        DebugLog.d(TAG, "redraw");
        DrawSurfaceView drawSurfaceView = (DrawSurfaceView) this.views.get(R.id.draw_surface_view);
        drawSurfaceView.redraw();
    }

    /**
     * setUndoRedoAvailable
     *
     * @param itemsCount draw items count
     * @param index     index
     */
    public void setUndoRedoAvailable(int itemsCount, int index) {
        DebugLog.d(TAG, "setUndoRedoAvailable");
        boolean undoAvailable = (itemsCount > 0 && index > 0);
        boolean redoAvailable = (itemsCount > 0 && index < itemsCount);
        this.views.get(R.id.button_paint_undo).setEnabled(undoAvailable);
        this.views.get(R.id.button_paint_redo).setEnabled(redoAvailable);
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
