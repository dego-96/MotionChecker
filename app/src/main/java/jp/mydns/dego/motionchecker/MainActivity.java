package jp.mydns.dego.motionchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.Util.PermissionManager;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoController;
import jp.mydns.dego.motionchecker.VideoPlayer.VideoDecoder;

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
        setContentView(R.layout.activity_main);

        this.getVideoController().setViews(this);
        this.getVideoController().bindDisplay(this.getWindowManager().getDefaultDisplay());
        if (!this.getVideoController().isVideoStandby()) {
            this.getVideoController().setVisibilities(VideoDecoder.DecoderStatus.INIT);
        }
    }

    /**
     * onStart
     */
    @Override
    public void onStart() {
        DebugLog.d(TAG, "onStart");
        super.onStart();
        this.hideSystemUI();
    }

    /**
     * onResume
     */
    @Override
    public void onResume() {
        DebugLog.d(TAG, "onResume");
        super.onResume();
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
        DebugLog.d(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (permissions.length <= 0 ||
                !Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[0]) ||
                grantResults.length <= 0 ||
                grantResults[0] == PackageManager.PERMISSION_DENIED
            ) {
                Toast.makeText(
                    this,
                    this.getString(R.string.toast_no_permission),
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
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * getVideoController
     *
     * @return video controller
     */
    private VideoController getVideoController() {
        return InstanceHolder.getInstance().getVideoController();
    }

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

        if (!InstanceHolder.getInstance().getPermissionManager()
            .getPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DebugLog.e(TAG, "Can not read external storage.");
            return;
        }

        Uri uri = data.getData();
        if (!this.getVideoController().setVideo(uri)) {
            Toast.makeText(getApplication(), getString(R.string.toast_no_video), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * videoSelect
     */
    private void videoSelect() {
        DebugLog.d(TAG, "videoSelect");

        PermissionManager permissionManager = InstanceHolder.getInstance().getPermissionManager();

        if (permissionManager.getPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            this.getVideoController().join();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_GALLERY);
        } else {
            if (!permissionManager.requestPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, getString(R.string.toast_no_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

}
