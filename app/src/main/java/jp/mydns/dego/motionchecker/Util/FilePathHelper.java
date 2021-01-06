package jp.mydns.dego.motionchecker.Util;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class FilePathHelper {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "FilePathHelper";

    // ---------------------------------------------------------------------------------------------
    // Static Method
    // ---------------------------------------------------------------------------------------------

    /**
     * getVideoPathFromUri
     *
     * @param context activity
     * @param data    data
     * @return video path
     */
    public static String getVideoPathFromUri(Activity context, Intent data) {
        DebugLog.d(TAG, "getPathFromUri");

        Uri uri = data.getData();
        if (uri == null) {
            DebugLog.e(TAG, "Can not get data.");
            return null;
        }

        String path = null;
        int takeFlags = data.getFlags();
        takeFlags &= Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

        context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
        String wholeID = DocumentsContract.getDocumentId(uri);
        DebugLog.d(TAG, "whole ID : " + wholeID);
        if (wholeID.contains(":")) {
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Video.Media.DATA};
            String selection = MediaStore.Video.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                column,
                selection,
                new String[]{id},
                null
            );
            if (cursor != null && cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(column[0]));
                cursor.close();
            }
            return path;
        } else if (wholeID.contains("/")) {
            return wholeID;
        } else {
            DebugLog.e(TAG, "Can not get document id.");
            return null;
        }
    }
}
