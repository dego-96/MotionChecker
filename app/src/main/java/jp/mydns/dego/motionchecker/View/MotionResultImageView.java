package jp.mydns.dego.motionchecker.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class MotionResultImageView extends AppCompatImageView {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MotionResultImageView";
    private static final int CLICK_TIME = 150;
    private static final float SCALE_MAX = 4.0f;
    private static final float SCALE_MIN = 1.0f;

    private enum ACTION_MODE {
        NONE,
        MOVE,
        EXPAND,
    }

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private long touchDownTime;
    private ACTION_MODE mode;
    private ScaleGestureDetector scaleGestureDetector;
    private float scale;
    private Rect displaySize;
    private Rect initLayout;
    private int offsetX;
    private int offsetY;
    private Point touchDown;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * FrameControlAreaView
     *
     * @param context context
     */
    public MotionResultImageView(@NonNull Context context) {
        super(context);
        DebugLog.d(TAG, "MotionResultImageView 1");

        this.init(context);
    }

    /**
     * MotionResultImageView
     *
     * @param context context
     * @param attrs   attribute set
     */
    public MotionResultImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DebugLog.d(TAG, "MotionResultImageView 2");

        this.init(context);
    }

    /**
     * MotionResultImageView
     *
     * @param context      context
     * @param attrs        attribute set
     * @param defStyleAttr default style attribute
     */
    public MotionResultImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DebugLog.d(TAG, "MotionResultImageView 3");

        this.init(context);
    }
    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

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

        if (event.getPointerCount() == 1) {
            this.move(event);
        } else if (event.getPointerCount() == 2) {
            this.mode = ACTION_MODE.EXPAND;
            this.scaleGestureDetector.onTouchEvent(event);
        } else {
            DebugLog.i(TAG, "Multi touch (over 2 points) is not support.");
            return false;
        }

        return true;
    }

    /**
     * performClick
     *
     * @return True there was an assigned OnClickListener that was called, false
     * otherwise is returned.
     */
    @Override
    public boolean performClick() {
        // WARNING対策
        return super.performClick();
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
     * setImageBitmap
     *
     * @param bitmap bitmap
     */
    @Override
    public void setImageBitmap(Bitmap bitmap) {
        DebugLog.d(TAG, "setImageBitmap");
        super.setImageBitmap(bitmap);

        this.initLayout(bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * bindDisplay
     *
     * @param display display
     */
    public void bindDisplay(Display display) {
        DebugLog.d(TAG, "bindDisplay");

        if (this.displaySize == null) {
            Point outSize = new Point();
            display.getRealSize(outSize);
            this.displaySize = new Rect(0, 0, outSize.x, outSize.y);
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
        this.offsetX = 0;
        this.offsetY = 0;
    }

    /**
     * initLayout
     */
    private void initLayout(int width, int height) {
        DebugLog.d(TAG, "initLayout");

        int left = this.displaySize.centerX() - (width / 2);
        int top = this.displaySize.centerY() - (height / 2);
        int right = left + width;
        int bottom = top + height;
        this.initLayout = new Rect(left, top, right, bottom);

        DebugLog.v(TAG, "init layout (" + left + ", " + top + ", " + right + ", " + bottom + ")");
        super.layout(left, top, right, bottom);
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
            this.touchDownTime = System.nanoTime();
            this.touchDown = new Point(x, y);
            this.mode = ACTION_MODE.MOVE;
        } else if (this.mode == ACTION_MODE.MOVE && event.getAction() == MotionEvent.ACTION_MOVE) {
            int diffX = x - this.touchDown.x;
            int diffY = y - this.touchDown.y;
            this.offsetX += diffX;
            this.offsetY += diffY;
            this.limit(
                this.getLeft() + diffX,
                this.getTop() + diffY,
                this.getRight() + diffX,
                this.getBottom() + diffY
            );
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            long nanoTime = System.nanoTime();
            if (nanoTime - this.touchDownTime < CLICK_TIME * 1000) {
                this.performClick();
            }
            this.touchDown = null;
            this.mode = ACTION_MODE.NONE;
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

        int width = (int) ((float) this.initLayout.width() * this.scale);
        int height = (int) ((float) this.initLayout.height() * this.scale);

        this.limit(
            this.displaySize.centerX() - (width / 2) + this.offsetX,
            this.displaySize.centerY() - (height / 2) + this.offsetY,
            this.displaySize.centerX() + (width / 2) + this.offsetX,
            this.displaySize.centerY() + (height / 2) + this.offsetY
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
        int displayWidth = this.displaySize.width();
        int videoWidth = right - left;
        if (videoWidth <= displayWidth) {
            if (left < 0) {
                this.offsetX -= left;
                result.right -= left;
                result.left = 0;
            } else if (right > displayWidth) {
                this.offsetX -= right - displayWidth;
                result.left -= right - displayWidth;
                result.right = displayWidth;
            }
        }
        if (videoWidth >= displayWidth) {
            if (left > 0) {
                this.offsetX -= left;
                result.right -= left;
                result.left = 0;
            } else if (right < displayWidth) {
                this.offsetX += displayWidth - right;
                result.left += displayWidth - right;
                result.right = displayWidth;
            }
        }

        // 縦位置の限界を設定
        int displayHeight = this.displaySize.height();
        int videoHeight = bottom - top;
        if (videoHeight <= displayHeight) {
            if (top < 0) {
                this.offsetY -= top;
                result.bottom -= top;
                result.top = 0;
            } else if (bottom > displayHeight) {
                this.offsetY -= bottom - displayHeight;
                result.top -= bottom - displayHeight;
                result.bottom = displayHeight;
            }
        }
        if (videoHeight >= displayHeight) {
            if (top > 0) {
                this.offsetY -= top;
                result.bottom -= top;
                result.top = 0;
            } else if (bottom < displayHeight) {
                this.offsetY += displayHeight - bottom;
                result.top += displayHeight - bottom;
                result.bottom = displayHeight;
            }
        }

        DebugLog.v(TAG, "layout after limit (" + result.left + ", " + result.top + ", " + result.right + ", " + result.bottom + ")");
        super.layout(result.left, result.top, result.right, result.bottom);
    }
}
