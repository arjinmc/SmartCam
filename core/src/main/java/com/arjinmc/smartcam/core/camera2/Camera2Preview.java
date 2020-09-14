package com.arjinmc.smartcam.core.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
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
import com.arjinmc.smartcam.core.file.ImagePathSaver;
import com.arjinmc.smartcam.core.file.ImageUriSaver;
import com.arjinmc.smartcam.core.model.CameraSaveType;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;
import com.arjinmc.smartcam.core.model.SmartCamError;
import com.arjinmc.smartcam.core.model.SmartCamOpenError;
import com.arjinmc.smartcam.core.model.SmartCamOutputOption2;
import com.arjinmc.smartcam.core.model.SmartCamPreviewError;
import com.arjinmc.smartcam.core.model.SmartCamUnknownError;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

import java.io.File;
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
    private AbsCameraWrapper.OnClickCaptureListener mOnClickCaptureListener;
    private SmartCamOrientationEventListener mOrientationEventListener;
    private SmartCamPreview.OnManualFocusListener mOnManualFocusListener;
    private int mWidth, mHeight;
    private CameraSize mPreviewSize;
    private int mCameraSaveType;
    private File mSaveFile;
    private String mSaveFileUri;
    /**
     * the degreee when capture
     */
    private Integer mDegree;
    private Matrix mMatrix;

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

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            if (mCamera2Wrapper.canManualFocus() && SmartCamConfig.getInstance().isUseManualFocus()
                    && mPreviewRequestBuilder != null
                    && mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_TRIGGER) != null) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                doPreView(mWidth, mHeight);
            }
            super.onCaptureCompleted(session, request, result);
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
            switch (mCameraSaveType) {
                case CameraSaveType.TYPE_FILE:
                case CameraSaveType.TYPE_PATH:
                    SmartCamLog.e("CameraSaveType", "ImagePathSaver");
                    new ImagePathSaver(
                            new SmartCamOutputOption2(image, mSaveFile, mDegree
                                    , mWidth, mHeight, mMatrix, mCamera2Wrapper.getCurrentCameraType())
                            , mCamera2Wrapper.getCaptureCallback()).run();
                    break;
                case CameraSaveType.TYPE_URI:
                    SmartCamLog.e("CameraSaveType", "ImageUriSaver");
                    new ImageUriSaver(getContext()
                            , new SmartCamOutputOption2(image, mSaveFileUri, mDegree
                            , mWidth, mHeight, mMatrix, mCamera2Wrapper.getCurrentCameraType())
                            , mCamera2Wrapper.getCaptureCallback()).run();
                    break;
                default:
                    break;

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

        //init click capture listener
        mOnClickCaptureListener = new AbsCameraWrapper.OnClickCaptureListener() {
            @Override
            public void onCapture(File file) {

                if (mCaptureSession == null) {
                    return;
                }
                mCameraSaveType = CameraSaveType.TYPE_FILE;
                mSaveFile = file;
                capture();
            }

            @Override
            public void onCapturePath(String filePath) {
                if (mCaptureSession == null) {
                    return;
                }
                if (!TextUtils.isEmpty(filePath)) {
                    mSaveFile = new File(filePath);
                }
                mCameraSaveType = CameraSaveType.TYPE_PATH;
                capture();
            }

            @Override
            public void onCaptureUri(String fileUri) {

                if (mCaptureSession == null) {
                    return;
                }
                mSaveFileUri = fileUri;
                mCameraSaveType = CameraSaveType.TYPE_URI;
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

            mPreviewSize = mCamera2Wrapper.getCompatPreviewSize(height, width);
            SmartCamLog.i(TAG, "preview size:"
                    + mPreviewSize.getWidth() + "/" + mPreviewSize.getHeight());

            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            surface = new Surface(texture);

            mImageReader = ImageReader.newInstance(largestOutputSize.getWidth(), largestOutputSize.getHeight()
                    , ImageFormat.JPEG, 1);

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
            if (mCamera2Wrapper.canManualFocus()) {
                //cancel any existing AF trigger (repeated touches, etc.)
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
            }
            mCaptureSession.capture(mPreviewRequestBuilder.build(), null, null);

            //if use manual focus
            if (mCamera2Wrapper.canManualFocus() && SmartCamConfig.getInstance().isUseManualFocus()
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

            //resume params has set
            mPreviewRequestBuilder = mCamera2Wrapper.resumeParams(mPreviewRequestBuilder);
            mPreviewRequest = mPreviewRequestBuilder.build();
            mCaptureSession.setRepeatingRequest(mPreviewRequest,
                    mCaptureCallback, mCamera2Wrapper.getHandler());
            mCaptureSession.capture(mPreviewRequest, mCaptureCallback, mCamera2Wrapper.getHandler());

            mMatrix = SmartCamUtils.getBetterPreviewScaleMatrix(width, height, mPreviewSize);
            if (mMatrix != null) {
                setTransform(mMatrix);
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
//            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, SmartCamUtils.getShouldRotateDegree(
//                    getContext()
//                    , mCamera2Wrapper.getCurrentCameraType()
//                    , mCamera2Wrapper.getCurrentCameraId()
//                    , SmartCamUtils.getCaptureOrientation(mDegree)));
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, SmartCamUtils.getShouldRotateDegree(
                    mCamera2Wrapper.getCurrentCameraType(), mDegree));

            Log.e("my1", SmartCamUtils.getShouldRotateDegree(
                    mCamera2Wrapper.getCurrentCameraType(), mDegree) + "");
            CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCamera2Wrapper.getCurrentCameraId());
            Log.e("my2", getJpegOrientation(characteristics, SmartCamUtils.getWindowDisplayRotation(getContext())) + "");
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureRequestBuilder.build(), null, null);
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

    private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) {
            return 0;
        }
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;

        return jpegOrientation;
    }

    @Override
    public void onOrientationChange(int degree) {
        if (mDegree != null
                && Math.abs(degree - mDegree) > SmartCamConfig.getInstance().getDismissManualFocusDegreeOffset()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnManualFocusListener(SmartCamPreview.OnManualFocusListener onManualFocusListener) {
        mOnManualFocusListener = onManualFocusListener;
    }

    private void dispatchError(SmartCamError smartCamError) {
        if (mCamera2Wrapper == null || mCamera2Wrapper.getCaptureCallback() == null
                || smartCamError == null) {
            return;
        }
        mCamera2Wrapper.getCaptureCallback().onError(smartCamError);
    }
}
