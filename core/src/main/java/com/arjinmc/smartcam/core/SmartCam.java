package com.arjinmc.smartcam.core;


import android.content.Context;
import android.os.Build;

import com.arjinmc.smartcam.core.callback.SmartCamStateListener;
import com.arjinmc.smartcam.core.camera1.Camera1Wrapper;
import com.arjinmc.smartcam.core.camera2.Camera2Wrapper;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;

import java.util.List;

/**
 * SmartCam
 * the camera compat for camera v1 and v2
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class SmartCam extends AbsCameraWrapper {

    private AbsCameraWrapper mCameraWrapper;

    public SmartCam(Context context) {
        setContext(context);

        if (DebugConfig.useV1) {
            mCameraWrapper = new Camera1Wrapper();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraWrapper = new Camera2Wrapper(getContext());
        } else {
            mCameraWrapper = new Camera1Wrapper();
        }
    }

    @Override
    public void open() {
        mCameraWrapper.open();
    }

    @Override
    public void close() {
        mCameraWrapper.close();
    }

    @Override
    public Object getCameraWrapper() {
        return mCameraWrapper;
    }

    @Override
    public Object getCamera() {
        return mCameraWrapper.getCamera();
    }

    @Override
    public int getCameraCount() {
        return mCameraWrapper.getCameraCount();
    }

    @Override
    public int getOrientation() {
        return mCameraWrapper.getOrientation();
    }

    @Override
    public void capture() {
        mCameraWrapper.capture();
    }

    @Override
    public List<CameraSupportPreviewSize> getSupperPreviewSizes() {
        return mCameraWrapper.getSupperPreviewSizes();
    }

    @Override
    public String getCurrentCameraId() {
        return mCameraWrapper.getCurrentCameraId();
    }

    @Override
    public int getCurrentCameraType() {
        return mCameraWrapper.getCurrentCameraType();
    }

    @Override
    public boolean isBackCamera() {
        return mCameraWrapper.isBackCamera();
    }

    @Override
    public void switchToBackCamera() {
        mCameraWrapper.switchToBackCamera();
    }

    @Override
    public void switchToFrontCamera() {
        mCameraWrapper.switchToFrontCamera();
    }

    @Override
    public boolean isLock() {
        return mCameraWrapper.isLock();
    }

    @Override
    public int getZoom() {
        return mCameraWrapper.getZoom();
    }

    @Override
    public void setZoom(int zoomLevel) {
        mCameraWrapper.setZoom(zoomLevel);
    }

    @Override
    public void openFlashMode() {
        mCameraWrapper.openFlashMode();
    }

    @Override
    public void closeFlashMode() {
        mCameraWrapper.closeFlashMode();
    }

    @Override
    public void autoFlashMode() {
        mCameraWrapper.autoFlashMode();
    }

    @Override
    public void torchFlashMode() {
        mCameraWrapper.torchFlashMode();
    }

    @Override
    public int getFlashMode() {
        return mCameraWrapper.getFlashMode();
    }

    @Override
    public void setStateListener(SmartCamStateListener smartCamStateListener) {
        mCameraWrapper.setStateListener(smartCamStateListener);
    }

    @Override
    public void logFeatures() {
        mCameraWrapper.logFeatures();
    }
}
