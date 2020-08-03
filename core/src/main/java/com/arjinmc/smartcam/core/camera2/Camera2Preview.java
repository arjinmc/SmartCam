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
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamOrientationEventListener;
import com.arjinmc.smartcam.core.comparator.CompareSizesByArea;
import com.arjinmc.smartcam.core.file.ImagePathSaver;
import com.arjinmc.smartcam.core.file.ImageUriSaver;
import com.arjinmc.smartcam.core.model.CameraSaveType;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.model.SmartCamOutputOption;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Preview for camera2
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Preview extends TextureView implements TextureView.SurfaceTextureListener, ICameraPreviewWrapper {

    private final String TAG = "Camera2Preview";

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;
    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private Camera2Wrapper mCamera2Wrapper;
    private CameraDevice mCamera;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private ImageReader mImageReader;
    private Camera2Wrapper.OnFlashChangeListener mOnFlashChangeListener;
    private AbsCameraWrapper.OnClickCaptureLisenter mOnClickCaptureLisenter;
    private SmartCamOrientationEventListener mOrientationEventListener;
    private int mWidth, mHeight;
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
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
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
                    mCamera2Wrapper.getHandler().post(new ImagePathSaver(
                            new SmartCamOutputOption(image, mSaveFile, mDegree, mWidth, mHeight, mMatrix)
                            , mCamera2Wrapper.getCaptureCallback()));
                    break;
                case CameraSaveType.TYPE_URI:
                    SmartCamLog.e("CameraSaveType", "ImageUriSaver");
                    mCamera2Wrapper.getHandler().post(new ImageUriSaver(getContext(), image, mDegree
                            , mSaveFileUri, mCamera2Wrapper.getCaptureCallback()));
                    break;
                default:
                    break;

            }
            startPreview(mWidth, mHeight);
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
        mOnClickCaptureLisenter = new AbsCameraWrapper.OnClickCaptureLisenter() {
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
        mCamera2Wrapper.setOnClickCaptureLisenter(mOnClickCaptureLisenter);
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

            CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCamera.getId());
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            CameraSupportPreviewSize largest = Collections.max(
                    SmartCamUtils.convertSizes(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());

            SmartCamLog.e(TAG, "photo size:"
                    + largest.getWidth() + "/" + largest.getHeight());

            final CameraSupportPreviewSize previewSize = chooseOptimalSize(mCamera2Wrapper.getSupperPreviewSizes(), height, width, largest);
            SmartCamLog.e(TAG, "preview size:"
                    + previewSize.getWidth() + "/" + previewSize.getHeight());

            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            surface = new Surface(texture);

            mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight()
                    , ImageFormat.JPEG, 1);

            mPreviewRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, SmartCamUtils.getWindowDisplayRotation(getContext()));
            mPreviewRequestBuilder.addTarget(surface);

            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mCamera2Wrapper.getHandler());

            mCamera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (mCamera == null || cameraCaptureSession == null) {
                                return;
                            }

                            mCaptureSession = cameraCaptureSession;

                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                mPreviewRequestBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);
                                //resume params has set
                                mPreviewRequestBuilder = mCamera2Wrapper.resumeParams(mPreviewRequestBuilder);
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mCamera2Wrapper.getHandler());

                                mMatrix = SmartCamUtils.getBetterPreviewScaleMatrix(width, height, previewSize);
                                if (mMatrix != null) {
                                    setTransform(mMatrix);
                                }
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                        }
                    }, mCamera2Wrapper.getHandler());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * capture phohto
     */
    private void capture() {

        try {
            CaptureRequest.Builder captureRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder = mCamera2Wrapper.resumeParams(captureRequestBuilder);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, SmartCamUtils.getShouldRotateDegree(
                    getContext()
                    , mCamera2Wrapper.getCurrentCameraType()
                    , mCamera2Wrapper.getCurrentCameraId()
                    , SmartCamUtils.getWindowDisplayRotation(getContext())));
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onOrientationChange(int degree) {
        mDegree = degree;
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
    private CameraSupportPreviewSize chooseOptimalSize(List<CameraSupportPreviewSize> choices, int textureViewWidth,
                                                       int textureViewHeight, CameraSupportPreviewSize aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<CameraSupportPreviewSize> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<CameraSupportPreviewSize> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (CameraSupportPreviewSize option : choices) {
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


    @Override
    public void destroy() {
        mOrientationEventListener.disable();
        onSurfaceTextureDestroyed(getSurfaceTexture());
        try {
            if (mCaptureSession != null) {
                mCaptureSession.abortCaptures();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
