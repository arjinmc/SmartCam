package com.arjinmc.smartcam.core.camera1;

import android.hardware.Camera;
import android.util.Log;

import com.arjinmc.smartcam.core.AbsCameraWrapper;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.model.CameraFlashMode;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.model.CameraType;

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

    @Override
    public Camera1Wrapper getCameraWrapper() {
        return this;
    }

    @Override
    public Camera getCamera() {
        return mCamera;
    }

    @Override
    public boolean open() {

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
                    mCurrentCameraId = i;
                }

                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frontCameraId = i;
                }
            }

            // attempt to get a Camera instance
            mCamera = Camera.open();
            if (mCamera != null) {
                mCurrentCameraType = CameraType.CAMERA_BACK;
                return true;
            } else {
                mCamera = Camera.open(frontCameraId);
                if (mCamera == null) {
                    return false;
                }
                mCurrentCameraType = CameraType.CAMERA_FRONT;
                mCurrentCameraId = frontCameraId;
                return true;
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean resumeOpen() {
        try {
            if (mCurrentCameraType != CameraType.CAMERA_NULL) {
                mCamera = Camera.open(mCurrentCameraId);
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
        switchCamera(CameraType.CAMERA_BACK);
    }

    @Override
    public void switchToFrontCamera() {
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
        mCamera.stopPreview();
        mCamera.release();
        if (type == CameraType.CAMERA_FRONT && frontIndex != -1) {
            mCurrentCameraId = frontIndex;
            mCamera = Camera.open(frontIndex);
        } else if (type == CameraType.CAMERA_BACK && backIndex != -1) {
            mCurrentCameraId = backIndex;
            mCamera = Camera.open(backIndex);
        }
    }

    @Override
    public List<CameraSupportPreviewSize> getSupperPreviewSizes() {

        if (mCamera == null) {
            return null;
        }

        List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        if (previewSizes != null && !previewSizes.isEmpty()) {
            List<CameraSupportPreviewSize> cameraSupportPreviewSizes = new ArrayList<>();
            for (Camera.Size previewSize : previewSizes) {
                cameraSupportPreviewSizes.add(new CameraSupportPreviewSize(previewSize.width, previewSize.height));
            }

            return cameraSupportPreviewSizes;
        }
        return null;
    }

    @Override
    public int getOrientation() {
        if (mCurrentCameraId == -1) {
            return 0;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        getCameraInfo(mCurrentCameraId, info);
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
    public void capture() {

        try {
            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    Log.i("onShutter", "onShutter");

                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.i("onPictureTaken", "raw");

                }
            }, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.i("onPictureTaken2", "jpeg");
                    mCamera.startPreview();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
        SmartCamUtils.switchCamera1FlashMode(mCamera, Camera.Parameters.FLASH_MODE_ON);
    }

    @Override
    public void closeFlashMode() {
        SmartCamUtils.switchCamera1FlashMode(mCamera, Camera.Parameters.FLASH_MODE_OFF);
    }

    @Override
    public void autoFlashMode() {
        SmartCamUtils.switchCamera1FlashMode(mCamera, Camera.Parameters.FLASH_MODE_AUTO);
    }

    @Override
    public void torchFlashMode() {
        SmartCamUtils.switchCamera1FlashMode(mCamera, Camera.Parameters.FLASH_MODE_TORCH);
    }

    @Override
    public void logFeatures() {
        if (mCamera == null) {
            Log.i(TAG, "Camera is not open");
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Log.i(TAG, "Antibanding:" + parameters.getAntibanding());
        Log.i(TAG, "ColorEffect:" + parameters.getColorEffect());
        Log.i(TAG, "FlashMode:" + parameters.getFlashMode());
        Log.i(TAG, "FocusMode:" + parameters.getFocusMode());
    }
}
