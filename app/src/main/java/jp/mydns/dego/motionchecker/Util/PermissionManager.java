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
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE));
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE));
            case Manifest.permission.INTERNET:
                return (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET));
            case Manifest.permission.ACCESS_NETWORK_STATE:
                return (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE));
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
     */
    public void requestPermission(Activity activity, String permission, int requestCode) {
        DebugLog.d(TAG, "requestPermission");

        ActivityCompat.requestPermissions(
            activity,
            new String[]{permission},
            requestCode);
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

}
