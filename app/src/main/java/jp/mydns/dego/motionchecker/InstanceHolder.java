package jp.mydns.dego.motionchecker;

import android.app.Application;

import jp.mydns.dego.motionchecker.Drawer.DrawingManager;
import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.Util.PermissionManager;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoController;

public class InstanceHolder extends Application {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "InstanceHolder";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private static InstanceHolder instance = null;
    private VideoController videoController = null;
    private DrawingManager drawingManager = null;
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
     * getDrawingManager
     *
     * @return DrawingManager
     */
    public DrawingManager getDrawingManager() {
        if (this.drawingManager == null) {
            this.drawingManager = new DrawingManager();
        }
        return this.drawingManager;
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
