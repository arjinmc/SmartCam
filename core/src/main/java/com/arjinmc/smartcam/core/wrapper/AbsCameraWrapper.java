package com.arjinmc.smartcam.core.wrapper;

import android.content.Context;

import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.comparator.CompareSizesByArea;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraType;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Abstact Camera Wrapper
 * define camera with functions
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class AbsCameraWrapper implements ICameraWrapper {

    protected Context mContext;

    /**
     * mark current camera type
     */
    protected int mCurrentCameraType = CameraType.CAMERA_NULL;
    protected String mCurrentCameraId = "-1";

    protected SmartCamStateCallback mSmartCamStateCallback;
    protected SmartCamCaptureCallback mSmartCamCaptureCallback;
    protected OnClickCaptureLisenter mOnClickCaptureLisenter;

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public Object getCameraWrapper() {
        return null;
    }

    @Override
    public Object getCamera() {
        return null;
    }

    @Override
    public void open() {

    }

    @Override
    public boolean resumeOpen() {
        return false;
    }


    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public int getCameraCount() {
        return 0;
    }

    @Override
    public void capture(File file) {
        if (mOnClickCaptureLisenter != null) {
            mOnClickCaptureLisenter.onCapture(file);
        }
    }

    @Override
    public void capturePath(String filePath) {
        if (mOnClickCaptureLisenter != null) {
            mOnClickCaptureLisenter.onCapturePath(filePath);
        }
    }

    @Override
    public void captureUri(String fileUri) {
        if (mOnClickCaptureLisenter != null) {
            mOnClickCaptureLisenter.onCaptureUri(fileUri);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void logFeatures() {

    }

    @Override
    public void openFlashMode() {
    }

    @Override
    public void closeFlashMode() {

    }

    @Override
    public void torchFlashMode() {

    }

    @Override
    public void autoFlashMode() {

    }

    @Override
    public int getFlashMode() {
        return 0;
    }

    @Override
    public boolean isBackCamera() {
        return true;
    }

    @Override
    public void switchToFrontCamera() {

    }

    @Override
    public void switchToBackCamera() {

    }

    @Override
    public boolean isLock() {
        return false;
    }

    @Override
    public int getCurrentCameraType() {
        return 0;
    }

    @Override
    public String getCurrentCameraId() {
        return mCurrentCameraId;
    }


    @Override
    public List<CameraSize> getSupperPreviewSizes() {
        return null;
    }

    @Override
    public CameraSize getCompatPreviewSize(int width, int height) {
        return null;
    }

    @Override
    public List<CameraSize> getOutputSizes() {
        return null;
    }

    @Override
    public CameraSize getMaxOutputSize() {
        List<CameraSize> cameraSizes = getOutputSizes();
        if (cameraSizes == null || cameraSizes.isEmpty()) {
            return null;
        }
        CameraSize largest = Collections.max(cameraSizes, new CompareSizesByArea());
        return largest;
    }

    @Override
    public int getOrientation() {
        return 0;
    }

    @Override
    public int getZoom() {
        return 0;
    }

    @Override
    public void setZoom(int zoomLevel) {

    }

    @Override
    public void setStateCallback(SmartCamStateCallback smartCamStateCallback) {
        mSmartCamStateCallback = smartCamStateCallback;
    }

    @Override
    public void setCaptureCallback(SmartCamCaptureCallback smartCamCaptureCallback) {
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    @Override
    public SmartCamCaptureCallback getCaptureCallback() {
        return mSmartCamCaptureCallback;
    }


    @Override
    public boolean hasFocusAuto() {
        return false;
    }

    public void setOnClickCaptureLisenter(OnClickCaptureLisenter onClickCaptureLisenter) {
        mOnClickCaptureLisenter = onClickCaptureLisenter;
    }

    /**
     * listener for capture photo
     */
    public interface OnClickCaptureLisenter {
        void onCapture(File file);

        void onCapturePath(String filePath);

        void onCaptureUri(String fileUri);
    }

}
