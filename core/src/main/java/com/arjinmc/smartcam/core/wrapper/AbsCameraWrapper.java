package com.arjinmc.smartcam.core.wrapper;

import android.content.Context;

import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.comparator.CompareSizesByArea;
import com.arjinmc.smartcam.core.model.CameraAspectRatio;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    protected OnClickCaptureListener mOnClickCaptureListener;

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
    public void capture() {
        if (mOnClickCaptureListener != null) {
            mOnClickCaptureListener.onCapture();
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
    public void release() {
        if (mSmartCamCaptureCallback != null) {
            mSmartCamCaptureCallback = null;
        }
        if (mSmartCamStateCallback != null) {
            mSmartCamStateCallback = null;
        }
        if (mOnClickCaptureListener != null) {
            mOnClickCaptureListener = null;
        }
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
    public List<CameraSize> getSupportPreviewSizes() {
        return null;
    }

    @Override
    public Map<String, List<CameraSize>> getSupportPreviewSizeRatioMap() {
        List<CameraSize> supportPreviewSizeList = getSupportPreviewSizes();
        if (supportPreviewSizeList == null) {
            return null;
        }
        Map<String, List<CameraSize>> result = new HashMap<>();
        for (CameraSize previewSize : supportPreviewSizeList) {
            CameraAspectRatio aspectRatio = new CameraAspectRatio();
            aspectRatio.toRatio(previewSize.getWidth(), previewSize.getHeight());

            List<CameraSize> cameraSizeList = result.get(aspectRatio.getName());
            if (cameraSizeList == null) {
                cameraSizeList = new ArrayList<>();
            }
            cameraSizeList.add(previewSize);
            result.put(aspectRatio.getName(), cameraSizeList);
        }
        return result;
    }

    @Override
    public List<String> getSupportPreviewSizeRatioList() {
        Map<String, List<CameraSize>> supportPreviewSizeRatioMap = getSupportPreviewSizeRatioMap();
        if (supportPreviewSizeRatioMap == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        Set<String> keySet = supportPreviewSizeRatioMap.keySet();
        for (String key : keySet) {
            result.add(key);
        }
        return result;
    }

    @Override
    public List<CameraSize> getSupportPreviewSizeListByRatio(String ratio) {

        if (ratio == null) {
            return null;
        }

        Map<String, List<CameraSize>> ratioMap = getSupportPreviewSizeRatioMap();
        if (ratioMap != null && ratioMap.containsKey(ratio)) {
            return ratioMap.get(ratio);
        }
        return null;
    }

    @Override
    public CameraSize getCompatPreviewSizeByRatio(String ratio, int width, int height) {

        if (ratio == null) {
            return null;
        }
        List<CameraSize> previewSizeList = getSupportPreviewSizeListByRatio(ratio);
        if (previewSizeList == null || previewSizeList.isEmpty()) {
            return null;
        }
        for (CameraSize cameraSize : previewSizeList) {
            if (width <= cameraSize.getWidth() && height <= cameraSize.getHeight()) {
                return cameraSize;
            }
        }
        return previewSizeList.get(0);
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
    public boolean canFocusAuto() {
        return false;
    }

    @Override
    public boolean canManualFocus() {
        return false;
    }

    public void setOnClickCaptureListener(OnClickCaptureListener onClickCaptureListener) {
        mOnClickCaptureListener = onClickCaptureListener;
    }

    /**
     * listener for capture photo
     */
    public interface OnClickCaptureListener {
        void onCapture();
    }

}
