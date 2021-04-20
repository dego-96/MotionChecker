package jp.mydns.dego.motionchecker.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import jp.mydns.dego.motionchecker.InstanceHolder;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "DrawSurfaceView";
    private static final int TOUCH_TIME = 300;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private Bitmap prevBitmap;
    private Canvas previousCanvas;
    private Paint paint;
    private Path path;
    private long time;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * DrawSurfaceView
     *
     * @param context context
     */
    public DrawSurfaceView(Context context) {
        super(context);
        DebugLog.d(TAG, "DrawSurfaceView 1");

        init();
    }

    public DrawSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        DebugLog.d(TAG, "DrawSurfaceView 2");

        init();
    }

    /**
     * DrawSurfaceView
     *
     * @param context      context
     * @param attributeSet attribute set
     * @param defStyleAttr define style attribute
     */
    public DrawSurfaceView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        DebugLog.d(TAG, "DrawSurfaceView 3");

        init();
    }

    // ---------------------------------------------------------------------------------------------
    // protected method
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean performClick() {
        DebugLog.d(TAG, "performClick");
        super.performClick();
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DebugLog.d(TAG, "onTouchEvent");
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.time = System.nanoTime() / 1000000;
            this.path = new Path();
            this.path.moveTo(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            this.path.lineTo(x, y);
            this.draw();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            this.path.lineTo(x, y);
            this.draw();
            this.previousCanvas.drawPath(this.path, this.paint);

            if (System.nanoTime() / 1000000 - this.time < TOUCH_TIME) {
                this.performClick();
            }
        }
        return true;
    }

    /**
     * surfaceCreated
     *
     * @param holder surface holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DebugLog.d(TAG, "surfaceCreated");
        this.initBitmap();
    }

    /**
     * surfaceChanged
     *
     * @param holder surface holder
     * @param format format
     * @param width  width
     * @param height height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        DebugLog.d(TAG, "surfaceChanged");
    }

    /**
     * surfaceDestroyed
     *
     * @param holder surface holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        DebugLog.d(TAG, "surfaceDestroyed");
        this.prevBitmap.recycle();
        this.prevBitmap = null;
    }

//    /**
//     * clear
//     */
//    public void clear() {
//        DebugLog.d(TAG, "draw");
//
//        Canvas canvas = this.getHolder().lockCanvas();
//        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//        this.getHolder().unlockCanvasAndPost(canvas);
//    }

    /**
     * draw
     */
    public void draw() {
        DebugLog.d(TAG, "draw");

        Canvas canvas = this.getHolder().lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(this.prevBitmap, 0.0f, 0.0f, null);
        canvas.drawPath(this.path, this.paint);
        this.getHolder().unlockCanvasAndPost(canvas);
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     */
    private void init() {
        DebugLog.d(TAG, "init");

        SurfaceHolder holder = this.getHolder();
        if (holder != null) {
            holder.setFormat(PixelFormat.TRANSLUCENT);
            setZOrderOnTop(true);
            holder.addCallback(this);
        } else {
            DebugLog.e(TAG, "holder is null.");
        }

        this.paint = new Paint();
        this.paint.setColor(this.getColor());
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(15.0f);
    }

    /**
     * initBitmap
     */
    private void initBitmap() {
        DebugLog.d(TAG, "initBitmap");

        if (this.prevBitmap == null) {
            this.prevBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        }

        if (this.previousCanvas == null) {
            this.previousCanvas = new Canvas(this.prevBitmap);
        }

        this.previousCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    /**
     * getColor
     *
     * @return current color
     */
    private int getColor() {
        DebugLog.d(TAG, "getColor");
        return InstanceHolder.getInstance().getDrawingManager().getColor();
    }
}
