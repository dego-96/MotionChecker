package jp.mydns.dego.motionchecker.Util;

import android.app.Activity;
import android.view.View;

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
}
