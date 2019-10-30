package com.arjinmc.smartcam.core.camera2;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

import java.util.Arrays;

/**
 * Preview for camera2
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Preview extends TextureView implements TextureView.SurfaceTextureListener, ICameraPreviewWrapper {

    private Camera2Wrapper mCamera2Wrapper;
    private CameraDevice mCamera;
    private Handler mHandler;
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
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
        mHandler = new Handler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e("onSurfaceTextureAvailable", "onSurfaceTextureAvailable");
        startPreview(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e("onSurfaceTextureDestroyed", "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void startPreview(int width, int height) {

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
            mCamera2Wrapper.setPreviewRequestBuilder(mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW));
            mCamera2Wrapper.getPreviewRequestBuilder().addTarget(surface);

            CameraSupportPreviewSize cameraSupportPreviewSize = mCamera2Wrapper.getCompatPreviewSize(width, height);
            if (cameraSupportPreviewSize == null) {
                return;
            }
            Log.e("CameraSupportPreviewSize",cameraSupportPreviewSize.getWidth()+"/"+cameraSupportPreviewSize.getHeight());
            mCamera2Wrapper.setImageReader(ImageReader.newInstance(cameraSupportPreviewSize.getWidth(), cameraSupportPreviewSize.getHeight(),
                    ImageFormat.JPEG, 2));
            mCamera.createCaptureSession(Arrays.asList(surface, mCamera2Wrapper.getImageReader().getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (null == mCamera) {
                                return;
                            }

                            mCamera2Wrapper.setCaptureSession(cameraCaptureSession);

                            try {
                                // Auto focus should be continuous for camera preview.
                                mCamera2Wrapper.getPreviewRequestBuilder().set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // Flash is automatically enabled when necessary.
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                mCamera2Wrapper.setPreviewRequest(mCamera2Wrapper.getPreviewRequestBuilder().build());
                                mCamera2Wrapper.getCaptureSession().setRepeatingRequest(mCamera2Wrapper.getPreviewRequest(),
                                        mCaptureCallback, mHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
        }
    }
}
