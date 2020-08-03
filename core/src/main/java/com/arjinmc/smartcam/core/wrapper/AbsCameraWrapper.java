package com.arjinmc.smartcam.core.wrapper;

import android.content.Context;

import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.model.CameraType;

import java.io.File;
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
    public List<CameraSupportPreviewSize> getSupperPreviewSizes() {
        return null;
    }

    @Override
    public CameraSupportPreviewSize getCompatPreviewSize(int width, int height) {

//        List<CameraSupportPreviewSize> supportPreviewSizes = getSupperPreviewSizes();
//        if (supportPreviewSizes == null || supportPreviewSizes.isEmpty()) {
//            return null;
//        }
//
//        if (height == 0) {
//            return null;
//        }
//
////        Log.e("SurfaceTexture", width + "/" + height);
//        int postion = -1;
//        double minOffset = 0;
//        int size = supportPreviewSizes.size();
//        for (int i = 0; i < size; i++) {
//            CameraSupportPreviewSize cameraSupportPreviewSize = supportPreviewSizes.get(i);
//            double offset = Math.abs(cameraSupportPreviewSize.getWidth() / cameraSupportPreviewSize.getHeight() - width / height);
////            Log.e("PreviewSize", cameraSupportPreviewSize.getWidth() + "/" + cameraSupportPreviewSize.getHeight());
//            if (offset == 0) {
//                postion = i;
//                break;
//            }
//
//            if (minOffset == 0) {
//                minOffset = offset;
//                postion = i;
//            }
//            if (minOffset > offset) {
//                minOffset = offset;
//                postion = i;
//            }
//        }
//        if (postion == -1) {
//            return null;
//        }
//        return supportPreviewSizes.get(postion);
        return null;
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
