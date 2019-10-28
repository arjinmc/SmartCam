package com.arjinmc.smartcam.core.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.model.SmartCamOpenError;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;

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

    public Camera2Wrapper(Context context) {
        setContext(context);
        mHandler = new Handler();
    }

    @Override
    public CameraDevice getCamera() {
        return mCamera;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void open() {
        final CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
//            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
//                throw new RuntimeException("Time out waiting to lock camera opening.");
//            }

            if (mStateCallBack == null) {
                mStateCallBack = new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        mCamera = camera;
                        if (mSmartCamStateListener != null) {
                            mSmartCamStateListener.onConnected();
                        }
                        Log.e("onOpened", "onOpened");

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        mCamera = null;
                        if (mSmartCamStateListener != null) {
                            mSmartCamStateListener.onDisconnected();
                        }
                        Log.e("onDisconnected", "onDisconnected");

                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
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
            mCurrentCameraId = Integer.parseInt(cameraIdList[0]);
            manager.openCamera(String.valueOf(mCurrentCameraId), mStateCallBack, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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
}
