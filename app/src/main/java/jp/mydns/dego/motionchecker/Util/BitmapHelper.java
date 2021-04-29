package jp.mydns.dego.motionchecker.Util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import jp.mydns.dego.motionchecker.InstanceHolder;

public class BitmapHelper {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "BitmapHelper";
//    private static final String IMAGE_FILENAME = "capture.png";
    private static final String IMAGE_EXTERNAL_FILENAME = "MotionImage_DATE.png";

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

//    /**
//     * saveBitmapToCache
//     *
//     * @param bitmap bitmap
//     * @return is succeed
//     */
//    public static boolean saveBitmapToCache(Bitmap bitmap) {
//        DebugLog.d(TAG, "saveBitmapToExternal");
//
//        if (bitmap == null) {
//            return false;
//        }
//
//        try {
//            File bitmapFile = BitmapHelper.getCacheFile();
//            FileOutputStream outStream = new FileOutputStream(bitmapFile);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//        } catch (IOException exception) {
//            exception.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    /**
     * saveBitmapToExternal
     *
     * @param bitmap bitmap
     * @return is succeed
     */
    public static boolean saveBitmapToExternal(Bitmap bitmap) {
        DebugLog.d(TAG, "saveBitmapToExternal");

        if (bitmap == null) {
            return false;
        }

        File bitmapFile;
        String filename = BitmapHelper.getFilename();
        try {
            bitmapFile = BitmapHelper.getExternalFile(filename);
            FileOutputStream outStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        // 画像データとしてAndroidに登録
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = InstanceHolder.getInstance().getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put("_data", bitmapFile.getAbsolutePath());
        DebugLog.v(TAG, "absolute path: " + bitmapFile.getAbsolutePath());
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return true;
    }

//    /**
//     * loadBitmapFromCache
//     *
//     * @return bitmap
//     */
//    public static Bitmap loadBitmapFromCache() {
//        DebugLog.d(TAG, "loadBitmapFromCache");
//
//        try {
//            File bitmapFile = BitmapHelper.getCacheFile();
//            return BitmapFactory.decodeStream(new FileInputStream(bitmapFile));
//        } catch (FileNotFoundException exception) {
//            exception.printStackTrace();
//        }
//        return null;
//    }

    /**
     * createBitmapFromPixels
     *
     * @param pixels pixel values
     * @param width  width
     * @param height height
     * @return bitmap
     */
    public static Bitmap createBitmapFromPixels(int[] pixels, int width, int height) {
        DebugLog.d(TAG, "createBitmapFromPixels");
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

//    /**
//     * getCacheFile
//     *
//     * @return bitmap file in cache directory
//     */
//    private static File getCacheFile() {
//        DebugLog.d(TAG, "getCacheFile");
//
//        Context context = InstanceHolder.getInstance().getApplicationContext();
//        File rootDir = context.getCacheDir();
//
//        return new File(rootDir, IMAGE_FILENAME);
//    }

    /**
     * getExternalFile
     *
     * @return bitmap file in external storage directory
     */
    private static File getExternalFile(String filename) {
        DebugLog.d(TAG, "getExternalFile");

        Context context = InstanceHolder.getInstance().getApplicationContext();
        File rootDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        DebugLog.v(TAG, "Root Dir: " + rootDir.getAbsolutePath());

        return new File(rootDir, filename);
    }

    /**
     * getFilename
     *
     * @return file name
     */
    private static String getFilename() {
        DebugLog.d(TAG, "getFilename");

        String filename;
        Date now = new Date();
        String dateStr = (String) DateFormat.format("yyyyMMdd_kkmmss", now);

        filename = IMAGE_EXTERNAL_FILENAME.replace("DATE", dateStr);
        DebugLog.v(TAG, "File: " + filename);
        return filename;
    }
}
