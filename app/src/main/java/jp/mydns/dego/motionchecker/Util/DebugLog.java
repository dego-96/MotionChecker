package jp.mydns.dego.motionchecker.Util;

import android.util.Log;

import jp.mydns.dego.motionchecker.BuildConfig;

public class DebugLog {

    // ---------------------------------------------------------------------------------------------
    // Static Method
    // ---------------------------------------------------------------------------------------------

    /**
     * log verbose
     *
     * @param tag tag
     * @param msg log message
     */
    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    /**
     * log debug
     *
     * @param tag tag
     * @param msg log message
     */
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * log warning
     *
     * @param tag tag
     * @param msg log message
     */
    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * log info
     *
     * @param tag tag
     * @param msg log message
     */
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    /**
     * log error
     *
     * @param tag tag
     * @param msg log message
     */
    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }
}
