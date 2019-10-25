package com.arjinmc.smartcam.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.smartcam.core.SmartCam;
import com.arjinmc.smartcam.core.SmartCamOrientationListener;
import com.arjinmc.smartcam.core.SmartCamPreview;

/**
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
public class SmartCamActivity extends AppCompatActivity implements View.OnClickListener {

    private SmartCam mSmartCam;
    private SmartCamPreview mSmartCamPreview;
    private ImageButton mIbCapture;
    private SmartCamOrientationListener mSmartCamOrientationListener = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smartcam_act_main);

        mSmartCamPreview = findViewById(R.id.smartcam_preview);
        mIbCapture = findViewById(R.id.smartcam_btn_capture);
        mIbCapture.setOnClickListener(this);

        mSmartCamOrientationListener = new SmartCamOrientationListener(this, mSmartCamPreview);
        mSmartCamOrientationListener.enable();

        mSmartCam = new SmartCam();
        boolean isOpen = mSmartCam.open();
        if (isOpen) {
            mSmartCamPreview.setCamera(mSmartCam);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.smartcam_btn_capture) {
            mSmartCam.capture();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause", "onPause");
        mSmartCamOrientationListener.disable();
        mSmartCamPreview.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume", "onResume");
        mSmartCamOrientationListener.enable();
        mSmartCamPreview.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartCamOrientationListener.disable();
        mSmartCam.close();
    }
}
