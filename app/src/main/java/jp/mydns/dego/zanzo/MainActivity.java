package jp.mydns.dego.zanzo;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import jp.mydns.dego.zanzo.Drawer.DrawItemBase;
import jp.mydns.dego.zanzo.Drawer.DrawingManager;
import jp.mydns.dego.zanzo.Util.ActivityHelper;
import jp.mydns.dego.zanzo.Util.DebugLog;
import jp.mydns.dego.zanzo.Util.NetworkHelper;
import jp.mydns.dego.zanzo.Util.PermissionManager;
import jp.mydns.dego.zanzo.VideoPlayer.VideoController;

public class MainActivity extends Activity {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MainActivity";

    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 10;
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 20;
    public static final int REQUEST_PERMISSION_INTERNET = 30;
    public static final int REQUEST_GALLERY = 40;

    private enum Mode {
        Video,
        Paint,
        Motion,
    }

    // ---------------------------------------------------------------------------------------------
    // Private Fields
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Activity Lifecycle
    // ---------------------------------------------------------------------------------------------

    /**
     * onCreate
     *
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.getVideoController().setViews(this);
        this.getDrawingManager().setViews(this);
    }

    /**
     * onStart
     */
    @Override
    public void onStart() {
        DebugLog.d(TAG, "onStart");
        super.onStart();
        ActivityHelper.hideSystemUI(this);
        this.setMode(Mode.Video);

        this.initAdMobBanner();
    }

