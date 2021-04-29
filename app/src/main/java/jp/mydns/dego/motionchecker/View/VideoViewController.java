package jp.mydns.dego.motionchecker.View;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.mydns.dego.motionchecker.InstanceHolder;
import jp.mydns.dego.motionchecker.R;
import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.VideoPlayer.PlaySpeedManager;
import jp.mydns.dego.motionchecker.VideoPlayer.Video;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoDecoder;

public class VideoViewController {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "VideoViewController";
    private static final int ANIMATOR_DURATION = 300;
    private static final float ANIMATION_DISTANCE = 1000.0f;

    private enum AnimationDirection {
        None,
        Left,
        Up,
        Right,
        Down
    }

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private static final SimpleDateFormat TimerFormat = new SimpleDateFormat("mm:ss:SSS", Locale.JAPAN);
    private Display display;
    private SparseArray<View> views;
    private final int[] viewIdList = {
        R.id.video_surface_view,
        R.id.image_no_video,
        R.id.button_gallery,
        R.id.button_play,
        R.id.button_stop,
        R.id.button_speed_up,
        R.id.button_speed_down,
        R.id.button_next_frame,
        R.id.button_previous_frame,
        R.id.button_move_after,
        R.id.label_move_after,
        R.id.button_move_before,
        R.id.label_move_before,
        R.id.seek_bar_playtime,
        R.id.text_view_current_time,
        R.id.text_view_remain_time,
        R.id.text_view_speed,
        R.id.frame_control_area,
        R.id.button_paint,
        R.id.button_player,
        R.id.button_lock,
        R.id.button_motion_image,
    };

