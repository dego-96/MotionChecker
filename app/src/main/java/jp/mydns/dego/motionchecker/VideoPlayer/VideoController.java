package jp.mydns.dego.motionchecker.VideoPlayer;

import android.media.MediaMetadataRetriever;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class VideoController {

    // ---------------------------------------------------------------------------------------------
    // inner class
    // ---------------------------------------------------------------------------------------------
    public class VideoInfo {
        int width;
        int height;
        int duration;
        int rotation;

        private VideoInfo() {
            this.width = 0;
            this.height = 0;
            this.duration = 0;
            this.rotation = 0;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getRotation() {
            return this.rotation;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
//    public static final double[] VIDEO_SPEEDS = {
//        1.000,  // 1/1
//        0.500,  // 1/2
//        0.250,  // 1/4
//        0.125,  // 1/8
//        0.100,  // 1/10
//    };

    private static final String TAG = "VideoController";
    private VideoInfo info;
//    private static final int SPEED_SLOWEST = VIDEO_SPEEDS.length - 1;
//    private static final int SPEED_FASTEST = 0;

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private String filePath;
//    private Thread videoThread;
//    private int speedLevel;

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * VideoController
     */
    public VideoController() {
        DebugLog.d(TAG, "VideoController");
        this.filePath = null;
//        this.videoThread = null;
//        this.speedLevel = 0;
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * setVideoPath
     *
     * @param path video file path
     */
    public void setVideoPath(String path) {
        DebugLog.d(TAG, "setVideoPath");
        this.filePath = path;
        DebugLog.v(TAG, "path : " + this.filePath);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        logMetaData(retriever);

        this.info = new VideoInfo();
        this.info.width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        this.info.height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        this.info.rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
    }

    /**
     * getVideoInfo
     *
     * @return video meta data
     */
    public VideoInfo getVideoInfo() {
        return this.info;
    }

    /**
     * isStandby
     *
     * @return video controller is standby.
     */
    public boolean isStandby() {
        return (this.filePath != null && !"".equals(this.filePath));
    }

    /**
     * play
     */
    public void play() {
        DebugLog.d(TAG, "play");

    }

//    /**
//     * pause
//     */
//    public void pause() {
//        DebugLog.d(TAG, "pause");
//
//    }

    /**
     * stop
     */
    public void stop() {
        DebugLog.d(TAG, "stop");

    }

//    /**
//     * seek
//     */
//    public void seek(long seekTo) {
//        DebugLog.d(TAG, "seek");
//
//    }

    /**
     * speedUp
     */
    public void speedUp() {
        DebugLog.d(TAG, "speedUp");

    }

    /**
     * speedDown
     */
    public void speedDown() {
        DebugLog.d(TAG, "speedDown");

    }

    /**
     * nextFrame
     */
    public void nextFrame() {
        DebugLog.d(TAG, "nextFrame");

    }

    /**
     * previousFrame
     */
    public void previousFrame() {
        DebugLog.d(TAG, "previousFrame");
    }

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
