package jp.mydns.dego.motionchecker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import jp.mydns.dego.motionchecker.Util.DebugLog;

public class MainActivity extends AppCompatActivity {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "MainActivity";

    // ---------------------------------------------------------------------------------------------
    // Android Lifecycle
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        DebugLog.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        DebugLog.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        DebugLog.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        DebugLog.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        DebugLog.d(TAG, "onDestroy");
        super.onDestroy();
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

}
