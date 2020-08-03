package com.arjinmc.smartcam.core.camera1;

import android.hardware.Camera;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.lock.CameraLock;
import com.arjinmc.smartcam.core.model.CameraFlashMode;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraType;
import com.arjinmc.smartcam.core.model.SmartCamOpenError;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.Camera.getCameraInfo;
import static android.hardware.Camera.getNumberOfCameras;

/**
 * Wrapper for camera v1
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class Camera1Wrapper extends AbsCameraWrapper {

    private final String TAG = "Camera1Wrapper";

    private Camera mCamera;
    private CameraLock mCameraLock;

    public Camera1Wrapper() {
        mCameraLock = new CameraLock();
    }

    @Override
    public Camera1Wrapper getCameraWrapper() {
        return this;
    }

    @Override
    public Camera getCamera() {
        return mCamera;
    }

    @Override
    public void open() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }

        mCamera = null;

        try {

            //if camera is not open before
            int numberOfCameras = getNumberOfCameras();
            int frontCameraId = 1;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    //default mCurrentCameraId is back camera id
                    mCurrentCameraId = i + "";
                }

                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frontCameraId = i;
                }
            }

            // attempt to get a Camera instance
            mCamera = Camera.open();
            if (mCamera != null) {
                mCurrentCameraType = CameraType.CAMERA_BACK;

                if (mSmartCamStateCallback != null) {
                    mSmartCamStateCallback.onConnected();
                }
                return;
            } else {
                mCamera = Camera.open(frontCameraId);
                if (mCamera == null) {
                    if (mSmartCamStateCallback != null) {
                        mSmartCamStateCallback.onError(new SmartCamOpenError());
                    }
                    return;
                }
                mCurrentCameraType = CameraType.CAMERA_FRONT;
                mCurrentCameraId = frontCameraId + "";

                if (mSmartCamStateCallback != null) {
                    mSmartCamStateCallback.onConnected();
                }
                return;
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
            if (mSmartCamStateCallback != null) {
                mSmartCamStateCallback.onError(new SmartCamOpenError(e.getMessage()));
            }
            return;
        }
    }

    @Override
    public boolean resumeOpen() {
        try {
            if (mCurrentCameraType != CameraType.CAMERA_NULL) {
                mCamera = Camera.open(Integer.valueOf(mCurrentCameraId));
                if (mCamera != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean isOpen() {
        if (mCamera == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isLock() {
        return mCameraLock.isLock();
    }

    @Override
    public int getCameraCount() {
        return getNumberOfCameras();
    }

    @Override
    public boolean hasFocusAuto() {
        if (mCamera == null) {
            return false;
        }
        // get Camera parameters
        Camera.Parameters params = mCamera.getParameters();

        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            return true;
        }
        return false;
    }

    @Override
    public int getCurrentCameraType() {
        if (mCamera == null) {
            return CameraType.CAMERA_NULL;
        }
        return mCurrentCameraType;
    }

    @Override
    public boolean isBackCamera() {
        if (mCurrentCameraType == CameraType.CAMERA_BACK) {
            return true;
        }
        return false;
    }

    @Override
    public void switchToBackCamera() {
        if (isBackCamera()) {
            return;
        }
        switchCamera(CameraType.CAMERA_BACK);
    }

    @Override
    public void switchToFrontCamera() {
        if (!isBackCamera()) {
            return;
        }
        switchCamera(CameraType.CAMERA_FRONT);
    }

    private void switchCamera(@CameraType.Type int type) {

        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            getCameraInfo(cameraIndex, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }

        mCurrentCameraType = type;
        if (type == CameraType.CAMERA_FRONT && frontIndex != -1) {
            mCurrentCameraId = frontIndex + "";
        } else if (type == CameraType.CAMERA_BACK && backIndex != -1) {
            mCurrentCameraId = backIndex + "";
        }
    }

    @Override
    public List<CameraSize> getSupperPreviewSizes() {

        if (mCamera == null) {
            return null;
        }

        List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        if (previewSizes != null && !previewSizes.isEmpty()) {
            List<CameraSize> cameraSizes = new ArrayList<>();
            for (Camera.Size previewSize : previewSizes) {
                cameraSizes.add(new CameraSize(previewSize.width, previewSize.height));
            }

            return cameraSizes;
        }
        return null;
    }

    @Override
    public int getOrientation() {
        if ("-1".equals(mCurrentCameraId)) {
            return 0;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        getCameraInfo(Integer.valueOf(mCurrentCameraId), info);
        return info.orientation;
    }

    @Override
    public void pause() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void resume() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    public int getZoom() {
        if (mCamera != null) {
            return mCamera.getParameters().getZoom();
        }
        return -1;
    }

    @Override
    public void setZoom(int zoomLevel) {
        mCamera.getParameters().setZoom(zoomLevel);
    }

    @Override
    public int getFlashMode() {
        if (mCamera != null) {
            return CameraFlashMode.getMode(mCamera.getParameters().getFlashMode());
        }
        return CameraFlashMode.MODE_UNKNOWN;
    }

    @Override
    public void openFlashMode() {
        switchCameraFlashMode(Camera.Parameters.FLASH_MODE_ON);
    }

    @Override
    public void closeFlashMode() {
        switchCameraFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    }

    @Override
    public void autoFlashMode() {
        switchCameraFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
    }

    @Override
    public void torchFlashMode() {
        switchCameraFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
    }

    /**
     * switch camera1 flash mode
     *
     * @param mode
     */
    public void switchCameraFlashMode(String mode) {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.stopPreview();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(mode);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logFeatures() {
        if (mCamera == null) {
            SmartCamLog.i(TAG, "Camera is not open");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        SmartCamLog.i(TAG, "Antibanding:" + parameters.getAntibanding());
        SmartCamLog.i(TAG, "ColorEffect:" + parameters.getColorEffect());
        SmartCamLog.i(TAG, "FlashMode:" + parameters.getFlashMode());
        SmartCamLog.i(TAG, "FocusMode:" + parameters.getFocusMode());
    }
}
