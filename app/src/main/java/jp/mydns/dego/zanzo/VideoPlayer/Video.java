package jp.mydns.dego.zanzo.VideoPlayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import jp.mydns.dego.zanzo.BuildConfig;
import jp.mydns.dego.zanzo.InstanceHolder;
import jp.mydns.dego.zanzo.Util.DebugLog;

public class Video {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "Video";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final Uri uri;
    private final int width;
    private final int height;
    private int duration;
    private final int rotation;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------
    public Video(Uri uri) {
        this.uri = uri;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Context context = InstanceHolder.getInstance().getApplicationContext();
        retriever.setDataSource(context, uri);

        this.width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        this.height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        this.rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        this.duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        if (BuildConfig.DEBUG) {
            logMetaData(retriever);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * getUri
     *
     * @return uri
     */
    public Uri getUri() {
        return this.uri;
    }

    /**
     * getWidth
     *
     * @return width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * getHeight
     *
     * @return height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * getDuration
     *
     * @return duration
     */
    public int getDuration() {
        return this.duration;
    }

    /**
     * getRotation
     *
     * @return rotation
     */
    public int getRotation() {
        return this.rotation;
    }

    /**
     * setDuration
     *
     * @param duration duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

    /**
     * logMetaData
     *
     * @param retriever media meta data retriever
     */
    private void logMetaData(MediaMetadataRetriever retriever) {
        DebugLog.d(TAG, "logMetaData");

        DebugLog.d(TAG, "==================================================");
        DebugLog.d(TAG, "has audio  :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO));
        DebugLog.d(TAG, "has video  :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO));
        DebugLog.d(TAG, "date       :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
        DebugLog.d(TAG, "width      :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        DebugLog.d(TAG, "height     :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        DebugLog.d(TAG, "duration   :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        DebugLog.d(TAG, "rotation   :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        DebugLog.d(TAG, "num tracks :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS));
        DebugLog.d(TAG, "title      :" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        DebugLog.d(TAG, "==================================================");
    }
}
