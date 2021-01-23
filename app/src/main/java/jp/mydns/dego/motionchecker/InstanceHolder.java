package jp.mydns.dego.motionchecker;

import android.app.Application;

import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoController;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoRunnable;
import jp.mydns.dego.motionchecker.View.ViewController;

public class InstanceHolder extends Application {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "InstanceHolder";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private static InstanceHolder instance = null;
    private ViewController viewController = null;
    private VideoController videoController = null;
    private VideoRunnable videoRunnable = null;

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * onCreate
     */
    @Override
    public void onCreate() {
        DebugLog.d(TAG, "onCreate");
        super.onCreate();

        instance = this;
    }

    /**
     * getInstance
     *
     * @return Application context
     */
    public static InstanceHolder getInstance() {
        return instance;
    }

    /**
     * ViewController
     *
     * @return ViewController
     */
    public ViewController getViewController() {
        if (this.viewController == null) {
            this.viewController = new ViewController();
        }
        return this.viewController;
    }

    /**
     * getVideoController
     *
     * @return VideoController
     */
    public VideoController getVideoController() {
        if (this.videoController == null) {
            this.videoController = new VideoController();
        }
        return this.videoController;
    }

//    /**
//     * getVideoRunnable
//     *
//     * @return VideoRunnable
//     */
//    public VideoRunnable getVideoRunnable() {
//        if (this.videoRunnable == null) {
//            this.videoRunnable = new VideoRunnable();
//        }
//        return this.videoRunnable;
//    }
}
