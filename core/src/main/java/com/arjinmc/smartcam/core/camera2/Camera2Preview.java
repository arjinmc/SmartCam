package com.arjinmc.smartcam.core.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
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
import com.arjinmc.smartcam.core.file.ImagePathSaver;
import com.arjinmc.smartcam.core.file.ImageUriSaver;
import com.arjinmc.smartcam.core.model.CameraSaveType;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.SmartCamOutputOption2;
import com.arjinmc.smartcam.core.wrapper.AbsCameraWrapper;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

import java.io.File;
import java.util.Arrays;

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
    private AbsCameraWrapper.OnClickCaptureListener mOnClickCaptureListener;
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
                            new SmartCamOutputOption2(image, mSaveFile, mDegree
                                    , mWidth, mHeight, mMatrix, mCamera2Wrapper.getCurrentCameraType())
                            , mCamera2Wrapper.getCaptureCallback()));
                    break;
                case CameraSaveType.TYPE_URI:
                    SmartCamLog.e("CameraSaveType", "ImageUriSaver");
                    mCamera2Wrapper.getHandler().post(new ImageUriSaver(getContext()
                            , new SmartCamOutputOption2(image, mSaveFileUri, mDegree
                            , mWidth, mHeight, mMatrix, mCamera2Wrapper.getCurrentCameraType())
                            , mCamera2Wrapper.getCaptureCallback()));
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
        mCamera2Wrapper.setOnClickCaptureLisenter(mOnClickCaptureListener);
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

            CameraSize largestOutputSize = mCamera2Wrapper.getMaxOutputSize();
            SmartCamLog.i(TAG, "photo size:"
                    + largestOutputSize.getWidth() + "/" + largestOutputSize.getHeight());

            final CameraSize previewSize = mCamera2Wrapper.getCompatPreviewSize(height, width);
            SmartCamLog.i(TAG, "preview size:"
                    + previewSize.getWidth() + "/" + previewSize.getHeight());

            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            surface = new Surface(texture);

            mImageReader = ImageReader.newInstance(largestOutputSize.getWidth(), largestOutputSize.getHeight()
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

        if (mCaptureSession == null) {
            return;
        }
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
                mCaptureSession = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
