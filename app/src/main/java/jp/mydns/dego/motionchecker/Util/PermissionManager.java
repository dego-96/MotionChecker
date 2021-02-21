package jp.mydns.dego.motionchecker.Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import jp.mydns.dego.motionchecker.InstanceHolder;

public class PermissionManager {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "PermissionManager";

    // ---------------------------------------------------------------------------------------------
    // Private Fields
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------------------------------
    public PermissionManager() {
        DebugLog.d(TAG, "PermissionManager");
    }

    // ---------------------------------------------------------------------------------------------
    // Public Method
    // ---------------------------------------------------------------------------------------------

    /**
     * getPermission
     *
     * @param permission permission
     * @return get result
     */
    public boolean getPermission(String permission) {
        DebugLog.d(TAG, "getPermission");
        Context context = InstanceHolder.getInstance().getApplicationContext();
        int result;
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                return (result == PackageManager.PERMISSION_GRANTED);
            case Manifest.permission.INTERNET:
                result = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
                return (result == PackageManager.PERMISSION_GRANTED);
            default:
                return false;
        }
    }

    /**
     * requestPermission
     *
     * @param activity    activity
     * @param permission  permission
     * @param requestCode request code
     * @return is requested
     */
    public boolean requestPermission(Activity activity, String permission, int requestCode) {
        DebugLog.d(TAG, "requestPermission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(
                activity,
                new String[]{permission},
                requestCode);
            return true;
        }
        DebugLog.i(TAG, "can not show request permission dialog");
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

}
