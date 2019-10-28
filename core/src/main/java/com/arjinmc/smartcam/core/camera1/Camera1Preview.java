package com.arjinmc.smartcam.core.camera1;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;

import java.io.IOException;

/**
 * Preview for camera v1
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class Camera1Preview extends SurfaceView implements SurfaceHolder.Callback, ICameraPreviewWrapper {

    private final String TAG = "Camera1Preview";

    private SurfaceHolder mHolder;
    private Camera1Wrapper mCameraWrapper;
    private Camera mCamera;

    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;

    public Camera1Preview(Context context, Camera1Wrapper camera1Wrapper) {
        super(context);
        mCameraWrapper = camera1Wrapper;
        init();
    }

    private void init() {

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged:" + width + "/" + height);
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");

        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            if (mCamera != null) {
                SmartCamUtils.switchCamera1FlashMode(mCamera, Camera.Parameters.FLASH_MODE_OFF);
                mCamera.stopPreview();
                getHolder().removeCallback(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {

        try {
            mCameraWrapper.resumeOpen();
            mCamera = mCameraWrapper.getCamera();
            if (mCamera != null) {
                mOrientation = SmartCamUtils.getWindowDisplayRotation(getContext());
                Log.i("startPreview", mOrientation + "");
                mCamera.setDisplayOrientation(SmartCamUtils.getShouldRotateDegree(getContext()
                        , mCameraWrapper.getCurrentCameraType()
                        , mCameraWrapper.getCurrentCameraId()
                        , mOrientation));

                CameraSupportPreviewSize cameraSupportPreviewSize = mCameraWrapper.getCompatPreviewSize(
                        getMeasuredWidth(), getMeasuredHeight());

                if (cameraSupportPreviewSize != null) {
                    Log.e("final preview size", cameraSupportPreviewSize.getWidth() + "/" + cameraSupportPreviewSize.getHeight());
                    getHolder().setFixedSize(cameraSupportPreviewSize.getWidth(), cameraSupportPreviewSize.getHeight());

                }
                mCamera.setPreviewDisplay(getHolder());
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void destroy() {
        surfaceDestroyed(mHolder);
    }

}
