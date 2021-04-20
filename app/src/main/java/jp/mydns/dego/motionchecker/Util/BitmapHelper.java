package jp.mydns.dego.motionchecker.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.mydns.dego.motionchecker.InstanceHolder;

public class BitmapHelper {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "BitmapHelper";
    private static final String CAPTURE_IMAGE_FILENAME = "capture.png";
    private static final String DRAW_IMAGE_FILENAME = "draw.png";

    public enum BitmapType {
        Capture,
        Draw,
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * saveBitmapToCache
     *
     * @param type bitmap type
     * @param bitmap bitmap
     * @return is success.
     */
    public static boolean saveBitmapToCache(BitmapType type, Bitmap bitmap) {
        DebugLog.d(TAG, "saveBitmapToCache");

        if (bitmap == null) {
            return false;
        }

        try {
            File bitmapFile = BitmapHelper.getCacheFile(type);
            FileOutputStream outStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * loadBitmapFromCache
     *
     * @param type bitmap type
     * @return bitmap
     */
    public static Bitmap loadBitmapFromCache(BitmapType type) {
        DebugLog.d(TAG, "loadBitmapFromCache");

        try {
            File bitmapFile = BitmapHelper.getCacheFile(type);
            return BitmapFactory.decodeStream(new FileInputStream(bitmapFile));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * getCacheFile
     *
     * @param type bitmap type
     * @return bitmap file in cache directory
     */
    private static File getCacheFile(BitmapType type) {
        DebugLog.d(TAG, "getCacheFile");

        Context context = InstanceHolder.getInstance().getApplicationContext();
        File rootDir = context.getCacheDir();

        if (type == BitmapType.Capture) {
            return new File(rootDir, CAPTURE_IMAGE_FILENAME);
        } else if (type == BitmapType.Draw) {
            return new File(rootDir, DRAW_IMAGE_FILENAME);
        } else {
            return null;    // ありえないケース
        }
    }
}
