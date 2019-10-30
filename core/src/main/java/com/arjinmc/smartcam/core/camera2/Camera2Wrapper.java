package com.arjinmc.smartcam.core.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.lock.CameraLock;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.model.CameraType;
import com.arjinmc.smartcam.core.model.SmartCamOpenError;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for camera v2
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Wrapper extends AbsCameraWrapper {

    private CameraDevice mCamera;
    private Handler mHandler;
    private CameraDevice.StateCallback mStateCallBack;
    private CameraLock mCameraLock;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private ImageReader mImageReader;

    public Camera2Wrapper(Context context) {
        setContext(context);
        mHandler = new Handler();
        mCameraLock = new CameraLock();
    }

    @Override
    public CameraDevice getCamera() {
        return mCamera;
    }

    @Override
    public Camera2Wrapper getCameraWrapper() {
        return this;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void open() {
        final CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {

            if (mStateCallBack == null) {
                mStateCallBack = new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        mCameraLock.release();
                        mCamera = camera;
                        mCurrentCameraId = camera.getId();

                        if (mSmartCamStateListener != null) {
                            mSmartCamStateListener.onConnected();
                        }
                        Log.e("onOpened", "onOpened");

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        mCameraLock.release();
                        mCamera = null;
                        if (mSmartCamStateListener != null) {
                            mSmartCamStateListener.onDisconnected();
                        }
                        Log.e("onDisconnected", "onDisconnected");

                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        mCameraLock.release();
                        mCamera = null;
                        if (mSmartCamStateListener != null) {
                            mSmartCamStateListener.onError(new SmartCamOpenError(error + "CameraDevice.StateCallback error"));
                        }
                        Log.e("onError", "onError");
                    }
                };
            }

            String[] cameraIdList = getCameraIdList();
            if (cameraIdList == null || cameraIdList.length == 0) {
                if (mSmartCamStateListener != null) {
                    mSmartCamStateListener.onError(new SmartCamOpenError("No camera"));
                }
                return;
            }
            manager.openCamera(cameraIdList[0], mStateCallBack, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean resumeOpen() {

        Log.e("resumeOpen", "resumeOpen");

        if (mCurrentCameraId == null && "-1".equals(mCurrentCameraId)) {
            return false;
        }

        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.openCamera(mCurrentCameraId, mStateCallBack, mHandler);
            return true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * get camera id list
     *
     * @return
     */
    private String[] getCameraIdList() {
        if (getContext() == null) {
            return null;
        }
        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            return manager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCameraCount() {
        if (getContext() == null) {
            return -1;
        }
        String[] cameraIdList = getCameraIdList();
        if (cameraIdList != null && cameraIdList.length != 0) {
            return cameraIdList.length;
        }
        return -1;
    }

    @Override
    public List<CameraSupportPreviewSize> getSupperPreviewSizes() {
        if (mCamera == null) {
            return null;
        }

        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(mCamera.getId());
            StreamConfigurationMap previewSizeMap = cameraCharacteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] preiviewSizeList = previewSizeMap.getOutputSizes(SurfaceTexture.class);
            if (preiviewSizeList == null || preiviewSizeList.length == 0) {
                return null;
            }
            List<CameraSupportPreviewSize> cameraSupportPreviewSizeList = new ArrayList<>();
            for (Size childSize : preiviewSizeList) {
                cameraSupportPreviewSizeList.add(new CameraSupportPreviewSize(
                        childSize.getWidth(), childSize.getHeight()));
            }
            return cameraSupportPreviewSizeList;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getCurrentCameraId() {
        if (mCamera == null) {
            return "-1";
        }
        return mCamera.getId();
    }

    @Override
    public int getCurrentCameraType() {

        if (mCamera == null) {
            return CameraType.CAMERA_NULL;
        }
        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(mCamera.getId());
            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                return CameraType.CAMERA_BACK;
            }
            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                return CameraType.CAMERA_FRONT;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return CameraType.CAMERA_NULL;
        }
        return CameraType.CAMERA_NULL;
    }

    @Override
    public boolean isBackCamera() {
        return getCurrentCameraType() == CameraType.CAMERA_BACK;
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

    @SuppressLint("MissingPermission")
    private void switchCamera(int cameraType) {
        Log.e("switch camera", "switch camera");
        String[] cameraIdList = getCameraIdList();
        if (cameraIdList == null || cameraIdList.length < 2) {
            return;
        }

        if (mCaptureSession == null) {
            return;
        }

        mCaptureSession.close();
        mCaptureSession = null;
        mCamera.close();
        mCamera = null;

        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        int size = cameraIdList.length;
        String frontCameraId = null, backCameraId = null;
        for (int i = 0; i < size; i++) {
            try {
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraIdList[i]);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    backCameraId = cameraIdList[i];
                }
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = cameraIdList[i];
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        if (cameraType == CameraType.CAMERA_BACK) {
            mCurrentCameraId = backCameraId;
            mCurrentCameraType = CameraType.CAMERA_BACK;
            return;
        }

        if (cameraType == CameraType.CAMERA_FRONT) {
            mCurrentCameraId = frontCameraId;
            mCurrentCameraType = CameraType.CAMERA_FRONT;
            return;
        }

    }

    @Override
    public boolean isLock() {
        return mCameraLock.isLock();
    }

    @Override
    public int getZoom() {
        return super.getZoom();
    }

    @Override
    public void setZoom(int zoomLevel) {
        super.setZoom(zoomLevel);
    }

    public void setPreviewRequestBuilder(CaptureRequest.Builder previewRequestBuilder) {
        mPreviewRequestBuilder = previewRequestBuilder;
    }

    public CaptureRequest.Builder getPreviewRequestBuilder() {
        return mPreviewRequestBuilder;
    }

    public void setCaptureSession(CameraCaptureSession captureSession) {
        mCaptureSession = captureSession;
    }

    public CameraCaptureSession getCaptureSession() {
        return mCaptureSession;
    }

    public void setPreviewRequest(CaptureRequest captureRequest) {
        mPreviewRequest = captureRequest;
    }

    public CaptureRequest getPreviewRequest() {
        return mPreviewRequest;
    }

    public void setImageReader(ImageReader imageReader) {
        mImageReader = imageReader;
    }

    public ImageReader getImageReader() {
        return mImageReader;
    }
}