    private final int[][] visibilityTable = {
        /* 0x00:VISIBLE,  0x04:INVISIBLE,  0x08:GONE */
        /*INIT PAUSE PLAY  SEEK  NEXT  PREV */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00},   /* video_surface_view */
        {0x00, 0x08, 0x08, 0x08, 0x08, 0x08},   /* image_no_video */
        {0x00, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_gallery */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00},   /* button_play */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00},   /* button_stop */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_speed_up */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_speed_down */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_next_frame */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_previous_frame */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_move_after */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* label_move_after */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_move_before */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* label_move_before */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00},   /* seek_bar_playtime */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00},   /* text_view_current_time */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00},   /* text_view_remain_time */
        {0x08, 0x00, 0x00, 0x00, 0x00, 0x00},   /* text_view_speed */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* frame_control_area */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_paint */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_player */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_lock */
        {0x08, 0x00, 0x04, 0x00, 0x00, 0x00},   /* button_motion_image */
    };

    private final AnimationDirection[] animationDirections = {
        /* 0:no animation,  1:left,  2:up,  3:right,  4:down */
        AnimationDirection.None,    /* video_surface_view */
        AnimationDirection.None,    /* image_no_video */
        AnimationDirection.Up,      /* button_gallery */
        AnimationDirection.Down,    /* button_play */
        AnimationDirection.Down,    /* button_stop */
        AnimationDirection.Down,    /* button_speed_up */
        AnimationDirection.Down,    /* button_speed_down */
        AnimationDirection.Down,    /* button_next_frame */
        AnimationDirection.Down,    /* button_previous_frame */
        AnimationDirection.Down,    /* button_move_after */
        AnimationDirection.Down,    /* label_move_after */
        AnimationDirection.Down,    /* button_move_before */
        AnimationDirection.Down,    /* label_move_before */
        AnimationDirection.Down,    /* seek_bar_playtime */
        AnimationDirection.Down,    /* text_view_current_time */
        AnimationDirection.Down,    /* text_view_remain_time */
        AnimationDirection.Down,    /* text_view_speed */
        AnimationDirection.Down,    /* frame_control_area */
        AnimationDirection.Up,      /* button_paint */
        AnimationDirection.Up,      /* button_player */
        AnimationDirection.Up,      /* button_lock */
    };

    private boolean isFullScreenPreview;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoViewController
     */
    public VideoViewController() {
        DebugLog.d(TAG, "VideoViewController");

        this.display = null;
        this.views = null;
        this.isFullScreenPreview = false;
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
     * bindDisplay
     *
     * @param display display
     */
    public void bindDisplay(Display display) {
        DebugLog.d(TAG, "bindDisplay");
        this.display = display;

        if (this.views != null) {
            ((VideoSurfaceView) this.views.get(R.id.video_surface_view)).bindDisplay(display);
        }
    }

    /**
     * setVisibilities
     *
     * @param status video status
     */
    public void setVisibilities(VideoDecoder.DecoderStatus status) {
        DebugLog.d(TAG, "setVisibilities( " + status.name() + " )");

        for (int index = 0; index < visibilityTable.length; index++) {
            int visibility = visibilityTable[index][status.ordinal()];
            View view = this.views.get(this.viewIdList[index]);
            if (view != null) {
                DebugLog.v(TAG, "visibility : " + visibility);
                view.setVisibility(visibility);
            } else {
                DebugLog.w(TAG, "getView is null. (" + this.viewIdList[index] + ")");
                return;
            }
        }

        if (status == VideoDecoder.DecoderStatus.PLAYING) {
            this.setImageResource(status);
        } else if (status == VideoDecoder.DecoderStatus.PAUSED) {
            this.setImageResource(status);
            this.updateSpeedUpDownViews();
        }
    }

    /**
     * setSurfaceViewSize
     *
     * @param video video
     */
    public void setSurfaceViewSize(Video video) {
        DebugLog.d(TAG, "setSurfaceViewSize");

        if (this.display == null) {
            DebugLog.e(TAG, "Can not set surface view size.");
            return;
        }

        int width = video.getWidth();
        int height = video.getHeight();
        int rotation = video.getRotation();
        DebugLog.v(TAG, "video size : (" + width + ", " + height + ")");
        DebugLog.v(TAG, "video rotation : " + rotation);

        Point point = new Point();
        this.display.getRealSize(point);
        int displayW = point.x;
        int displayH = point.y;
        boolean isLandscape = ((rotation / 90) % 2 == 0);
        float videoAspect = isLandscape ? (float) width / (float) height : (float) height / (float) width;
        float displayAspect = (float) displayW / (float) displayH;

        int calcW;
        int calcH;
        if (isLandscape) {   // 横長画像
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
                calcW = displayW;
                calcH = (int) ((float) width * ((float) displayW / (float) height));
            }
        }

        VideoSurfaceView surfaceView = (VideoSurfaceView) this.views.get(R.id.video_surface_view);
        surfaceView.setSize(calcW, calcH);
    }

    /**
     * setDuration
     *
     * @param duration duration
     */
    public void setDuration(int duration) {
        DebugLog.d(TAG, "setDuration (" + duration + ")");
        ((SeekBar) this.views.get(R.id.seek_bar_playtime)).setMax(duration);
        ((TextView) this.views.get(R.id.text_view_remain_time)).setText(TimerFormat.format(new Date(duration)));
    }

    /**
     * getDuration
     *
     * @return duration
     */
    public int getDuration() {
        DebugLog.d(TAG, "getDuration");
        SeekBar seekBar = (SeekBar) this.views.get(R.id.seek_bar_playtime);
        return seekBar.getMax();
    }

    /**
     * setProgress
     *
     * @param progress progress
     */
    public void setProgress(int progress) {
        DebugLog.d(TAG, "setProgress (" + progress + ")");
        SeekBar seekBar = (SeekBar) this.views.get(R.id.seek_bar_playtime);
        seekBar.setProgress(progress);

        ((TextView) this.views.get(R.id.text_view_current_time)).setText(TimerFormat.format(new Date(progress)));
        int duration = seekBar.getMax();
        if (progress < duration) {
            ((TextView) this.views.get(R.id.text_view_remain_time)).setText(TimerFormat.format(new Date(duration - progress)));
        } else {
            DebugLog.e(TAG, "progress value error.");
            ((TextView) this.views.get(R.id.text_view_remain_time)).setText(InstanceHolder.getInstance().getText(R.string.remain_time_init));
        }

        this.update10SecAfterBeforeViews(progress);
    }

    /**
     * getProgress
     *
     * @return progress
     */
    public int getProgress() {
        DebugLog.d(TAG, "getProgress");
        SeekBar seekBar = (SeekBar) this.views.get(R.id.seek_bar_playtime);
        return seekBar.getProgress();
    }

    /**
     * changeViewLock
     */
    public void changeViewLock() {
        DebugLog.d(TAG, "changeViewLock");

        ImageView lockButton = (ImageView) this.views.get(R.id.button_lock);
        VideoSurfaceView surfaceView = (VideoSurfaceView) this.views.get(R.id.video_surface_view);

        boolean lock = surfaceView.changeLock();
        if (lock) {
            lockButton.setImageResource(R.drawable.video_view_lock);
        } else {
            lockButton.setImageResource(R.drawable.video_unlock);
        }
    }

    /**
     * updateSpeedUpDownViews
     */
    public void updateSpeedUpDownViews() {
        DebugLog.d(TAG, "updateSpeedUpDownViews");

        ImageView speedUpImageView = (ImageView) this.views.get(R.id.button_speed_up);
        ImageView speedDownImageView = (ImageView) this.views.get(R.id.button_speed_down);
        TextView speedTextView = (TextView) this.views.get(R.id.text_view_speed);
        int speedLevel = InstanceHolder.getInstance().getVideoController().getSpeedLevel();

        if (speedUpImageView == null ||
            speedDownImageView == null ||
            speedTextView == null) {
            DebugLog.e(TAG, "get view error.");
            return;
        }

        if (speedLevel == PlaySpeedManager.SPEED_LEVEL_MIN) {
            speedUpImageView.setEnabled(true);
            speedDownImageView.setEnabled(false);
        } else if (speedLevel == PlaySpeedManager.SPEED_LEVEL_MAX) {
            speedUpImageView.setEnabled(false);
            speedDownImageView.setEnabled(true);
        } else {
            speedUpImageView.setEnabled(true);
            speedDownImageView.setEnabled(true);
        }

        String[] speedText = InstanceHolder.getInstance().getResources().getStringArray(R.array.play_speed_text);
        speedTextView.setText(speedText[speedLevel]);
    }

    /**
     * updateNextPreviousViews
     *
     * @param position video position
     */
    public void updateNextPreviousViews(VideoDecoder.FramePosition position) {
        DebugLog.d(TAG, "updateNextPreviousViews");

        ImageView nextImageView = (ImageView) this.views.get(R.id.button_next_frame);
        ImageView prevImageView = (ImageView) this.views.get(R.id.button_previous_frame);

        if (nextImageView == null ||
            prevImageView == null) {
            DebugLog.e(TAG, "get view error.");
            return;
        }

        if (position == null ||
            position == VideoDecoder.FramePosition.FIRST) {
            nextImageView.setEnabled(true);
            prevImageView.setEnabled(false);
        } else if (position == VideoDecoder.FramePosition.MID) {
            nextImageView.setEnabled(true);
            prevImageView.setEnabled(true);
        } else if (position == VideoDecoder.FramePosition.LAST) {
            nextImageView.setEnabled(false);
            prevImageView.setEnabled(false);
        }
    }

    /**
     * animFullscreenPreview
     */
    public void animFullscreenPreview() {
        DebugLog.d(TAG, "fullScreenAnimationStart");

        float toY1, fromY1;
        float toY2, fromY2;
        List<Animator> animatorList = new ArrayList<>();

        if (this.isFullScreenPreview) {
            // animation (frame in)
            fromY1 = -1.0f * ANIMATION_DISTANCE;
            toY1 = 0.0f;
            fromY2 = ANIMATION_DISTANCE;
            toY2 = 0.0f;
        } else {
            // animation (frame out)
            fromY1 = 0.0f;
            toY1 = -1.0f * ANIMATION_DISTANCE;
            fromY2 = 0.0f;
            toY2 = ANIMATION_DISTANCE;
        }
        this.isFullScreenPreview = !this.isFullScreenPreview;

        for (int index = 0; index < this.viewIdList.length; index++) {
            View view = this.views.get(this.viewIdList[index]);
            AnimationDirection direction = animationDirections[index];
            if (direction == AnimationDirection.Up) {
                animatorList.add(
                    ObjectAnimator.ofFloat(view, "translationY", fromY1, toY1)
                        .setDuration(ANIMATOR_DURATION)
                );
            } else if (direction == AnimationDirection.Down) {
                animatorList.add(
                    ObjectAnimator.ofFloat(view, "translationY", fromY2, toY2)
                        .setDuration(ANIMATOR_DURATION)
                );
            }
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorList);
        animatorSet.start();
    }

    /**
     * getVideoSurfaceView
     *
     * @return video surface view
     */
    public VideoSurfaceView getVideoSurfaceView() {
        DebugLog.d(TAG, "getVideoSurfaceView");
        return ((VideoSurfaceView) this.views.get(R.id.video_surface_view));
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * setImageResource
     *
     * @param status video status
     */
    private void setImageResource(VideoDecoder.DecoderStatus status) {
        DebugLog.d(TAG, "setImageResource");
        ImageView playButton = (ImageView) this.views.get(R.id.button_play);

        if (playButton != null) {
            if (status == VideoDecoder.DecoderStatus.PLAYING) {
                playButton.setImageResource(R.drawable.video_pause);
            } else if (status == VideoDecoder.DecoderStatus.PAUSED) {
                playButton.setImageResource(R.drawable.video_play);
            }
        } else {
            DebugLog.e(TAG, "get image view resource error.");
        }
    }

    /**
     * update10SecAfterBeforeViews
     *
     * @param progress progress
     */
    private void update10SecAfterBeforeViews(int progress) {
        DebugLog.d(TAG, "update10SecAfterBeforeViews");
        int duration = this.getDuration();

        ImageView moveAftImageView = (ImageView) this.views.get(R.id.button_move_after);
        ImageView moveBfrImageView = (ImageView) this.views.get(R.id.button_move_before);
        TextView textMoveAft = (TextView) this.views.get(R.id.label_move_after);
        TextView textMoveBfr = (TextView) this.views.get(R.id.label_move_before);

        if (moveAftImageView == null ||
            moveBfrImageView == null ||
            textMoveAft == null ||
            textMoveBfr == null) {
            DebugLog.e(TAG, "get view error.");
            return;
        }

        // 10秒未満の場合は10秒戻るボタンを無効化
        if (progress < 10000) {
            moveBfrImageView.setEnabled(false);
            textMoveBfr.setEnabled(false);
        } else {
            moveBfrImageView.setEnabled(true);
            textMoveBfr.setEnabled(true);
        }

        // 残り時間が10秒未満の場合は10秒進むボタンを無効化
        if (duration - progress < 10000) {
            moveAftImageView.setEnabled(false);
            textMoveAft.setEnabled(false);
        } else {
            moveAftImageView.setEnabled(true);
            textMoveAft.setEnabled(true);
        }
    }
}
