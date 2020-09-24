package com.arjinmc.smartcam.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.smartcam.core.SmartCam;
import com.arjinmc.smartcam.core.SmartCamConfig;
import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamPreview;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.file.SmartCamFileUtils;
import com.arjinmc.smartcam.core.model.CameraFlashMode;
import com.arjinmc.smartcam.core.model.SmartCamCaptureResult;
import com.arjinmc.smartcam.core.model.SmartCamError;
import com.arjinmc.smartcam.core.model.SmartCamPreviewError;
import com.arjinmc.smartcam.ui.R;
import com.arjinmc.smartcam.ui.widget.MenuPopupWindow;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
public class SmartCamComplexActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SmartCamComplexActivity";

    private final int FLASH_MODE_OFF = 0;
    private final int FLASH_MODE_AUTO = 1;
    private final int FLASH_MODE_ON = 2;

    private SmartCam mSmartCam;
    private SmartCamPreview mSmartCamPreview;
    private ImageButton mIbCapture;
    private ImageView mIvSwitchCamera;
    private ImageView mIvSwitchFlashLight;
    private ImageView mIvMenu;

    private RelativeLayout mViewRoot;
    private MenuPopupWindow mMenuPopupWindow;
    private boolean hasCamera, hasFlashLight;
    private int mFlashMode;
    private File mFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smartcam_act_complex);
        initView();
        initListener();
        initData();

    }

    private void initView() {

        mSmartCamPreview = findViewById(R.id.smartcam_preview);
        mIbCapture = findViewById(R.id.smartcam_btn_capture);
        mIvSwitchCamera = findViewById(R.id.smartcam_iv_switch_camera);
        mIvSwitchFlashLight = findViewById(R.id.smartcam_iv_switch_flash);
        mIvMenu = findViewById(R.id.smartcam_iv_menu);

        mViewRoot = findViewById(R.id.smartcam_view_root);
        mMenuPopupWindow = new MenuPopupWindow(this);
    }

    private void initListener() {
        mIbCapture.setOnClickListener(this);
        mIvSwitchCamera.setOnClickListener(this);
        mIvSwitchFlashLight.setOnClickListener(this);
        mIvMenu.setOnClickListener(this);
    }

    private void initData() {

        SmartCamConfig.getInstance().setAutoReset(true);

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
                if (error instanceof SmartCamPreviewError) {
                    mSmartCam.open();
                }
            }
        });

        mSmartCam.setCaptureCallback(new SmartCamCaptureCallback() {

            @Override
            public void onSuccess(SmartCamCaptureResult smartCamCaptureResult) {
                long time = System.currentTimeMillis();
                SmartCamUtils.dealAndSaveJpegFile(smartCamCaptureResult, mFile);
                SmartCamLog.i(TAG, "CaptureCallback time:" + (System.currentTimeMillis() - time) + "ms");
                SmartCamLog.i(TAG, "CaptureCallback onSuccessPath:" + mFile.getAbsolutePath());

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

        saveCameraInfoIfNeed();
    }

    /**
     * save static camera info
     */
    private void saveCameraInfoIfNeed() {

        List<String> list = mSmartCam.getSupportPreviewSizeRatioList();
        for (String li : list) {
            Log.e("list", li);
        }
    }

    private void unconnected() {
        if (!hasCamera) {
            Toast.makeText(this, R.string.smartcam_no_camera_tips, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {

        if (!hasCamera) {
            return;
        }

        int viewId = v.getId();
        //capture a photo
        if (viewId == R.id.smartcam_btn_capture) {

            mFile = createNewFile();
            mSmartCam.capture();
            return;
        }

        //switch camera
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

        if (viewId == R.id.smartcam_iv_menu) {
//            mMenuPopupWindow.show(mViewRoot);
//            startActivity(new Intent(SmartCamComplexActivity.this, SmartCamSettingActivity.class));
            //test for preview ratio
            setPreviewRatio("16:9");

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

    private void setPreviewRatio(String previewRatio) {
        if (mSmartCam != null) {
            mSmartCam.setPreviewRatio(previewRatio);
            //remember to restart smartcam
            mSmartCam.restart();
            mSmartCamPreview.restart();
        }
    }

    private File createNewFile() {

        File file = new File(SmartCamConfig.getInstance().getRootDirPath() + File.separator + createNewFileName());
        if (!file.exists()) {
            try {
                Log.e("file path", file.getAbsolutePath());
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String createNewFileUri() {
        return SmartCamFileUtils.createFileByUri(this, SmartCamConfig.getInstance().getRootDirPath()
                , "image", createNewFileName());
    }

    private String createNewFileName() {
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "IMAGE_" + simpleFormatter.format(new Date()) + ".jpeg";
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasFlashLight) {
            resetFlashMode();
        }
        if (mSmartCam != null) {
            mSmartCam.pause();
        }
        if (mSmartCamPreview != null) {
            mSmartCamPreview.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSmartCam != null) {
            mSmartCam.resume();
        }
        if (mSmartCamPreview != null) {
            mSmartCamPreview.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mSmartCam != null) {
            mSmartCam.release();
        }
        if (mSmartCamPreview != null) {
            mSmartCamPreview.release();
        }
        super.onDestroy();
    }
}