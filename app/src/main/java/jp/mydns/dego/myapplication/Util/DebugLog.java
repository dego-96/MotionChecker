package jp.mydns.dego.myapplication.Util;

import android.util.Log;

import jp.mydns.dego.myapplication.BuildConfig;

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
