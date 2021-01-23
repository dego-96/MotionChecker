package jp.mydns.dego.motionchecker.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoSurfaceView extends SurfaceView {

    // ---------------------------------------------------------------------------------------------
    // inner class
    // ---------------------------------------------------------------------------------------------
//    private class DisplayInfo {
//        private int width;
//        private int height;
//        private Point center;
//
//        private DisplayInfo() {
//            this.width = 0;
//            this.height = 0;
//            this.center = null;
//        }
//
//        private DisplayInfo(Display display) {
//            Point outSize = new Point();
//            display.getSize(outSize);
//            this.width = outSize.x;
//            this.height = outSize.y;
//            this.center = new Point(outSize.x / 2, outSize.y / 2);
//        }
//
//        private int left() {
//            return (this.center != null) ? this.center.x - (this.width / 2) : 0;
//        }
//
//        private int right() {
//            return (this.center != null) ? this.center.x + (this.width / 2) : 0;
//        }
//
//        private int top() {
//            return (this.center != null) ? this.center.y - (this.height / 2) : 0;
//        }
//
//        private int bottom() {
//            return (this.center != null) ? this.center.y - (this.height / 2) : 0;
//        }
//    }

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoSurfaceView";
//    private static final float SCALE_MAX = 4.0f;
//    private static final float SCALE_MIN = 0.5f;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
//    private ScaleGestureDetector scaleGestureDetector;
//    private float scale;
//    private DisplayInfo display;
//    private ViewGroup.LayoutParams initLayout;
//    private Point startPoint;
//    private Point offset;
//    private boolean canChangeLayout;
//    private boolean isMove;

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
        DebugLog.d(TAG, "VideoSurfaceView");
    }

    /**
     * VideoSurfaceView
     *
     * @param context      context
     * @param attributeSet attribute set
     */
    public VideoSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        DebugLog.d(TAG, "VideoSurfaceView");
//        init(context);
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
        DebugLog.d(TAG, "VideoSurfaceView");
//        init(context);
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
     * layout
     *
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    @Override
    public void layout(int l, int t, int r, int b) {
        DebugLog.d(TAG, "layout(" + l + ", " + t + ", " + r + ", " + b + ")");
//        if (this.canChangeLayout) {
            DebugLog.d(TAG, "call super.layout(...)");
            super.layout(l, t, r, b);
//        }
    }

//    /**
//     * setGestureMotionEvent
//     *
//     * @param event motion event
//     */
//    public void setGestureMotionEvent(MotionEvent event) {
//        DebugLog.d(TAG, "setGestureMotionEvent");
//        this.isMove = false;
//        this.scaleGestureDetector.onTouchEvent(event);
//    }
//
//    /**
//     * move
//     *
//     * @param event Motion Event
//     */
//    public void move(MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            this.isMove = true;
//            this.startPoint = new Point(x, y);
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            this.startPoint = null;
//            this.isMove = false;
//        } else if (this.isMove && event.getAction() == MotionEvent.ACTION_MOVE) {
//            this.offset.x = x - this.startPoint.x;
//            this.offset.y = y - this.startPoint.y;
//
//            limit(
//                this.getLeft() + this.offset.x,
//                this.getTop() + this.offset.y,
//                this.getRight() + this.offset.x,
//                this.getBottom() + this.offset.y);
//        }
//    }
//
//    /**
//     * setInitialSize
//     *
//     * @param width  width
//     * @param height height
//     */
//    void setInitialSize(int width, int height) {
//        DebugLog.d(TAG, "setInitialSize(" + width + ", " + height + ")");
//        this.initLayout = new ViewGroup.LayoutParams(width, height);
//
//        if (this.display == null) {
//            return;
//        }
//
//        this.canChangeLayout = true;
//        this.layout(
//            this.display.left(),
//            this.display.top(),
//            this.display.right(),
//            this.display.bottom());
//        this.canChangeLayout = false;
//    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

//    /**
//     * init
//     *
//     * @param context context
//     */
//    private void init(Context context) {
//        DebugLog.d(TAG, "init");
//
//        this.scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
//            @Override
//            public boolean onScale(ScaleGestureDetector detector) {
//                resize(detector.getScaleFactor());
//                return true;
//            }
//
//            @Override
//            public boolean onScaleBegin(ScaleGestureDetector detector) {
//                resize(detector.getScaleFactor());
//                return true;
//            }
//
//            @Override
//            public void onScaleEnd(ScaleGestureDetector detector) {
//            }
//        });
//
//        this.scale = 1.0f;
//        this.initLayout = null;
//        this.canChangeLayout = true;
//        this.isMove = false;
//
//        if (context instanceof Activity) {
//            this.display = new DisplayInfo(((Activity) context).getWindowManager().getDefaultDisplay());
//        } else {
//            DebugLog.e(TAG, "Can not set SurfaceView layout.");
//            this.display = null;
//        }
//    }
//
//    /**
//     * resize
//     *
//     * @param scale scale
//     */
//    private void resize(float scale) {
//
//        /* calc scale */
//        this.scale *= scale;
//        this.scale = this.scale > SCALE_MAX ? SCALE_MAX : this.scale;
//        this.scale = this.scale < SCALE_MIN ? SCALE_MIN : this.scale;
//        DebugLog.d(TAG, "scale : " + this.scale);
//
//    }
//
//    /**
//     * limit
//     *
//     * @param left   setting value of left
//     * @param top    setting value of top
//     * @param right  setting value of right
//     * @param bottom setting value of bottom
//     */
//    private void limit(int left, int top, int right, int bottom) {
//        // 横位置の限界を設定
//        int displayWidth = this.display.width;
//        int movieWidth = right - left;
//        if (movieWidth <= displayWidth) {
//            if (left < 0) {
//                this.moveX -= left;
//                right -= left;
//                left = 0;
//            } else if (right > displayWidth) {
//                this.moveX -= right - displayWidth;
//                left -= right - displayWidth;
//                right = displayWidth;
//            }
//        }
//        if (movieWidth >= displayWidth) {
//            if (left > 0) {
//                this.moveX -= left;
//                right -= left;
//                left = 0;
//            } else if (right < displayWidth) {
//                this.moveX += displayWidth - right;
//                left += displayWidth - right;
//                right = displayWidth;
//            }
//        }
//
//        // 縦位置の限界を設定
//        int displayHeight = this.displayCenter.y * 2;
//        int movieHeight = bottom - top;
//        if (movieHeight <= displayHeight) {
//            if (top < 0) {
//                this.moveY -= top;
//                bottom -= top;
//                top = 0;
//            } else if (bottom > displayHeight) {
//                this.moveY -= bottom - displayHeight;
//                top -= bottom - displayHeight;
//                bottom = displayHeight;
//            }
//        }
//        if (movieHeight >= displayHeight) {
//            if (top > 0) {
//                this.moveY -= top;
//                bottom -= top;
//                top = 0;
//            } else if (bottom < displayHeight) {
//                this.moveY += displayHeight - bottom;
//                top += displayHeight - bottom;
//                bottom = displayHeight;
//            }
//        }
//
//        this.canChangeLayout = true;
//        this.layout(left, top, right, bottom);
//        this.canChangeLayout = false;
//    }
}
