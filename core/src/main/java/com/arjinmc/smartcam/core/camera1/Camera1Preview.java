package com.arjinmc.smartcam.core.camera1;

import android.content.Context;
import android.hardware.Camera;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

import java.io.File;
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
    private AbsCameraWrapper.OnClickCaptureLisenter mOnClickCaptureLisenter;

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

        mOnClickCaptureLisenter = new AbsCameraWrapper.OnClickCaptureLisenter() {
            @Override
            public void onCapture(File file) {
                try {
                    mCamera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {
                            SmartCamLog.i(TAG, "onShutter");

                        }
                    }, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            SmartCamLog.i(TAG, "raw");

                        }
                    }, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            SmartCamLog.i(TAG, "jpeg");
                            mCamera.startPreview();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCapturePath(String filePath) {

            }

            @Override
            public void onCaptureUri(String fileUri) {

            }
        };
        mCameraWrapper.setOnClickCaptureLisenter(mOnClickCaptureLisenter);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        SmartCamLog.i(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        SmartCamLog.i(TAG, "surfaceChanged:" + width + "/" + height);
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        SmartCamLog.i(TAG, "surfaceDestroyed");

        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            if (mCamera != null) {
                mCameraWrapper.closeFlashMode();
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
                SmartCamLog.i(TAG, "startPreview orientation:" + mOrientation);
                mCamera.setDisplayOrientation(SmartCamUtils.getShouldRotateDegree(getContext()
                        , mCameraWrapper.getCurrentCameraType()
                        , mCameraWrapper.getCurrentCameraId()
                        , mOrientation));

                CameraSupportPreviewSize cameraSupportPreviewSize = mCameraWrapper.getCompatPreviewSize(
                        getMeasuredWidth(), getMeasuredHeight());

                if (cameraSupportPreviewSize != null) {
                    SmartCamLog.e(TAG, "final preview size:"
                            + cameraSupportPreviewSize.getWidth() + "/" + cameraSupportPreviewSize.getHeight());
                    getHolder().setFixedSize(cameraSupportPreviewSize.getWidth(), cameraSupportPreviewSize.getHeight());

                }
                mCamera.setPreviewDisplay(getHolder());
                mCamera.startPreview();
            }
        } catch (IOException e) {
            SmartCamLog.d(TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void destroy() {
        surfaceDestroyed(mHolder);
    }

}
