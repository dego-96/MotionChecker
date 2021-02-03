package jp.mydns.dego.motionchecker.View;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import jp.mydns.dego.motionchecker.InstanceHolder;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // ---------------------------------------------------------------------------------------------
    // inner class
    // ---------------------------------------------------------------------------------------------
    private static class LayoutInfo {
        private final Rect displaySize;
        private final Point center;
        private int timeTouchDown;
        private Point startPoint;
        private Rect initLayout;
        private int offsetX;
        private int offsetY;

        private LayoutInfo(Display display) {
            Point outSize = new Point();
            display.getSize(outSize);
            this.displaySize = new Rect(0, 0, outSize.x, outSize.y);
            this.center = new Point(outSize.x / 2, outSize.y / 2);
            DebugLog.v(TAG, "display center : (" + this.center.x + ", " + this.center.y + ")");

            this.timeTouchDown = 0;
            this.startPoint = null;
            this.initLayout = new Rect();
            this.offsetX = 0;
            this.offsetY = 0;
        }

        private void touchStart(int x, int y, long nanoTime) {
            this.timeTouchDown = (int) (nanoTime / 1000 / 1000);
            this.startPoint = new Point(x, y);
        }

        private int diffTime(long nanoTime) {
            return (int) (nanoTime / 1000 / 1000) - this.timeTouchDown;
        }

        private void setOffset(int x, int y) {
            if (this.startPoint != null) {
                this.offsetX += x - this.startPoint.x;
                this.offsetY += y - this.startPoint.y;
                DebugLog.v(TAG, "start  : (" + this.startPoint.x + ", " + this.startPoint.y + ")");
                DebugLog.v(TAG, "offset : (" + this.offsetX + ", " + this.offsetY + ")");
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoSurfaceView";
    private static final int CLICK_TIME = 300;
    private static final float SCALE_MAX = 4.0f;
    private static final float SCALE_MIN = 0.5f;

    private enum ACTION_MODE {
        NONE,
        MOVE,
        EXPAND,
    }

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private ScaleGestureDetector scaleGestureDetector;
    private float scale;
    private LayoutInfo layoutInfo;
    private ACTION_MODE mode;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoSurfaceView
     *
     * @param context context
     */
    public VideoSurfaceView(Context context) {
        super(context);
        DebugLog.d(TAG, "VideoSurfaceView 1");
        init(context);
    }

    /**
     * VideoSurfaceView
     *
     * @param context      context
     * @param attributeSet attribute set
     */
    public VideoSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        DebugLog.d(TAG, "VideoSurfaceView 2");
        init(context);
    }

    /**
     * VideoSurfaceView
     *
     * @param context      context
     * @param attributeSet attribute set
     * @param defStyleAttr define style attribute
     */
    public VideoSurfaceView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        DebugLog.d(TAG, "VideoSurfaceView 3");
        init(context);
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * performClick
     *
     * @return disable next touch event
     */
    @Override
    public boolean performClick() {
        DebugLog.d(TAG, "performClick");
        super.performClick();
        return false;
    }

    /**
     * onTouchEvent
     *
     * @param event motion event
     * @return touch event result
     */
    @SuppressWarnings("ClickableViewAccessibility") // touchメソッド内でperformClickを実行
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DebugLog.d(TAG, "onTouchEvent");
        return touch(event);
    }

    /**
     * layout
     *
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    @Override
    public void layout(int l, int t, int r, int b) {
        DebugLog.d(TAG, "layout (" + l + ", " + t + ", " + r + ", " + b + ")");
        /* super.layout(l, t, r, b); */ /* layout指定は独自に管理したいので親クラスのlayoutメソッドは無効化 */
    }

    /**
     * surfaceCreated
     *
     * @param holder surface holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DebugLog.d(TAG, "surfaceCreated");
        InstanceHolder.getInstance().getVideoController().videoSetup(holder.getSurface());
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
        DebugLog.v(TAG, "format : " + format);
        DebugLog.v(TAG, "width  : " + width);
        DebugLog.v(TAG, "height : " + height);
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
     * setSize
     *
     * @param width  width
     * @param height height
     */
    void setSize(int width, int height) {
        DebugLog.d(TAG, "setSize(" + width + ", " + height + ")");

        if (this.layoutInfo == null) {
            return;
        }

        int left = this.layoutInfo.center.x - (width / 2);
        int top = this.layoutInfo.center.y - (height / 2);
        int right = left + width;
        int bottom = top + height;
        this.layoutInfo.initLayout = new Rect(left, top, right, bottom);

        DebugLog.v(TAG, "size (" + left + ", " + top + ", " + right + ", " + bottom + ")");
        super.layout(left, top, right, bottom);
    }

    /**
     * setDisplay
     *
     * @param display display
     */
    void setDisplay(Display display) {
        DebugLog.d(TAG, "setDisplay");

        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(display);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * init
     */
    private void init(Context context) {
        DebugLog.d(TAG, "init");

        SurfaceHolder holder = this.getHolder();
        if (holder != null) {
            holder.addCallback(this);
        } else {
            DebugLog.e(TAG, "holder is null");
        }

        this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                DebugLog.d(TAG, "onScale");
                resize(detector.getScaleFactor());
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                DebugLog.d(TAG, "onScaleBegin");
                resize(detector.getScaleFactor());
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                DebugLog.d(TAG, "onScaleEnd");
                mode = ACTION_MODE.NONE;
            }
        });

        this.scale = 1.0f;
        this.layoutInfo = null;
    }

    /**
     * touch
     *
     * @param event motion event
     * @return touch result
     */
    private boolean touch(MotionEvent event) {
        DebugLog.d(TAG, "touch");

        if (event.getPointerCount() == 1) {
            DebugLog.v(TAG, "PointerCount 1");
            this.move(event);
        } else if (event.getPointerCount() == 2) {
            DebugLog.v(TAG, "PointerCount 2");
            this.mode = ACTION_MODE.EXPAND;
            this.scaleGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    /**
     * move
     *
     * @param event motion event
     */
    private void move(MotionEvent event) {
        DebugLog.d(TAG, "move");

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            DebugLog.v(TAG, "ACTION_DOWN");
            this.layoutInfo.touchStart(x, y, System.nanoTime());
            this.mode = ACTION_MODE.MOVE;
        } else if (this.mode == ACTION_MODE.MOVE && event.getAction() == MotionEvent.ACTION_MOVE) {
            DebugLog.v(TAG, "ACTION_MOVE");
            this.layoutInfo.setOffset(x, y);
            int dx = x - this.layoutInfo.startPoint.x;
            int dy = y - this.layoutInfo.startPoint.y;
            this.limit(
                this.getLeft() + dx,
                this.getTop() + dy,
                this.getRight() + dx,
                this.getBottom() + dy
            );
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            DebugLog.v(TAG, "ACTION_UP");
            this.mode = ACTION_MODE.NONE;
            int diffTime = this.layoutInfo.diffTime(System.nanoTime());
            if (diffTime < CLICK_TIME) {
                this.performClick();
            }
        }
    }

    /**
     * resize
     *
     * @param scale scale
     */
    private void resize(float scale) {
        DebugLog.d(TAG, "resize");

        /* calc scale */
        this.scale *= scale;
        this.scale = Math.min(this.scale, SCALE_MAX);
        this.scale = Math.max(this.scale, SCALE_MIN);
        DebugLog.v(TAG, "scale : " + this.scale);

        int width = (int) ((float) this.layoutInfo.initLayout.width() * this.scale);
        int height = (int) ((float) this.layoutInfo.initLayout.height() * this.scale);

        this.limit(
            this.layoutInfo.center.x - (width / 2) + this.layoutInfo.offsetX,
            this.layoutInfo.center.y - (height / 2) + this.layoutInfo.offsetY,
            this.layoutInfo.center.x + (width / 2) + this.layoutInfo.offsetX,
            this.layoutInfo.center.y + (height / 2) + this.layoutInfo.offsetY
        );
    }

    /**
     * limit
     *
     * @param left   setting value of left
     * @param top    setting value of top
     * @param right  setting value of right
     * @param bottom setting value of bottom
     */
    private void limit(int left, int top, int right, int bottom) {
        DebugLog.d(TAG, "limit");

        Rect result = new Rect(left, top, right, bottom);

        // 横位置の限界を設定
        int displayWidth = this.layoutInfo.displaySize.width();
        int videoWidth = right - left;
        if (videoWidth <= displayWidth) {
            if (left < 0) {
                this.layoutInfo.offsetX -= left;
                result.right -= left;
                result.left = 0;
            } else if (right > displayWidth) {
                this.layoutInfo.offsetX -= right - displayWidth;
                result.left -= right - displayWidth;
                result.right = displayWidth;
            }
        }
        if (videoWidth >= displayWidth) {
            if (left > 0) {
                this.layoutInfo.offsetX -= left;
                result.right -= left;
                result.left = 0;
            } else if (right < displayWidth) {
                this.layoutInfo.offsetX += displayWidth - right;
                result.left += displayWidth - right;
                result.right = displayWidth;
            }
        }

        // 縦位置の限界を設定
        int displayHeight = this.layoutInfo.displaySize.height();
        int videoHeight = bottom - top;
        if (videoHeight <= displayHeight) {
            if (top < 0) {
                this.layoutInfo.offsetY -= top;
                result.bottom -= top;
                result.top = 0;
            } else if (bottom > displayHeight) {
                this.layoutInfo.offsetY -= bottom - displayHeight;
                result.top -= bottom - displayHeight;
                result.bottom = displayHeight;
            }
        }
        if (videoHeight >= displayHeight) {
            if (top > 0) {
                this.layoutInfo.offsetY -= top;
                result.bottom -= top;
                result.top = 0;
            } else if (bottom < displayHeight) {
                this.layoutInfo.offsetY += displayHeight - bottom;
                result.top += displayHeight - bottom;
                result.bottom = displayHeight;
            }
        }

        super.layout(result.left, result.top, result.right, result.bottom);
    }
}
