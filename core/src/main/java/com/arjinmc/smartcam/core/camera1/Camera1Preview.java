package com.arjinmc.smartcam.core.camera1;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.arjinmc.smartcam.core.SmartCamConfig;
import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamPreview;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamOrientationEventListener;
import com.arjinmc.smartcam.core.file.ImageSaver;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraVersion;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;
import com.arjinmc.smartcam.core.model.SmartCamCaptureResult;
import com.arjinmc.smartcam.core.model.SmartCamError;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private AbsCameraWrapper.OnClickCaptureListener mOnClickCaptureListener;
    private SmartCamOrientationEventListener mOrientationEventListener;
    private SmartCamPreview.OnManualFocusListener mOnManualFocusListener;
    private SmartCamPreview.OnCaptureAnimationLister mOnCaptureAnimationListener;
    private SmartCamPreview.OnGestureToZoomListener mOnGestureToZoomListener;

    private int mCameraDegree;
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private CameraSize mPreviewSize;
    private float mLastGesturePointDistance;
    private boolean mIsTwoPointsMoving;

    public Camera1Preview(Context context, Camera1Wrapper camera1Wrapper) {
        super(context);
        mCameraWrapper = camera1Wrapper;
        if (SmartCamConfig.getInstance().isUseGestureToZoom()) {
            setClickable(true);
        }
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

        mOnClickCaptureListener = new AbsCameraWrapper.OnClickCaptureListener() {
            @Override
            public void onCapture() {

                if (mCamera == null) {
                    return;
                }
                try {
                    if (mOnCaptureAnimationListener != null) {
                        mOnCaptureAnimationListener.onPlay();
                    }
                    mCamera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    }, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(final byte[] data, Camera camera) {
                            if (mCameraWrapper.getCaptureCallback() != null) {
                                new ImageSaver(new SmartCamCaptureResult(data, CameraVersion.VERSION_1
                                        //no need to return orientation because it has auto fix the right orientation
                                        , 0
                                        , SmartCamUtils.isShouldReverse(mCameraWrapper.getCurrentCameraType())
                                        , mPreviewSize.getHeight()
                                        , mPreviewSize.getWidth()
                                        , mCameraWrapper.getPreviewRatio()), mCameraWrapper.getCaptureCallback()).start();
                            }
                            if (SmartCamConfig.getInstance().isAutoReset()) {
                                doPreview();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mOnCaptureAnimationListener != null) {
                        mOnCaptureAnimationListener.onStop();
                    }
                    dispatchError(new SmartCamCaptureError());
                }
            }
        };
        mCameraWrapper.setOnClickCaptureListener(mOnClickCaptureListener);
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
//            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP && event.getPointerCount() == 1
                && mOnManualFocusListener != null
                && SmartCamConfig.getInstance().isUseManualFocus() && !mIsTwoPointsMoving) {
            mOnManualFocusListener.requestFocus(event.getX(), event.getY());
            Camera.Area cameraArea = new Camera.Area(mOnManualFocusListener.getFocusRegion(), 1000);
            List<Camera.Area> meteringAreas = new ArrayList<>();
            List<Camera.Area> focusAreas = new ArrayList<>();
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                meteringAreas.add(cameraArea);
                focusAreas.add(cameraArea);
                try {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    parameters.setFocusAreas(focusAreas);
                    parameters.setMeteringAreas(meteringAreas);
                    mCamera.cancelAutoFocus();
                    mCamera.setParameters(parameters);
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean b, Camera camera) {

                        }
                    });
                } catch (Exception e) {
//                        e.printStackTrace();
                    mOnManualFocusListener.cancelFocus();
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2
                && SmartCamConfig.getInstance().isUseGestureToZoom()) {
            mIsTwoPointsMoving = true;
            float gesturePointDistance = SmartCamUtils.getPointsDistance(event.getX(0), event.getY(0)
                    , event.getX(1), event.getY(1));
            if (mLastGesturePointDistance != 0) {
                float changeDistance = mLastGesturePointDistance - gesturePointDistance;
                if (Math.abs(changeDistance) > SmartCamConfig.getInstance().getGestureMovingFactor()) {
                    if (changeDistance > 0) {
                        // zoom smaller
                        dispatchGestureToZoomEvent(true, changeDistance);
                    } else if (changeDistance < 0) {
                        //zoom bigger
                        dispatchGestureToZoomEvent(false, Math.abs(changeDistance));
                    }
                }
            }
            mLastGesturePointDistance = gesturePointDistance;
            if (mOnManualFocusListener != null) {
                mOnManualFocusListener.cancelFocus();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mIsTwoPointsMoving = false;
        }
        return super.onTouchEvent(event);

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

                if (mCameraWrapper.getPreviewRatio() == null) {
                    mPreviewSize = mCameraWrapper.getCompatPreviewSize(
                            getMeasuredHeight(), getMeasuredWidth());
                } else {
                    mPreviewSize = mCameraWrapper.getCompatPreviewSizeByRatio(
                            mCameraWrapper.getPreviewRatio()
                            , getMeasuredHeight(), getMeasuredWidth());
                }

                if (mPreviewSize != null) {
                    SmartCamLog.e(TAG, "final preview size:"
                            + mPreviewSize.getWidth() + "/" + mPreviewSize.getHeight());
                    getHolder().setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                }
                mCamera.setPreviewDisplay(getHolder());
                Camera.Parameters parameters = mCamera.getParameters();
                if (SmartCamUtils.hasAutoFocus(getContext())) {
                    parameters = setAutoFocusMode(parameters, true);
                }
                parameters.setPreviewSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                CameraSize outputSize = mCameraWrapper.getMaxOutputSize();
                parameters.setPictureSize(outputSize.getWidth(), outputSize.getHeight());
                parameters.setJpegQuality(SmartCamConfig.getInstance().getCaptureQuality());
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.setRotation(SmartCamUtils.getShouldRotateOrientationForCamera1(mCameraDegree, mCameraWrapper.getCurrentCameraType()));
                parameters = mCameraWrapper.resumeFlashMode(parameters);
                parameters = mCameraWrapper.resumeZoom(parameters);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onOrientationChange(int degree) {

        if (Math.abs(degree - mCameraDegree) > SmartCamConfig.getInstance().getDismissManualFocusDegreeOffset()) {
            if (mOnManualFocusListener != null) {
                mOnManualFocusListener.cancelFocus();
            }
        }

        int oldCameraDegree = mCameraDegree;
        mCameraDegree = degree;
        if (mCamera == null) {
            return;
        }

        //if should rotate degree is the same,no need to change preview orientation
        if (SmartCamUtils.getShouldRotateOrientationForCamera1(oldCameraDegree, mCameraWrapper.getCurrentCameraType())
                == SmartCamUtils.getShouldRotateOrientationForCamera1(mCameraDegree, mCameraWrapper.getCurrentCameraType())) {
            return;
        }

        //update capture orientation
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRotation(SmartCamUtils.getShouldRotateOrientationForCamera1(
                    mCameraDegree, mCameraWrapper.getCurrentCameraType()));
            mCamera.setParameters(parameters);

        } catch (Exception e) {
//            e.printStackTrace();
        }

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
        try {
            surfaceDestroyed(mHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mOnGestureToZoomListener != null) {
            mOnGestureToZoomListener = null;
        }
    }

    @Override
    public void resumeAutoFocus() {
        if (mCamera == null) {
            return;
        }
        if (!SmartCamUtils.hasAutoFocus(getContext())) {
            return;
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters = setAutoFocusMode(parameters, true);
            mCamera.setParameters(parameters);
        } catch (Exception e) {

        }

    }

    /**
     * set focus mode
     *
     * @param parameter
     * @param autoFocus
     * @return
     */
    private Camera.Parameters setAutoFocusMode(Camera.Parameters parameter, boolean autoFocus) {
        final List<String> modes = parameter.getSupportedFocusModes();
        if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameter.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
            parameter.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
            parameter.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        } else {
            parameter.setFocusMode(modes.get(0));
        }
        return parameter;
    }

    @Override
    public void setOnManualFocusListener(SmartCamPreview.OnManualFocusListener onManualFocusListener) {
        mOnManualFocusListener = onManualFocusListener;
    }

    @Override
    public void setOnCaptureAnimationListener(SmartCamPreview.OnCaptureAnimationLister onCaptureAnimationListener) {
        mOnCaptureAnimationListener = onCaptureAnimationListener;
    }

    @Override
    public void setOnGestureToZoomListener(SmartCamPreview.OnGestureToZoomListener onGestureToZoomListener) {
        mOnGestureToZoomListener = onGestureToZoomListener;
    }

    private void dispatchError(SmartCamError smartCamError) {
        if (mCameraWrapper == null || mCameraWrapper.getCaptureCallback() == null) {
            return;
        }
        mCameraWrapper.getCaptureCallback().onError(smartCamError);
    }

    /**
     * dispatch to ui
     *
     * @param isSmaller
     * @param changeDistance
     */
    private void dispatchGestureToZoomEvent(boolean isSmaller, float changeDistance) {
        if (mOnGestureToZoomListener != null) {
            if (isSmaller) {
                mOnGestureToZoomListener.onZoomToSmaller(changeDistance);
            } else {
                mOnGestureToZoomListener.onZoomToBigger(changeDistance);
            }
        }
    }
}
