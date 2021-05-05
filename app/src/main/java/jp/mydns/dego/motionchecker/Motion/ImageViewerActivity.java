package jp.mydns.dego.motionchecker.Motion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import jp.mydns.dego.motionchecker.R;
import jp.mydns.dego.motionchecker.Util.ActivityHelper;
import jp.mydns.dego.motionchecker.Util.BitmapHelper;
import jp.mydns.dego.motionchecker.Util.DebugLog;

public class ImageViewerActivity extends AppCompatActivity {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "ImageViewerActivity";

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
        this.setContentView(R.layout.activity_image_viewer);

        this.setImage();
    }

    /**
     * onStart
     */
    @Override
    public void onStart() {
        DebugLog.d(TAG, "onStart");
        super.onStart();
        ActivityHelper.hideSystemUI(this);
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
        if (id == R.id.button_back) {
            this.finish();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * setImage
     */
    private void setImage() {
        DebugLog.d(TAG, "setImage");

        Intent intent = this.getIntent();
        if (intent == null) {
            DebugLog.e(TAG, "intent is null.");
            String message = this.getString(R.string.toast_no_motion_image);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            this.finish();
        }

        String filename = intent.getStringExtra(MotionGenerator.INTENT_LAST_SAVED_IMAGE);
        DebugLog.v(TAG, "filename: " + filename);
        Bitmap bitmap = BitmapHelper.loadBitmapFromExternal(filename);
        ImageView imageView = this.findViewById(R.id.image_motion_result);
        imageView.setImageBitmap(bitmap);
    }
}
