package jp.mydns.dego.motionchecker.View;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import jp.mydns.dego.motionchecker.R;
import jp.mydns.dego.motionchecker.Util.BitmapHelper;
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
        R.id.video_capture_image,
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
     * setCaptureImage
     *
     * @param surfaceViewSize video surface view size
     */
    public void setCaptureImage(Rect surfaceViewSize) {
        DebugLog.d(TAG, "setCaptureImage");

        CaptureImageView imageView = (CaptureImageView) this.views.get(R.id.video_capture_image);

        BitmapHelper.BitmapType type = BitmapHelper.BitmapType.Capture;
        Bitmap bitmap = BitmapHelper.loadBitmapFromCache(type);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.layout(surfaceViewSize);
        } else {
            DebugLog.e(TAG, "bitmap is null");
        }

    }
}
