package com.arjinmc.smartcam.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.smartcam.core.SmartCam;
import com.arjinmc.smartcam.core.SmartCamPreview;

/**
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
public class SmartCamActivity extends AppCompatActivity implements View.OnClickListener {

    private SmartCam mSmartCam;
    private SmartCamPreview mSmartCamPreview;
    private ImageButton mIbCapture;
    private ImageView mIvSwitchCamera;
    private ImageView mIvSwitchFlashLight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.smartcam_act_main);
        initView();
        initListener();
        initData();
    }

    private void initView() {

        mSmartCamPreview = findViewById(R.id.smartcam_preview);
        mIbCapture = findViewById(R.id.smartcam_btn_capture);
        mIvSwitchCamera = findViewById(R.id.smartcam_iv_switch_camera);
        mIvSwitchFlashLight = findViewById(R.id.smartcam_iv_switch_flash);
    }

    private void initListener() {
        mIbCapture.setOnClickListener(this);
        mIvSwitchCamera.setOnClickListener(this);
        mIvSwitchFlashLight.setOnClickListener(this);
    }

    private void initData() {
        mSmartCam = new SmartCam();
        boolean isOpen = mSmartCam.open();

        if (!isOpen) {
            Toast.makeText(this, R.string.smartcam_no_camera_tips, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isOpen) {
            mSmartCamPreview.setCamera(mSmartCam);
            switchCamera(mSmartCam.isBackCamera());
        }

        if (mSmartCam.getCameraCount() >= 2) {
            mIvSwitchCamera.setVisibility(View.VISIBLE);
        } else {
            mIvSwitchCamera.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.smartcam_btn_capture) {
            mSmartCam.capture();
            return;
        }

        if (viewId == R.id.smartcam_iv_switch_camera) {
            if (mSmartCam.isBackCamera()) {
                mSmartCam.switchToFrontCamera();
                switchCamera(false);
            } else {
                mSmartCam.switchToBackCamera();
                switchCamera(true);
            }
            mSmartCamPreview.resume();
            return;
        }

        if (viewId == R.id.smartcam_iv_switch_flash) {
            return;
        }
    }

    /**
     * @param isBack back camera
     */
    private void switchCamera(boolean isBack) {
        if (isBack) {
            mIvSwitchCamera.setImageResource(R.drawable.smartcam_ic_switch_front);
        } else {
            mIvSwitchCamera.setImageResource(R.drawable.smartcam_ic_switch_back);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartCamPreview.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSmartCamPreview.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartCam.close();
    }
}
