package jp.mydns.dego.motionchecker.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import jp.mydns.dego.motionchecker.Drawer.DrawItemBase;
import jp.mydns.dego.motionchecker.Drawer.LineItem;
import jp.mydns.dego.motionchecker.Drawer.PathItem;
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
    private Paint paint;
    private DrawItemBase currentItem;
    private long touchDownTime;

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

    /**
     * onDraw
     *
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        DebugLog.d(TAG, "onDraw");

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        for (DrawItemBase item : this.getDrawItems()) {
            this.drawItem(canvas, item);
        }
        if (this.currentItem != null) {
            this.drawItem(canvas, this.currentItem);
        }
    }

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

        if (this.isClicked(event)) {
            this.performClick();
        }

        DrawItemBase.DrawType drawType = this.getDrawType();

        if (drawType == DrawItemBase.DrawType.Path) {
            this.createPath(event);
        } else if (drawType == DrawItemBase.DrawType.Line) {
            this.createLine(event);
        }

        Canvas canvas = this.getHolder().lockCanvas();
        this.draw(canvas);
        this.getHolder().unlockCanvasAndPost(canvas);

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
        this.clear();
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
    }

    /**
     * clear
     */
    public void clear() {
        DebugLog.d(TAG, "clear");

        Canvas canvas = this.getHolder().lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
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

        this.currentItem = null;
        this.paint = new Paint();
        this.paint.setColor(this.getColor());
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(10.0f);
    }

    /**
     * getDrawType
     *
     * @return draw type
     */
    private DrawItemBase.DrawType getDrawType() {
        DebugLog.d(TAG, "getDrawType");
        return InstanceHolder.getInstance().getDrawingManager().getDrawType();
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

    /**
     * createPath
     *
     * @param event motion event
     */
    private void createPath(MotionEvent event) {
        DebugLog.d(TAG, "createPath");
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.currentItem = new PathItem(this.getColor());
            ((PathItem) this.currentItem).moveTo(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (this.currentItem instanceof PathItem) {
                ((PathItem) this.currentItem).lineTo(x, y);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (this.currentItem instanceof PathItem) {
                ((PathItem) this.currentItem).lineTo(x, y);
                this.addItem();
            }
        }
    }

    private void createLine(MotionEvent event) {
        DebugLog.d(TAG, "createPath");
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.currentItem = new LineItem(this.getColor());
            ((LineItem) this.currentItem).start(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            ((LineItem) this.currentItem).end(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            ((LineItem) this.currentItem).end(x, y);
            this.addItem();
        }
    }

    /**
     * addItem
     */
    private void addItem() {
        DebugLog.d(TAG, "addItem");
        InstanceHolder.getInstance().getDrawingManager().addDrawItem(this.currentItem);
        this.currentItem = null;
    }

    private List<DrawItemBase> getDrawItems() {
        return InstanceHolder.getInstance().getDrawingManager().getDrawItems();
    }

    /**
     * isClicked
     *
     * @param event motion event
     * @return is clicked
     */
    private boolean isClicked(MotionEvent event) {
        DebugLog.d(TAG, "isClicked");

        long currentTime = System.nanoTime() / 1000000;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.touchDownTime = currentTime;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return (currentTime - this.touchDownTime) < TOUCH_TIME;
        }
        return false;
    }

    /**
     * drawItem
     *
     * @param canvas canvas
     * @param item   draw item
     */
    private void drawItem(Canvas canvas, DrawItemBase item) {
        DebugLog.d(TAG, "drawItem");
        if (item instanceof PathItem) {
            canvas.drawPath(((PathItem) item).getPath(), this.paint);
        } else if (item instanceof LineItem) {
            float[] points = ((LineItem) item).getPoints();
            canvas.drawLine(points[0], points[1], points[2], points[3], this.paint);
        }
    }

}
