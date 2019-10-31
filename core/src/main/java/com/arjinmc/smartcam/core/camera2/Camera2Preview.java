package com.arjinmc.smartcam.core.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
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
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
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
                    mCamera2Wrapper.getHandler().post(new ImagePathSaver(image, mDegree, mSaveFile
                            , mCamera2Wrapper.getCaptureCallback()));
                    break;
                case CameraSaveType.TYPE_URI:
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        SmartCamLog.e(TAG, "onSurfaceTextureAvailable");
        startPreview(width, height);

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera2Wrapper.closeFlashMode();
        SmartCamLog.e(TAG, "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void startPreview(int width, int height) {

        mWidth = width;
        mHeight = height;

        if (mCamera == null) {
            mCamera2Wrapper.resumeOpen();
            mCamera = mCamera2Wrapper.getCamera();
        }

        //still empty
        if (mCamera == null) {
            return;
        }
        SurfaceTexture texture = getSurfaceTexture();
        texture.setDefaultBufferSize(width, height);
        Surface surface = new Surface(texture);

        try {
            mPreviewRequestBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, SmartCamUtils.getWindowDisplayRotation(getContext()));
            mPreviewRequestBuilder.addTarget(surface);

            CameraSupportPreviewSize cameraSupportPreviewSize = mCamera2Wrapper.getCompatPreviewSize(width, height);
            if (cameraSupportPreviewSize == null) {
                return;
            }
            SmartCamLog.e(TAG, "CameraSupportPreviewSize:"
                    + cameraSupportPreviewSize.getWidth() + "/" + cameraSupportPreviewSize.getHeight());
            mImageReader = ImageReader.newInstance(cameraSupportPreviewSize.getWidth(), cameraSupportPreviewSize.getHeight(),
                    ImageFormat.JPEG, 2);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mCamera2Wrapper.getHandler());

            mCamera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (null == mCamera) {
                                return;
                            }

                            mCaptureSession = cameraCaptureSession;

                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                //resume params has set
                                mPreviewRequestBuilder = mCamera2Wrapper.resumeParams(mPreviewRequestBuilder);
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mCamera2Wrapper.getHandler());
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
