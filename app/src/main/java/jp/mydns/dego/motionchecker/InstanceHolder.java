package jp.mydns.dego.motionchecker;

import android.app.Application;

import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.Util.PermissionManager;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoController;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoDecoder;
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
    private VideoDecoder videoDecoder = null;
    private PermissionManager permissionManager = null;

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

    /**
     * getVideoDecoder
     *
     * @return VideoDecoder
     */
    public VideoDecoder getVideoDecoder() {
        if (this.videoDecoder == null) {
            this.videoDecoder = new VideoDecoder();
        }
        return this.videoDecoder;
    }

    /**
     * getPermissionManager
     *
     * @return PermissionManager
     */
    public PermissionManager getPermissionManager() {
        if (this.permissionManager == null) {
            this.permissionManager = new PermissionManager();
        }
        return this.permissionManager;
    }
}
