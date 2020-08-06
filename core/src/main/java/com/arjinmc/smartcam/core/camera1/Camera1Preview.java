package com.arjinmc.smartcam.core.camera1;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.arjinmc.smartcam.core.SmartCamConfig;
import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamOrientationEventListener;
import com.arjinmc.smartcam.core.file.ImageFileSaver;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.SmartCamOutputOption1;
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
    private SmartCamOrientationEventListener mOrientationEventListener;

    private int mCameraDegree;
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;

    private Handler mHandler = new Handler();

    private CameraSize mPreviewSize;

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

        mOrientationEventListener = new SmartCamOrientationEventListener(getContext(), this);
        mOrientationEventListener.enable();

        mOnClickCaptureLisenter = new AbsCameraWrapper.OnClickCaptureLisenter() {
            @Override
            public void onCapture(final File file) {
                try {
                    mCamera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    }, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            mHandler.post(new ImageFileSaver(new SmartCamOutputOption1(data, file, mCameraDegree
                                    , mCameraWrapper.getCurrentCameraType(), getMeasuredHeight(), getMeasuredWidth())
                                    , mCameraWrapper.getCaptureCallback()));
                            if (SmartCamConfig.isAutoReset()) {
                                doPreview();
                            }
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
        doPreview();
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

    private void doPreview() {

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

                mPreviewSize = mCameraWrapper.getCompatPreviewSize(
                        getMeasuredHeight(), getMeasuredWidth());

                if (mPreviewSize != null) {
                    SmartCamLog.e(TAG, "final preview size:"
                            + mPreviewSize.getWidth() + "/" + mPreviewSize.getHeight());
                    getHolder().setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                }
                mCamera.setPreviewDisplay(getHolder());
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                parameters.setPreviewSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                CameraSize outputSize = mCameraWrapper.getMaxOutputSize();
                parameters.setPictureSize(outputSize.getWidth(), outputSize.getHeight());
                parameters.setJpegQuality(100);
                parameters.setPictureFormat(ImageFormat.JPEG);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onOrientationChange(int degree) {
        mCameraDegree = degree;
    }

    @Override
    public void startPreview() {
        if (mCamera == null) {
            return;
        }
        doPreview();
    }

    @Override
    public void stopPreview() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopPreview();
    }

    @Override
    public void destroy() {
        mOrientationEventListener.disable();
        surfaceDestroyed(mHolder);
    }

}
