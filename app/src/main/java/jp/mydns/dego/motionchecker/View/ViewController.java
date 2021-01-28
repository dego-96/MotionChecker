package jp.mydns.dego.motionchecker.View;

import android.graphics.Color;
import android.graphics.Point;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import jp.mydns.dego.motionchecker.R;
import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoRunnable;

public class ViewController {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "ViewController";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private View rootView;
    private Display display;
    private SparseArray<View> views;
    private int[] viewIdList = {
        R.id.video_surface_view,
        R.id.image_no_video,
        R.id.button_gallery,
        R.id.button_play,
        R.id.button_stop,
        R.id.button_speed_up,
        R.id.button_speed_down,
        R.id.button_next_frame,
        R.id.button_previous_frame,
        R.id.seek_bar_playtime,
    };
    private final int[][] visibilityTable = {
        /* 0x00:VISIBLE,  0x04:INVISIBLE,  0x08:GONE */
        /*INIT  SEL PAUSE  PLAY   END  SEEK  NEXT  PREV */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},   /* video_surface_view */
        {0x00, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08},   /* image_no_video */
        {0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00},   /* button_gallery */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},   /* button_play */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},   /* button_stop */
        {0x08, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00},   /* button_speed_up */
        {0x08, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00},   /* button_speed_down */
        {0x08, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00},   /* button_next_frame */
        {0x08, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00},   /* button_previous_frame */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},   /* seek_bar_playtime */
    };

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * ViewController
     */
    public ViewController() {
        DebugLog.d(TAG, "ViewController");

        this.rootView = null;
        this.views = null;
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * bindRootView
     *
     * @param rootView layout root view
     */
    public void bindRootView(View rootView) {
        DebugLog.d(TAG, "bindRootView");

        this.rootView = rootView;
        int width = rootView.getWidth();
        int height = rootView.getHeight();
        DebugLog.v(TAG, "rootView size (w, h) : (" + width + ", " + height + ")");

        if (this.views == null) {
            this.views = new SparseArray<>();
            for (int id : this.viewIdList) {
                View view = rootView.findViewById(id);
                if (view != null) {
                    DebugLog.v(TAG, "put view (" + id + ")");
                    this.views.put(id, view);
                } else {
                    DebugLog.e(TAG, "Can not get view. (" + id + ")");
                }
            }
        }
    }

    /**
     * bindDisplay
     *
     * @param display display
     */
    public void bindDisplay(Display display) {
        DebugLog.d(TAG, "bindDisplay");
        this.display = display;

        if (this.views != null) {
            VideoSurfaceView view = (VideoSurfaceView) this.getView(R.id.video_surface_view);
            view.setDisplay(display);
        }
    }

    /**
     * setVisibility
     *
     * @param status video status
     */
    public void setVisibility(VideoRunnable.STATUS status) {
        DebugLog.d(TAG, "setVisibility( " + status.name() + " )");

        if (this.rootView == null) {
            DebugLog.e(TAG, "root view is null.");
            return;
        }

        for (int index = 0; index < this.visibilityTable.length; index++) {
            int visibility = this.visibilityTable[index][status.ordinal()];
            View view = this.getView(this.viewIdList[index]);
            if (view != null) {
                DebugLog.v(TAG, "visibility : " + visibility);
                view.setVisibility(visibility);
            } else {
                DebugLog.v(TAG, "getView is null. (" + this.viewIdList[index] + ")");
            }
        }

        this.setImageResource(status);
    }

    /**
     * setSurfaceViewSize
     *
     * @param width    video width
     * @param height   video height
     * @param rotation video rotation
     */
    public void setSurfaceViewSize(int width, int height, int rotation) {
        DebugLog.d(TAG, "setSurfaceViewSize");
        DebugLog.v(TAG, "surface view size (w, h) : (" + width + ", " + height + ")");

        if (this.display == null) {
            DebugLog.e(TAG, "Can not set surface view size.");
            return;
        }

        Point point = new Point();
        this.display.getSize(point);
        int displayW = point.x;
        int displayH = point.y;
        float videoAspect = (float) width / (float) height;
        float displayAspect = (float) displayW / (float) displayH;

        int calcW;
        int calcH;
        if ((rotation % 180) == 0) {   // 横長画像
            if (displayAspect > videoAspect) {
                // 画面より横長
                calcW = (int) ((float) width * ((float) displayH / (float) height));
                calcH = displayH;
            } else {
                // 画面より横長
                calcW = displayW;
                calcH = (int) ((float) height * ((float) displayW / (float) width));
            }
        } else {    // 縦長画像
            if (displayAspect > videoAspect) {
                // 画面より縦長
                calcW = (int) ((float) height * ((float) displayH / (float) width));
                calcH = displayH;
            } else {
                // 画面より横長
                calcW = displayH;
                calcH = (int) ((float) width * ((float) displayW / (float) height));
            }
        }

        VideoSurfaceView surfaceView = (VideoSurfaceView) this.getView(R.id.video_surface_view);
        surfaceView.setSize(calcW, calcH);
        surfaceView.setBackgroundColor(Color.GREEN);    // for debug
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * getView
     *
     * @param id view id
     * @return view
     */
    private View getView(int id) {
        DebugLog.d(TAG, "getView (" + id + ")");
        return this.views.get(id);
    }

    /**
     * setImageResource
     *
     * @param status video status
     */
    private void setImageResource(VideoRunnable.STATUS status) {
        DebugLog.d(TAG, "setImageResource");
        ImageView playButton = (ImageView) this.getView(R.id.button_play);

        if (playButton != null) {
            if (status == VideoRunnable.STATUS.PLAYING) {
                playButton.setImageResource(R.drawable.pause);
            } else if (status == VideoRunnable.STATUS.PAUSED) {
                playButton.setImageResource(R.drawable.play);
            }
        } else {
            DebugLog.e(TAG, "get image view resource error.");
        }
    }
}
