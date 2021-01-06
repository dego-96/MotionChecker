package jp.mydns.dego.motionchecker.Util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionManager {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "PermissionManager";
    private static final int PERMISSIONS_COUNT = 2;
    private static final int DENY_COUNT_MAX = 3;
    private static final int PERMISSION_INDEX_READ_EXTERNAL_STORAGE = 0;
//    private static final int PERMISSION_INDEX_INTERNET = 1;

    public enum PermissionResult {
        GRANTED,
        DENIED,
        BAN,
    }

    // ---------------------------------------------------------------------------------------------
    // Private Fields
    // ---------------------------------------------------------------------------------------------
    private static PermissionManager instance = null;
    private static int[] permissionDenyCount;

    private boolean canReadExternalStorage;
    private boolean canInternet;

    // ---------------------------------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------------------------------
    private PermissionManager() {
        DebugLog.d(TAG, "PermissionManager");
        this.canReadExternalStorage = false;
        this.canInternet = false;
    }

    // ---------------------------------------------------------------------------------------------
    // Static Method
    // ---------------------------------------------------------------------------------------------

    /**
     * getInstance
     *
     * @return PermissionManager
     */
    public static PermissionManager getInstance() {
        if (instance == null) {
            permissionDenyCount = new int[PERMISSIONS_COUNT];
            instance = new PermissionManager();
        }
        return instance;
    }

    /**
     * clearDenyCount
     */
    public static void clearDenyCount() {
        for (int index = 0; index < permissionDenyCount.length; index++) {
            permissionDenyCount[index] = 0;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Public Method
    // ---------------------------------------------------------------------------------------------

    /**
     * checkReadExternalStorage
     *
     * @param context application context
     * @return check result
     */
    public PermissionResult checkReadExternalStorage(Activity context) {
        DebugLog.d(TAG, "checkReadExternalStorage");

        if (permissionDenyCount[PERMISSION_INDEX_READ_EXTERNAL_STORAGE] > DENY_COUNT_MAX) {
            DebugLog.e(TAG, "Permission is denied " + DENY_COUNT_MAX + " times or more.");
            return PermissionResult.BAN;
        }

        int permission = ContextCompat.checkSelfPermission(
            context.getApplicationContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        );

        this.canReadExternalStorage = (permission == PackageManager.PERMISSION_GRANTED);
        if (this.canReadExternalStorage) {
            return PermissionResult.GRANTED;
        } else {
            permissionDenyCount[PERMISSION_INDEX_READ_EXTERNAL_STORAGE]++;
            return PermissionResult.DENIED;
        }
    }

    /**
     * getPermission
     *
     * @param permission permission
     * @return get result
     */
    public boolean getPermission(String permission) {
        DebugLog.d(TAG, "getPermission");
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return this.canReadExternalStorage;
            case Manifest.permission.INTERNET:
                return this.canInternet;
            default:
                return false;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

}
