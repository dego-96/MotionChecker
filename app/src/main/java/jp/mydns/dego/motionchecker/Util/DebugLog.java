package jp.mydns.dego.motionchecker.Util;

import android.util.Log;

import jp.mydns.dego.motionchecker.BuildConfig;

public class DebugLog {

    // ---------------------------------------------------------------------------------------------
    // Static Method
    // ---------------------------------------------------------------------------------------------
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
