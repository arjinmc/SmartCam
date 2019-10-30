package com.arjinmc.smartcam.core.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.collection.ArrayMap;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.lock.CameraLock;
import com.arjinmc.smartcam.core.model.CameraFlashMode;
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

    private final String TAG = "Camera2Wrapper";

    private CameraDevice mCamera;
    private Handler mHandler;
    private CameraDevice.StateCallback mStateCallBack;
    private CameraLock mCameraLock;
    private OnFlashChangeListener mOnFlashChangeListener;
    /**
     * current params of camera
     */
    private ArrayMap<CaptureRequest.Key, Integer> mCameraParams;

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }
    };

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
                        SmartCamLog.e(TAG, "onOpened");

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        mCameraLock.release();
                        mCamera = null;
                        if (mSmartCamStateListener != null) {
                            mSmartCamStateListener.onDisconnected();
                        }
                        SmartCamLog.e(TAG, "onDisconnected");

                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        mCameraLock.release();
                        mCamera = null;
                        if (mSmartCamStateListener != null) {
                            mSmartCamStateListener.onError(new SmartCamOpenError(error + "CameraDevice.StateCallback error"));
                        }
                        SmartCamLog.e(TAG, "onError");
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

        SmartCamLog.e(TAG, "resumeOpen");

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
        SmartCamLog.e(TAG, "switch camera");
        String[] cameraIdList = getCameraIdList();
        if (cameraIdList == null || cameraIdList.length < 2) {
            return;
        }

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
    public int getFlashMode() {

        if (mCamera == null || mCameraParams == null || mCameraParams.isEmpty()) {
            return CameraFlashMode.MODE_OFF;
        }

        int mode = mCameraParams.get(CaptureRequest.FLASH_MODE);
        switch (mode) {
            case CaptureRequest.FLASH_MODE_OFF:
                return CameraFlashMode.MODE_OFF;
            case CaptureRequest.FLASH_MODE_SINGLE:
                return CameraFlashMode.MODE_AUTO;
            case CaptureRequest.FLASH_MODE_TORCH:
                return CameraFlashMode.MODE_TORCH;
            default:
                break;

        }
        return CameraFlashMode.MODE_OFF;
    }

    @Override
    public void openFlashMode() {

        dispatchFlashChange(CameraFlashMode.MODE_AUTO);
        addCameraParam(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_SINGLE);
    }

    @Override
    public void closeFlashMode() {

        dispatchFlashChange(CameraFlashMode.MODE_OFF);
        addCameraParam(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
    }

    @Override
    public void autoFlashMode() {

        dispatchFlashChange(CameraFlashMode.MODE_AUTO);
        addCameraParam(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_SINGLE);
    }

    @Override
    public void torchFlashMode() {
        dispatchFlashChange(CameraFlashMode.MODE_TORCH);
        addCameraParam(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
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

    public CameraCaptureSession.CaptureCallback getCaptureCallback() {
        return mCaptureCallback;
    }

    public Handler getHandler() {
        return mHandler;
    }

    private void addCameraParam(CaptureRequest.Key key, int value) {
        if (mCameraParams == null) {
            mCameraParams = new ArrayMap<>();
        }
        mCameraParams.put(key, value);
    }

    private void removeCameraParam(CaptureRequest.Key key) {
        if (mCameraParams == null || mCameraParams.isEmpty()) {
            return;
        }
        if (mCameraParams.containsKey(key)) {
            mCameraParams.remove(key);
        }
        if (mCameraParams.isEmpty()) {
            mCameraParams = null;
        }
    }

    public CaptureRequest.Builder resumeParams(CaptureRequest.Builder builder) {
        if (mCameraParams == null || mCameraParams.isEmpty()) {
            return builder;
        }
        int size = mCameraParams.size();
        for (int i = 0; i < size; i++) {
            CaptureRequest.Key key = mCameraParams.keyAt(i);
            builder.set(key, mCameraParams.get(key));
        }
        return builder;
    }


    /**
     * set listener for flash change
     *
     * @param onFlashChangeListener
     */
    public void setOnFlashChangeListener(OnFlashChangeListener onFlashChangeListener) {
        mOnFlashChangeListener = onFlashChangeListener;
    }

    /**
     * dispatch flash change to preview
     *
     * @param flashType {@link CameraFlashMode}
     */
    private void dispatchFlashChange(int flashType) {
        if (mOnFlashChangeListener != null && getFlashMode() != flashType) {
            mOnFlashChangeListener.onFlashChange();
        }
    }

    /**
     * listener for flash change
     */
    public interface OnFlashChangeListener {
        void onFlashChange();
    }
}
