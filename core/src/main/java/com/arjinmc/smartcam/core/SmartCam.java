package com.arjinmc.smartcam.core;


import android.content.Context;
import android.os.Build;

import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.camera1.Camera1Wrapper;
import com.arjinmc.smartcam.core.camera2.Camera2Wrapper;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraVersion;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;

import java.util.List;
import java.util.Map;

/**
 * SmartCam
 * the camera compat for camera v1 and v2
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class SmartCam extends AbsCameraWrapper {

    private AbsCameraWrapper mCameraWrapper;

    private int mCameraVersion = CameraVersion.VERSION_2;

    public SmartCam(Context context) {
        setContext(context);

        if (DebugConfig.useV1) {
            mCameraWrapper = new Camera1Wrapper();
            mCameraVersion = CameraVersion.VERSION_1;
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraWrapper = new Camera2Wrapper(getContext());
            mCameraVersion = CameraVersion.VERSION_2;
//            try {
//                if (!((Camera2Wrapper) mCameraWrapper).isUseCamera2Better()) {
//                    mCameraWrapper = new Camera1Wrapper();
//                    mCameraVersion = CameraVersion.VERSION_1;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else {
            mCameraWrapper = new Camera1Wrapper();
            mCameraVersion = CameraVersion.VERSION_1;
        }
    }

    @Override
    public void open() {
        mCameraWrapper.open();
    }

    @Override
    public boolean isOpen() {
        return mCameraWrapper.isOpen();
    }

    @Override
    public void close() {
        mCameraWrapper.close();
    }

    @Override
    public void release() {
        mCameraWrapper.release();
    }

    public void restart() {
        release();
        open();
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
    public List<CameraSize> getSupportPreviewSizes() {
        return mCameraWrapper.getSupportPreviewSizes();
    }

    @Override
    public Map<String, List<CameraSize>> getSupportPreviewSizeRatioMap() {
        return mCameraWrapper.getSupportPreviewSizeRatioMap();
    }

    @Override
    public List<String> getSupportPreviewSizeRatioList() {
        return mCameraWrapper.getSupportPreviewSizeRatioList();
    }

    @Override
    public List<CameraSize> getSupportPreviewSizeListByRatio(String ratio) {
        return mCameraWrapper.getSupportPreviewSizeListByRatio(ratio);
    }

    @Override
    public CameraSize getCompatPreviewSizeByRatio(String ratio, int width, int height) {
        return mCameraWrapper.getCompatPreviewSizeByRatio(ratio, width, height);
    }

    @Override
    public CameraSize getCompatPreviewSize(int width, int height) {
        return mCameraWrapper.getCompatPreviewSize(width, height);
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
    public boolean isZoomAvailable() {
        return mCameraWrapper.isZoomAvailable();
    }

    @Override
    public int getZoom() {
        return mCameraWrapper.getZoom();
    }

    @Override
    public void setZoom(int zoomLevel) {
        if (zoomLevel < 0) {
            zoomLevel = 0;
        }
        mCameraWrapper.setZoom(zoomLevel);
    }

    @Override
    public int getMaxZoom() {
        return mCameraWrapper.getMaxZoom();
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
    public void setStateCallback(SmartCamStateCallback smartCamStateCallback) {
        mCameraWrapper.setStateCallback(smartCamStateCallback);
    }

    @Override
    public void setCaptureCallback(SmartCamCaptureCallback smartCamCaptureCallback) {
        mCameraWrapper.setCaptureCallback(smartCamCaptureCallback);
    }

    @Override
    public SmartCamCaptureCallback getCaptureCallback() {
        return mCameraWrapper.getCaptureCallback();
    }

    @Override
    public void setPreviewRatio(String previewRatio) {
        mCameraWrapper.setPreviewRatio(previewRatio);
    }

    @Override
    public String getPreviewRatio() {
        return mCameraWrapper.getPreviewRatio();
    }

    @Override
    public void logFeatures() {
        mCameraWrapper.logFeatures();
    }

    public int getCameraVersion() {
        return mCameraVersion;
    }
}
