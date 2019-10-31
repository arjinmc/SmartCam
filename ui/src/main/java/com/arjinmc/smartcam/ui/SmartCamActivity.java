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
import com.arjinmc.smartcam.core.SmartCamConfig;
import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamPreview;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.model.CameraFlashMode;
import com.arjinmc.smartcam.core.model.SmartCamError;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
public class SmartCamActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SmartCamActivity";

    private final int FLASH_MODE_OFF = 0;
    private final int FLASH_MODE_AUTO = 1;
    private final int FLASH_MODE_ON = 2;

    private SmartCam mSmartCam;
    private SmartCamPreview mSmartCamPreview;
    private ImageButton mIbCapture;
    private ImageView mIvSwitchCamera;
    private ImageView mIvSwitchFlashLight;

    private boolean hasCamera, hasFlashLight;
    private int mFlashMode;

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

        mSmartCam = new SmartCam(this);
        mSmartCam.setStateCallback(new SmartCamStateCallback() {
            @Override
            public void onConnected() {
                hasCamera = true;
                connected();
            }

            @Override
            public void onDisconnected() {
                unconnected();
            }

            @Override
            public void onError(SmartCamError error) {
                unconnected();
            }
        });

        mSmartCam.setCaptureCallback(new SmartCamCaptureCallback() {
            @Override
            public void onSuccessPath(String path) {
                SmartCamLog.i(TAG, "CaptureCallback onSuccessPath:" + path);
            }

            @Override
            public void onSuccessUri(String uri) {
                SmartCamLog.i(TAG, "CaptureCallback onSuccessUri:" + uri);
            }

            @Override
            public void onError(SmartCamError smartCamError) {
                SmartCamLog.i(TAG, "CaptureCallback oError:" + smartCamError.toString());
            }
        });
        mSmartCam.open();
    }

    private void connected() {
        hasFlashLight = SmartCamUtils.hasFlashLight(this);

        mSmartCamPreview.setCamera(mSmartCam);
        switchCamera(mSmartCam.isBackCamera());

        if (mSmartCam.getCameraCount() >= 2) {
            mIvSwitchCamera.setVisibility(View.VISIBLE);
        } else {
            mIvSwitchCamera.setVisibility(View.GONE);
        }
    }

    private void unconnected() {
        if (!hasCamera) {
            Toast.makeText(this, R.string.smartcam_no_camera_tips, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onClick(View v) {

        if (!hasCamera) {
            return;
        }

        int viewId = v.getId();
        if (viewId == R.id.smartcam_btn_capture) {
            mSmartCam.capture(createNewFile());
            return;
        }

        if (viewId == R.id.smartcam_iv_switch_camera) {
            //prevent from switching camera frequently
            if (mSmartCam.isLock()) {
                return;
            }
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
            switchFlashMode();
            return;
        }
    }

    /**
     * switch camera ui front or back
     *
     * @param isBack back camera
     */
    private void switchCamera(boolean isBack) {
        if (isBack) {
            mIvSwitchCamera.setImageResource(R.drawable.smartcam_ic_switch_front);
            if (hasFlashLight) {
                resetFlashMode();
                mIvSwitchFlashLight.setVisibility(View.VISIBLE);
            }
        } else {
            mIvSwitchCamera.setImageResource(R.drawable.smartcam_ic_switch_back);
            mIvSwitchFlashLight.setVisibility(View.GONE);
        }
    }

    private void switchFlashMode() {
        mFlashMode++;
        if (mFlashMode >= 3) {
            mFlashMode = 0;
        }
        switch (mFlashMode) {
            case FLASH_MODE_OFF:
                switchFlashModeUI(CameraFlashMode.MODE_OFF);
                mSmartCam.closeFlashMode();
                break;
            case FLASH_MODE_AUTO:
                switchFlashModeUI(CameraFlashMode.MODE_AUTO);
                mSmartCam.autoFlashMode();
                break;
            case FLASH_MODE_ON:
                switchFlashModeUI(CameraFlashMode.MODE_TORCH);
                mSmartCam.torchFlashMode();
                break;
            default:
                break;
        }
    }

    /**
     * switch flash mode ui
     *
     * @param mode
     */
    private int switchFlashModeUI(int mode) {

        switch (mode) {
            case CameraFlashMode.MODE_AUTO:
                mIvSwitchFlashLight.setImageResource(R.drawable.smartcam_ic_flash_auto);
                return FLASH_MODE_AUTO;
            case CameraFlashMode.MODE_TORCH:
                mIvSwitchFlashLight.setImageResource(R.drawable.smartcam_ic_flash_on);
                return FLASH_MODE_ON;
            case CameraFlashMode.MODE_OFF:
                mIvSwitchFlashLight.setImageResource(R.drawable.smartcam_ic_flash_off);
                return FLASH_MODE_OFF;
            default:
                return FLASH_MODE_OFF;
        }
    }

    private void resetFlashMode() {
        mFlashMode = switchFlashModeUI(CameraFlashMode.MODE_OFF);
    }

    private File createNewFile() {
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = "IMAGE_" + simpleFormatter.format(new Date()) + ".jpeg";
        File file = new File(SmartCamConfig.getRootDirPath() + File.separator + fileName);
        if (!file.exists()) {
            try {
                boolean result = file.createNewFile();
                if (result) {
                    return file;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartCamPreview.pause();
        if (hasFlashLight) {
            resetFlashMode();
        }
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
