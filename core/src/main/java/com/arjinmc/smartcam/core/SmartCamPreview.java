package com.arjinmc.smartcam.core;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.camera1.Camera1Preview;
import com.arjinmc.smartcam.core.camera1.Camera1Wrapper;
import com.arjinmc.smartcam.core.camera2.Camera2Preview;
import com.arjinmc.smartcam.core.camera2.Camera2Wrapper;
import com.arjinmc.smartcam.core.model.CameraVersion;

/**
 * SmartCamPreview
 * Preview for SmartCam
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class SmartCamPreview extends FrameLayout {

    private int mCurrentCameraVersion;
    private SmartCam mSmartCam;
    private Camera1Preview mCamera1Preview;
    private Camera2Preview mCamera2Preview;

    public SmartCamPreview(@NonNull Context context) {
        super(context);
    }

    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCamera(SmartCam smartCam) {
        init(smartCam);
    }

    private void init(SmartCam smartCam) {

        if (smartCam == null) {
            return;
        }

        mSmartCam = smartCam;

        if (getChildCount() != 0) {
            removeAllViews();
        }

        if (DebugConfig.useV1) {
            mCurrentCameraVersion = CameraVersion.VERSION_1;
            mCamera1Preview = new Camera1Preview(getContext(), (Camera1Wrapper) mSmartCam.getCameraWrapper());
            addView(mCamera1Preview);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCurrentCameraVersion = CameraVersion.VERSION_2;
            mCamera2Preview = new Camera2Preview(getContext(), (Camera2Wrapper) mSmartCam.getCameraWrapper());
            addView(mCamera2Preview);
        } else {
            mCurrentCameraVersion = CameraVersion.VERSION_1;
            mCamera1Preview = new Camera1Preview(getContext(), (Camera1Wrapper) mSmartCam.getCameraWrapper());
            addView(mCamera1Preview);
        }
    }

    /**
     * set fixed size for preview
     *
     * @param width
     * @param height
     */
    public void setFixedSize(int width, int height) {
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.getHolder().setFixedSize(width, height);
        }
    }

    /**
     * keep screen on
     *
     * @param screenOn
     */
    @Override
    public void setKeepScreenOn(boolean screenOn) {
        super.setKeepScreenOn(screenOn);
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.getHolder().setKeepScreenOn(screenOn);
        }
    }

    public void resume() {
        init(mSmartCam);
    }

    public void pause() {
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.destroy();
        } else if (mCurrentCameraVersion == CameraVersion.VERSION_2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCamera2Preview.destroy();
        }
    }

    public void preview(){
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.preview();
        } else if (mCurrentCameraVersion == CameraVersion.VERSION_2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCamera2Preview.preview();
        }
    }
}


