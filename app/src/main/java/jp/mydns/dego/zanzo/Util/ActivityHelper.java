package jp.mydns.dego.zanzo.Util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;

import jp.mydns.dego.zanzo.MainActivity;

public class ActivityHelper {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "ActivityHelper";

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * hideSystemUI
     *
     * @param activity activity
     */
    public static void hideSystemUI(Activity activity) {
        DebugLog.d(TAG, "hideSystemUI");

        View decor = activity.getWindow().getDecorView();
        decor.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * setOrientationChangeable
     *
     * @param activity activity
     * @param mode mode
     */
    public static void setOrientationChangeable(Activity activity, MainActivity.Mode mode) {
        DebugLog.d(TAG, "setOrientationChangeable");

        int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        if (mode == MainActivity.Mode.Motion) {
            int orientation = activity.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        }
        activity.setRequestedOrientation(screenOrientation);
    }
}
