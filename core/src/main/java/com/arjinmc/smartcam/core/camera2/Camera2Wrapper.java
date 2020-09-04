package com.arjinmc.smartcam.core.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.collection.ArrayMap;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.comparator.CompareSizesByArea;
import com.arjinmc.smartcam.core.lock.CameraLock;
import com.arjinmc.smartcam.core.model.CameraFlashMode;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraType;
import com.arjinmc.smartcam.core.model.SmartCamOpenError;
import com.arjinmc.smartcam.core.model.SmartCamPreviewError;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;

import java.util.ArrayList;
import java.util.Collections;
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

    public Camera2Wrapper(Context context) {
        setContext(context);
        mHandler = new Handler(Looper.getMainLooper());
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

                        if (mSmartCamStateCallback != null) {
                            mSmartCamStateCallback.onConnected();
                        }
                        SmartCamLog.e(TAG, "onOpened");

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        mCameraLock.release();
                        mCamera = null;
                        if (mSmartCamStateCallback != null) {
                            mSmartCamStateCallback.onDisconnected();
                        }
                        SmartCamLog.e(TAG, "onDisconnected");

                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        mCameraLock.release();
                        mCamera = null;
                        if (mSmartCamStateCallback != null) {
                            if (error == 4) {
                                mSmartCamStateCallback.onError(new SmartCamPreviewError());
                            } else {
                                mSmartCamStateCallback.onError(new SmartCamOpenError(error + "CameraDevice.StateCallback error"));
                            }
                        }
                        SmartCamLog.e(TAG, "onError:" + error);
                    }
                };
            }

            String[] cameraIdList = getCameraIdList();
            if (cameraIdList == null || cameraIdList.length == 0) {
                if (mSmartCamStateCallback != null) {
                    mSmartCamStateCallback.onError(new SmartCamOpenError("No camera"));
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
    public List<CameraSize> getSupperPreviewSizes() {
        if (mCamera == null) {
            return null;
        }

        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(mCamera.getId());
            StreamConfigurationMap previewSizeMap = cameraCharacteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] previewSizeList = previewSizeMap.getOutputSizes(SurfaceTexture.class);
            if (previewSizeList == null || previewSizeList.length == 0) {
                return null;
            }
            List<CameraSize> cameraSizeList = new ArrayList<>();
            for (Size childSize : previewSizeList) {
                cameraSizeList.add(new CameraSize(
                        childSize.getWidth(), childSize.getHeight()));
//                SmartCamLog.e("preview size", childSize.getWidth() + "," + childSize.getHeight());
            }
            return cameraSizeList;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CameraSize getCompatPreviewSize(int width, int height) {
        return chooseOptimalSize(getSupperPreviewSizes(), width, height, getMaxOutputSize());
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

        if (mCameraParams.containsKey(CaptureRequest.CONTROL_AE_MODE)) {
            int mode = mCameraParams.get(CaptureRequest.CONTROL_AE_MODE);
            switch (mode) {
                case CameraMetadata.CONTROL_AE_MODE_ON:
                    return CameraFlashMode.MODE_ON;
                case CameraMetadata.CONTROL_AE_MODE_OFF:
                    return CameraFlashMode.MODE_OFF;
                case CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH:
                    return CameraFlashMode.MODE_AUTO;
                case CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                    return CameraFlashMode.MODE_TORCH;
                default:
                    break;
            }
        }

        if (mCameraParams.containsKey(CaptureRequest.FLASH_MODE)) {
            int mode = mCameraParams.get(CaptureRequest.FLASH_MODE);
            if (mode == CameraMetadata.FLASH_MODE_TORCH) {
                return CameraFlashMode.MODE_TORCH;
            }
        }
        return CameraFlashMode.MODE_OFF;
    }

    @Override
    public void openFlashMode() {

        dispatchFlashChange(CameraFlashMode.MODE_ON);
        addCameraParam(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_EXTERNAL_FLASH);
        removeCameraParam(CaptureRequest.FLASH_MODE);

    }

    @Override
    public void closeFlashMode() {

        dispatchFlashChange(CameraFlashMode.MODE_OFF);
        addCameraParam(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
        removeCameraParam(CaptureRequest.FLASH_MODE);

    }

    @Override
    public void autoFlashMode() {

        dispatchFlashChange(CameraFlashMode.MODE_AUTO);
        addCameraParam(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH);
        removeCameraParam(CaptureRequest.FLASH_MODE);

    }

    @Override
    public void torchFlashMode() {

        dispatchFlashChange(CameraFlashMode.MODE_TORCH);
        removeCameraParam(CaptureRequest.CONTROL_AE_MODE);
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

    @Override
    public List<CameraSize> getOutputSizes() {
        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = manager.getCameraCharacteristics(mCamera.getId());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (characteristics == null) {
            return null;
        }
        StreamConfigurationMap map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        return SmartCamUtils.convertSizes(map.getOutputSizes(ImageFormat.JPEG));
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

    @Override
    public void release() {
        if (mCamera != null) {
            mCamera.close();
        }

        if (mStateCallBack != null) {
            mStateCallBack = null;
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private CameraSize chooseOptimalSize(List<CameraSize> choices, int textureViewWidth,
                                         int textureViewHeight, CameraSize aspectRatio) {
//        SmartCamLog.e("textureViewWidth", textureViewWidth + "," + textureViewHeight);
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<CameraSize> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<CameraSize> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (CameraSize option : choices) {
//            SmartCamLog.e("supportSizes", option.getWidth() + "," + option.getHeight());
            if (option.getWidth() <= textureViewWidth && option.getHeight() <= textureViewHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            SmartCamLog.e(TAG, "Couldn't find any suitable preview size");
            return choices.get(0);
        }
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