    /**
     * onResume
     */
    @Override
    public void onResume() {
        DebugLog.d(TAG, "onResume");
        super.onResume();

        Uri uri = this.getIntent().getData();
        if (uri != null) {
            if (this.getVideoController().setVideo(uri)) {
                DebugLog.v(TAG, "video standby");
            } else {
                Toast.makeText(getApplication(), getString(R.string.toast_no_video), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        DebugLog.d(TAG, "onPause");
        super.onPause();
    }

    /**
     * onStop
     */
    @Override
    public void onStop() {
        DebugLog.d(TAG, "onStop");
        super.onStop();
    }

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {
        DebugLog.d(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * onActivityResult
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        received data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLog.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY) {
            requestGalleryResult(resultCode, data);
        } else {
            DebugLog.w(TAG, "unknown request code");
        }
    }

    /**
     * onRequestPermissionsResult
     *
     * @param requestCode  request code
     * @param permissions  permissions
     * @param grantResults grant result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DebugLog.d(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (permissions.length <= 0 ||
                !Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[0]) ||
                grantResults.length <= 0 ||
                grantResults[0] == PackageManager.PERMISSION_DENIED
            ) {
                Toast.makeText(
                    this,
                    this.getString(R.string.toast_no_permission_ext_read),
                    Toast.LENGTH_LONG
                ).show();
            } else {
                this.videoSelect();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Public Method
    // ---------------------------------------------------------------------------------------------

    /**
     * onButtonClicked
     *
     * @param button button view
     */
    public void onButtonClicked(View button) {
        DebugLog.d(TAG, "onButtonClicked");

        this.networkCheck();

        int id = button.getId();
        if (id == R.id.button_gallery) {
            this.videoSelect();
        } else if (id == R.id.button_play) {
            this.getVideoController().playOrPause();
        } else if (id == R.id.button_stop) {
            this.getVideoController().stop();
        } else if (id == R.id.button_speed_up) {
            this.getVideoController().speedUp();
        } else if (id == R.id.button_speed_down) {
            this.getVideoController().speedDown();
        } else if (id == R.id.button_next_frame) {
            this.getVideoController().nextFrame();
        } else if (id == R.id.button_previous_frame) {
            this.getVideoController().previousFrame();
        } else if (id == R.id.button_move_after) {
            this.getVideoController().moveAfter();
        } else if (id == R.id.button_move_before) {
            this.getVideoController().moveBefore();
        } else if (id == R.id.button_paint) {
            this.setMode(Mode.Paint);
        } else if (id == R.id.button_player) {
            this.setMode(Mode.Video);
        } else if (id == R.id.button_lock) {
            this.getVideoController().viewLock();
        } else if (id == R.id.button_paint_undo) {
            this.getDrawingManager().undo();
        } else if (id == R.id.button_paint_redo) {
            this.getDrawingManager().redo();
//        } else if (id == R.id.button_paint_grid) {
//            this.getDrawingManager().changeGrid();
        } else if (id == R.id.button_paint_line) {
            this.getDrawingManager().setDrawType(DrawItemBase.DrawType.Line);
        } else if (id == R.id.button_paint_rect) {
            this.getDrawingManager().setDrawType(DrawItemBase.DrawType.Rect);
        } else if (id == R.id.button_paint_round) {
            this.getDrawingManager().setDrawType(DrawItemBase.DrawType.Round);
        } else if (id == R.id.button_paint_path) {
            this.getDrawingManager().setDrawType(DrawItemBase.DrawType.Path);
        } else if (id == R.id.button_paint_erase) {
            this.getDrawingManager().clear();
        } else if (id == R.id.button_color_white) {
            this.getDrawingManager().setColor(DrawItemBase.ColorType.White);
        } else if (id == R.id.button_color_red) {
            this.getDrawingManager().setColor(DrawItemBase.ColorType.Red);
        } else if (id == R.id.button_color_green) {
            this.getDrawingManager().setColor(DrawItemBase.ColorType.Green);
        } else if (id == R.id.button_color_blue) {
            this.getDrawingManager().setColor(DrawItemBase.ColorType.Blue);
        } else if (id == R.id.button_color_yellow) {
            this.getDrawingManager().setColor(DrawItemBase.ColorType.Yellow);
        } else if (id == R.id.button_color_black) {
            this.getDrawingManager().setColor(DrawItemBase.ColorType.Black);
        } else if (id == R.id.button_motion_image) {
            this.setMode(Mode.Motion);
        } else if (id == R.id.button_motion_generate) {
            this.generateMotionImage();
        } else if (id == R.id.button_back) {
            this.setMode(Mode.Video);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * generateMotionImage
     */
    private void generateMotionImage() {
        DebugLog.d(TAG, "generateMotionImage");
        if (this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.getVideoController().generateMotionImage();
        }
    }

    /**
     * getVideoController
     *
     * @return video controller
     */
    private VideoController getVideoController() {
        return InstanceHolder.getInstance().getVideoController();
    }

    /**
     * getDrawingManager
     *
     * @return drawing manager
     */
    private DrawingManager getDrawingManager() {
        return InstanceHolder.getInstance().getDrawingManager();
    }

    /**
     * requestGalleryResult
     *
     * @param resultCode result code
     * @param data       data
     */
    private void requestGalleryResult(int resultCode, Intent data) {
        DebugLog.d(TAG, "requestGalleryResult");

        if (resultCode != Activity.RESULT_OK || data == null) {
            DebugLog.i(TAG, "Result code is not OK.");
            Toast.makeText(getApplication(), getString(R.string.toast_no_video), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!this.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DebugLog.e(TAG, "Can not read external storage.");
            return;
        }

        Uri uri = data.getData();
        if (this.getVideoController().setVideo(uri)) {
            DebugLog.v(TAG, "video standby");
        } else {
            Toast.makeText(getApplication(), getString(R.string.toast_no_video), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * videoSelect
     */
    private void videoSelect() {
        DebugLog.d(TAG, "videoSelect");

        if (this.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            this.getVideoController().join();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        } else {
            Toast.makeText(this, this.getString(R.string.toast_no_permission_ext_read), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * setMode
     *
     * @param mode mode
     */
    private void setMode(Mode mode) {
        DebugLog.d(TAG, "setMode");

        if (mode == Mode.Video) {
            this.findViewById(R.id.layout_video_controller).setVisibility(View.VISIBLE);
            this.findViewById(R.id.layout_video_paint).setVisibility(View.GONE);
            this.findViewById(R.id.layout_motion_generator).setVisibility(View.GONE);
        } else if (mode == Mode.Paint) {
            this.findViewById(R.id.layout_video_paint).setVisibility(View.VISIBLE);
            this.findViewById(R.id.layout_video_controller).setVisibility(View.GONE);
            this.findViewById(R.id.layout_motion_generator).setVisibility(View.GONE);
        } else if (mode == Mode.Motion) {
            this.findViewById(R.id.layout_motion_generator).setVisibility(View.VISIBLE);
            this.findViewById(R.id.layout_video_controller).setVisibility(View.GONE);
            this.findViewById(R.id.layout_video_paint).setVisibility(View.GONE);
            this.getVideoController().initMotionGenerator(this);
        }

        this.getDrawingManager().changeDrawable(mode == Mode.Paint);
    }

    /**
     * checkPermission
     *
     * @param permission permission
     * @return check result (is OK)
     */
    private boolean checkPermission(String permission) {
        DebugLog.d(TAG, "checkPermission");
        PermissionManager manager = InstanceHolder.getInstance().getPermissionManager();
        if (manager.getPermission(permission)) {
            return true;
        }

        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                manager.requestPermission(this, permission, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                manager.requestPermission(this, permission, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
                break;
            case Manifest.permission.INTERNET:
                manager.requestPermission(this, permission, REQUEST_PERMISSION_INTERNET);
                break;
            default:
                DebugLog.e(TAG, "invalid permission");
                break;
        }
        return false;
    }

    /**
     * initAdMobBanner
     */
    private void initAdMobBanner() {
        DebugLog.d(TAG, "initAdMobBanner");

        if (this.networkCheck()) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                    DebugLog.d(TAG, "onInitializationComplete");

                    DebugLog.v(TAG, "initializationStatus: " + initializationStatus);
                }
            });

            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            Toast.makeText(this, this.getString(R.string.toast_cannot_access_network), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * networkCheck
     *
     * @return can access network
     */
    private boolean networkCheck() {
        DebugLog.d(TAG, "networkCheck");

        AdView adView = findViewById(R.id.adView);
        if (this.checkPermission(Manifest.permission.INTERNET) && NetworkHelper.networkCheck()) {
            adView.setVisibility(View.VISIBLE);
            return true;
        } else {
            DebugLog.w(TAG, "Can not access network.");
            adView.setVisibility(View.GONE);
            return false;
        }
    }
}
