package com.arjinmc.smartcam.core.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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
import com.arjinmc.smartcam.core.model.SmartCamOpenError;
import com.arjinmc.smartcam.core.model.SmartCamPreviewError;
import com.arjinmc.smartcam.core.model.SmartCamUnknownError;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Preview for camera2
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Preview extends TextureView implements TextureView.SurfaceTextureListener, ICameraPreviewWrapper {

    private final String TAG = "Camera2Preview";

    private Camera2Wrapper mCamera2Wrapper;
    private CameraDevice mCamera;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private ImageReader mImageReader;
    private Executor mExecutor;
    private Camera2Wrapper.OnFlashChangeListener mOnFlashChangeListener;
    private Camera2Wrapper.OnZoomChangeListener mOnZoomChangeListener;
    private AbsCameraWrapper.OnClickCaptureListener mOnClickCaptureListener;
    private SmartCamOrientationEventListener mOrientationEventListener;
    private SmartCamPreview.OnManualFocusListener mOnManualFocusListener;
    private SmartCamPreview.OnCaptureAnimationLister mOnCaptureAnimationListener;
    private SmartCamPreview.OnGestureToZoomListener mOnGestureToZoomListener;
    private int mWidth, mHeight;
    private CameraSize mPreviewSize;
    /**
     * the degree when capture
     */
    private int mDegree;
    private Matrix mMatrix;
    private float mLastGesturePointDistance;

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            if (mCaptureSession != null) {
                mCaptureSession.close();
            }
            if (mCamera2Wrapper != null || mCamera2Wrapper.getCaptureCallback() != null) {
                if (CaptureFailure.REASON_ERROR == failure.getReason()) {
                    dispatchError(new SmartCamCaptureError());
                }
            }
        }
    };

    private CameraCaptureSession.StateCallback mCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            if (mCamera == null || cameraCaptureSession == null) {
                return;
            }
            mCaptureSession = cameraCaptureSession;
            doPreView(mWidth, mHeight);
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            dispatchError(new SmartCamPreviewError());
        }
    };

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            final byte[] imageData = data;

            if (mCamera2Wrapper.getCaptureCallback() != null) {
                new ImageSaver(new SmartCamCaptureResult(
                        imageData
                        , CameraVersion.VERSION_2
                        //System has auto fix this right orientation, not need to get the right degree to roate
//            SmartCamUtils.getShouldRotateOrientationForCamera2(mDegree, mCamera2Wrapper.getCurrentCameraType());
                        , 0
                        , SmartCamUtils.isShouldReverse(mCamera2Wrapper.getCurrentCameraType())
                        , mWidth, mHeight, mCamera2Wrapper.getPreviewRatio()),
                        mCamera2Wrapper.getCaptureCallback()).start();
            }

            if (SmartCamConfig.getInstance().isAutoReset()) {
                startPreview(mWidth, mHeight);
            }
        }
    };

    public Camera2Preview(Context context, Camera2Wrapper camera2Wrapper) {
        super(context);
        mCamera2Wrapper = camera2Wrapper;
        mCamera = camera2Wrapper.getCamera();
        init();
    }

    public Camera2Preview(Context context) {
        super(context);
        init();
    }

    public Camera2Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Camera2Preview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Camera2Preview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (SmartCamConfig.getInstance().isUseGestureToZoom()) {
            setClickable(true);
        }
        setSurfaceTextureListener(this);
        mOrientationEventListener = new SmartCamOrientationEventListener(getContext(), this);
        mOrientationEventListener.enable();

        //init click flash light listener
        mOnFlashChangeListener = new Camera2Wrapper.OnFlashChangeListener() {
            @Override
            public void onFlashChange() {
                startPreview(mWidth, mHeight);
            }
        };
        mCamera2Wrapper.setOnFlashChangeListener(mOnFlashChangeListener);

        mOnZoomChangeListener = new Camera2Wrapper.OnZoomChangeListener() {
            @Override
            public void onZoomChange(float zoom) {
                doPreView(mWidth, mHeight);
            }
        };
        mCamera2Wrapper.setOnZoomChangeListener(mOnZoomChangeListener);

        //init click capture listener
        mOnClickCaptureListener = new AbsCameraWrapper.OnClickCaptureListener() {
            @Override
            public void onCapture() {
                if (mCaptureSession == null) {
                    return;
                }
                capture();
            }

        };
        mCamera2Wrapper.setOnClickCaptureListener(mOnClickCaptureListener);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        SmartCamLog.i(TAG, "onSurfaceTextureAvailable");
        mWidth = width;
        mHeight = height;
        startPreview(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera2Wrapper.closeFlashMode();
        mCamera2Wrapper.close();
        SmartCamLog.i(TAG, "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mOnManualFocusListener != null) {
                mOnManualFocusListener.requestFocus(event.getX(), event.getY());
                if (mOnManualFocusListener.getFocusRegion() != null) {
                    doPreView(mWidth, mHeight);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 2
                && SmartCamConfig.getInstance().isUseGestureToZoom()) {
            float gesturePointDistance = SmartCamUtils.getPointsDistance(event.getX(0), event.getY(0)
                    , event.getX(1), event.getY(1));
            if (mLastGesturePointDistance != 0) {
                float changeDistance = mLastGesturePointDistance - gesturePointDistance;
                if (changeDistance > 0) {
                    // zoom smaller
                    dispatchGestureToZoomEvent(true, changeDistance);
                } else if (changeDistance < 0) {
                    //zoom bigger
                    dispatchGestureToZoomEvent(false, Math.abs(changeDistance));
                }
            }
            mLastGesturePointDistance = gesturePointDistance;

        }
        return super.onTouchEvent(event);
    }

    private void startPreview(final int width, final int height) {

        if (mCamera == null) {
            mCamera2Wrapper.resumeOpen();
            mCamera = mCamera2Wrapper.getCamera();
        }

        //still empty
        if (mCamera == null) {
            return;
        }

        SurfaceTexture texture = getSurfaceTexture();
        Surface surface = null;

        try {

            CameraSize largestOutputSize = mCamera2Wrapper.getMaxOutputSize();
            SmartCamLog.i(TAG, "photo size:"
                    + largestOutputSize.getWidth() + "/" + largestOutputSize.getHeight());

            if (mCamera2Wrapper.getPreviewRatio() == null) {
                mPreviewSize = mCamera2Wrapper.getCompatPreviewSize(height, width);
            } else {
                mPreviewSize = mCamera2Wrapper.getCompatPreviewSizeByRatio(mCamera2Wrapper.getPreviewRatio(), height, width);
            }
            SmartCamLog.i(TAG, "preview size:"
                    + mPreviewSize.getWidth() + "/" + mPreviewSize.getHeight());

            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            surface = new Surface(texture);

            mImageReader = ImageReader.newInstance(largestOutputSize.getWidth(), largestOutputSize.getHeight()
                    , ImageFormat.JPEG, 2);

            mPreviewRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, SmartCamUtils.getWindowDisplayRotation(getContext()));
            mPreviewRequestBuilder.addTarget(surface);

            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mCamera2Wrapper.getHandler());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (mWidth == 0 || mHeight == 0) {
                    return;
                }
                List<OutputConfiguration> outputConfigurationList = new ArrayList<>();
                outputConfigurationList.add(new OutputConfiguration(surface));
                outputConfigurationList.add(new OutputConfiguration(mImageReader.getSurface()));
                if (mExecutor == null) {
                    mExecutor = Executors.newSingleThreadExecutor();
                }
                SessionConfiguration sessionConfiguration = new SessionConfiguration(
                        SessionConfiguration.SESSION_REGULAR, outputConfigurationList, mExecutor, mCaptureSessionStateCallback);
                mCamera.createCaptureSession(sessionConfiguration);
            } else {
                mCamera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                        mCaptureSessionStateCallback, mCamera2Wrapper.getHandler());
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
            dispatchError(new SmartCamOpenError());
        } catch (Exception e) {
            e.printStackTrace();
            dispatchError(new SmartCamUnknownError());
        }
    }

    private void doPreView(int width, int height) {
        if (mCaptureSession == null) {
            return;
        }
        try {

            mCaptureSession.stopRepeating();
            if (SmartCamConfig.getInstance().isUseManualFocus()) {
                //cancel any existing AF trigger (repeated touches, etc.)
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            }
            mCaptureSession.capture(mPreviewRequestBuilder.build(), null, null);

            //if use manual focus
            if (SmartCamConfig.getInstance().isUseManualFocus()
                    && mOnManualFocusListener != null && mOnManualFocusListener.getFocusRegion() != null) {
                MeteringRectangle meteringRectangle = new MeteringRectangle(
                        mOnManualFocusListener.getFocusRegion(), MeteringRectangle.METERING_WEIGHT_MAX - 1);
                MeteringRectangle[] regions = new MeteringRectangle[]{meteringRectangle};
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, regions);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

            } else {
                if (mOnManualFocusListener.getFocusRegion() == null) {
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, null);
                }
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);

                if (mCamera2Wrapper.canFocusAuto()) {
                    // Auto focus should be continuous for camera preview.
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                }
            }

            mPreviewRequestBuilder.set(CaptureRequest.JPEG_QUALITY
                    , (byte) SmartCamConfig.getInstance().getCaptureQuality());

            //if has set zoom level
            if (mCamera2Wrapper.getZoom() != 0) {
                mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, getZoomRect());
            } else {
                mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, null);
            }

            //resume params has set
            mPreviewRequestBuilder = mCamera2Wrapper.resumeParams(mPreviewRequestBuilder);
            mPreviewRequest = mPreviewRequestBuilder.build();
            mCaptureSession.setRepeatingRequest(mPreviewRequest,
                    mCaptureCallback, mCamera2Wrapper.getHandler());
            mCaptureSession.capture(mPreviewRequest, mCaptureCallback, mCamera2Wrapper.getHandler());

            if (mCamera2Wrapper.getPreviewRatio() == null) {
                mMatrix = SmartCamUtils.getBetterPreviewScaleMatrix(width, height, mPreviewSize);
                if (mMatrix != null) {
                    setTransform(mMatrix);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            dispatchError(new SmartCamOpenError());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            dispatchError(new SmartCamOpenError());
        } catch (Exception e) {
            e.printStackTrace();
            dispatchError(new SmartCamUnknownError());
        }
    }

    /**
     * capture photo
     */
    private void capture() {

        if (mCaptureSession == null) {
            return;
        }
        try {
            CaptureRequest.Builder captureRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder = mCamera2Wrapper.resumeParams(captureRequestBuilder);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, SmartCamUtils.getShouldRotateDegree(
                    mCamera2Wrapper.getCurrentCameraType(), mDegree));
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureRequestBuilder.build(), null, null);

            if (mOnCaptureAnimationListener != null) {
                mOnCaptureAnimationListener.onPlay();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            stopCaptureAnimation();
            dispatchError(new SmartCamOpenError());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            stopCaptureAnimation();
            dispatchError(new SmartCamOpenError());
        } catch (Exception e) {
            e.printStackTrace();
            stopCaptureAnimation();
            dispatchError(new SmartCamUnknownError());
        }
    }

    private void stopCaptureAnimation() {
        if (mOnCaptureAnimationListener != null) {
            mOnCaptureAnimationListener.onStop();
        }
    }

    @Override
    public void onOrientationChange(int degree) {
        if (Math.abs(degree - mDegree) > SmartCamConfig.getInstance().getDismissManualFocusDegreeOffset()) {
            if (mOnManualFocusListener != null) {
                mOnManualFocusListener.cancelFocus();
            }
        }
        mDegree = degree;
    }

    @Override
    public void startPreview() {
        if (mCamera == null || mWidth == 0 || mHeight == 0 || mCaptureSession == null) {
            return;
        }
        startPreview(mWidth, mHeight);
    }

    @Override
    public void stopPreview() {
        if (mCaptureSession == null) {
            return;
        }
        try {
            mCaptureSession.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try {
            mOrientationEventListener.disable();
            onSurfaceTextureDestroyed(getSurfaceTexture());

            if (mCaptureSession != null) {
                stopPreview();
                mCaptureSession = null;
            }

            if (mOnZoomChangeListener != null) {
                mOnZoomChangeListener = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        doPreView(mWidth, mHeight);
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
        if (mCamera2Wrapper == null || mCamera2Wrapper.getCaptureCallback() == null
                || smartCamError == null) {
            return;
        }
        mCamera2Wrapper.getCaptureCallback().onError(smartCamError);
    }

    /**
     * get zoom rect
     *
     * @return
     */
    private Rect getZoomRect() {

        float newZoom = mCamera2Wrapper.getZoom() / 10f;
        final int centerX = getMeasuredWidth() / 2;
        final int centerY = getMeasuredHeight() / 2;
        final int deltaX = (int) ((0.5f * getMeasuredWidth()) / newZoom);
        final int deltaY = (int) ((0.5f * getMeasuredHeight()) / newZoom);

        Rect cropRegion = new Rect();
        cropRegion.set(centerX - deltaX,
                centerY - deltaY,
                centerX + deltaX,
                centerY + deltaY);
        return cropRegion;
    }

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
