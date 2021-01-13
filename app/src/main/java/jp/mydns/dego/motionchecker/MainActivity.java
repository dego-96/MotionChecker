package jp.mydns.dego.motionchecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.Util.FilePathHelper;
import jp.mydns.dego.motionchecker.Util.PermissionManager;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoController;

public class MainActivity extends AppCompatActivity {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MainActivity";
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 10;
    public static final int REQUEST_GALLERY = 20;

    // ---------------------------------------------------------------------------------------------
    // Private Fields
    // ---------------------------------------------------------------------------------------------
    private PermissionManager permissionManager;

    // ---------------------------------------------------------------------------------------------
    // Android Lifecycle
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
        setContentView(R.layout.activity_main);
        this.hideSystemUI();

        this.permissionManager = PermissionManager.getInstance();
    }

    /**
     * onStart
     */
    @Override
    public void onStart() {
        DebugLog.d(TAG, "onStart");
        super.onStart();
    }

    /**
     * onResume
     */
    @Override
    public void onResume() {
        DebugLog.d(TAG, "onResume");
        super.onResume();

        if (!this.permissionManager.getPermission(Manifest.permission.READ_EXTERNAL_STORAGE) &&
            this.permissionManager.checkReadExternalStorage(this) == PermissionManager.PermissionResult.DENIED) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
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

        PermissionManager.clearDenyCount();
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

        switch (requestCode) {
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                requestPermission(resultCode);
                break;

            case REQUEST_GALLERY:
                requestGalleryResult(resultCode, data);
                break;

            default:
                DebugLog.w(TAG, "unknown request code");
                break;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Public Method
    // ---------------------------------------------------------------------------------------------

    /**
     * onVideoSelectButtonClicked
     *
     * @param view button
     */
    public void onVideoSelectButtonClicked(View view) {
        DebugLog.d(TAG, "onVideoSelectButtonClicked");
        if (permissionManager.getPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        } else {
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    public void onPlayButtonClicked(View view) {
        DebugLog.d(TAG, "onPlayButtonClicked");
        VideoController.getInstance().play();
    }

    public void onStopButtonClicked(View view) {
        DebugLog.d(TAG, "onStopButtonClicked");
        VideoController.getInstance().stop();
    }

    public void onSpeedUpButtonClicked(View view) {
        DebugLog.d(TAG, "onSpeedUpButtonClicked");
        VideoController.getInstance().speedUp();
    }

    public void onSpeedDownButtonClicked(View view) {
        DebugLog.d(TAG, "onSpeedDownButtonClicked");
        VideoController.getInstance().speedDown();
    }

    public void onNextFrameButtonClicked(View view) {
        DebugLog.d(TAG, "onNextFrameButtonClicked");
        VideoController.getInstance().nextFrame();
    }

    public void onPreviousFrameButtonClicked(View view) {
        DebugLog.d(TAG, "onPreviousFrameButtonClicked");
        VideoController.getInstance().previousFrame();
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * hideSystemUI
     */
    private void hideSystemUI() {
        DebugLog.d(TAG, "hideSystemUI");
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * requestPermission
     *
     * @param resultCode result code
     */
    private void requestPermission(int resultCode) {
        DebugLog.d(TAG, "requestPermission");
        if (resultCode != Activity.RESULT_OK) {
            DebugLog.w(TAG, "Result code is not OK.");
            return;
        }

        if (this.permissionManager.checkReadExternalStorage(this) != PermissionManager.PermissionResult.GRANTED) {
            Toast.makeText(
                this,
                getString(R.string.toast_no_permission),
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * requestGalleryResult
     *
     * @param resultCode result code
     * @param data       data
     */
    private void requestGalleryResult(int resultCode, Intent data) {
        DebugLog.d(TAG, "requestGalleryResult");

        if (resultCode != Activity.RESULT_OK) {
            DebugLog.w(TAG, "Result code is not OK.");
            return;
        }

        if (!this.permissionManager.getPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DebugLog.e(TAG, "Can not read external storage.");
            return;
        }
        String videoPath = FilePathHelper.getVideoPathFromUri(this, data);

        if (videoPath == null || "".equals(videoPath)) {
            Toast.makeText(getApplication(), getString(R.string.toast_no_video), Toast.LENGTH_SHORT).show();
        } else {
            DebugLog.d(TAG, "video path :" + videoPath);
            VideoController.getInstance().setVideoPath(videoPath);
        }

    }
}
