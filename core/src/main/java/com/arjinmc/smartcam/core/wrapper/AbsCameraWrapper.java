package com.arjinmc.smartcam.core.wrapper;

import android.content.Context;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.comparator.CompareSizesByArea;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstact Camera Wrapper
 * define camera with functions
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class AbsCameraWrapper implements ICameraWrapper {

    protected Context mContext;

    /**
     * mark current camera type
     */
    protected int mCurrentCameraType = CameraType.CAMERA_NULL;
    protected String mCurrentCameraId = "-1";

    protected SmartCamStateCallback mSmartCamStateCallback;
    protected SmartCamCaptureCallback mSmartCamCaptureCallback;
    protected OnClickCaptureLisenter mOnClickCaptureLisenter;

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public Object getCameraWrapper() {
        return null;
    }

    @Override
    public Object getCamera() {
        return null;
    }

    @Override
    public void open() {

    }

    @Override
    public boolean resumeOpen() {
        return false;
    }


    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public int getCameraCount() {
        return 0;
    }

    @Override
    public void capture(File file) {
        if (mOnClickCaptureLisenter != null) {
            mOnClickCaptureLisenter.onCapture(file);
        }
    }

    @Override
    public void capturePath(String filePath) {
        if (mOnClickCaptureLisenter != null) {
            mOnClickCaptureLisenter.onCapturePath(filePath);
        }
    }

    @Override
    public void captureUri(String fileUri) {
        if (mOnClickCaptureLisenter != null) {
            mOnClickCaptureLisenter.onCaptureUri(fileUri);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void logFeatures() {

    }

    @Override
    public void openFlashMode() {
    }

    @Override
    public void closeFlashMode() {

    }

    @Override
    public void torchFlashMode() {

    }

    @Override
    public void autoFlashMode() {

    }

    @Override
    public int getFlashMode() {
        return 0;
    }

    @Override
    public boolean isBackCamera() {
        return true;
    }

    @Override
    public void switchToFrontCamera() {

    }

    @Override
    public void switchToBackCamera() {

    }

    @Override
    public boolean isLock() {
        return false;
    }

    @Override
    public int getCurrentCameraType() {
        return 0;
    }

    @Override
    public String getCurrentCameraId() {
        return mCurrentCameraId;
    }


    @Override
    public List<CameraSize> getSupperPreviewSizes() {
        return null;
    }

    @Override
    public CameraSize getCompatPreviewSize(int width, int height) {

        return chooseOptimalSize(getSupperPreviewSizes(), width, height, getMaxOutputSize());

//        List<CameraSupportPreviewSize> supportPreviewSizes = getSupperPreviewSizes();
//        if (supportPreviewSizes == null || supportPreviewSizes.isEmpty()) {
//            return null;
//        }
//
//        if (height == 0) {
//            return null;
//        }
//
////        Log.e("SurfaceTexture", width + "/" + height);
//        int postion = -1;
//        double minOffset = 0;
//        int size = supportPreviewSizes.size();
//        for (int i = 0; i < size; i++) {
//            CameraSupportPreviewSize cameraSupportPreviewSize = supportPreviewSizes.get(i);
//            double offset = Math.abs(cameraSupportPreviewSize.getWidth() / cameraSupportPreviewSize.getHeight() - width / height);
////            Log.e("PreviewSize", cameraSupportPreviewSize.getWidth() + "/" + cameraSupportPreviewSize.getHeight());
//            if (offset == 0) {
//                postion = i;
//                break;
//            }
//
//            if (minOffset == 0) {
//                minOffset = offset;
//                postion = i;
//            }
//            if (minOffset > offset) {
//                minOffset = offset;
//                postion = i;
//            }
//        }
//        if (postion == -1) {
//            return null;
//        }
//        return supportPreviewSizes.get(postion);
    }

    @Override
    public List<CameraSize> getOutputSizes() {
        return null;
    }

    @Override
    public CameraSize getMaxOutputSize() {
        List<CameraSize> cameraSizes = getOutputSizes();
        if (cameraSizes == null || cameraSizes.isEmpty()) {
            return null;
        }
        CameraSize largest = Collections.max(cameraSizes, new CompareSizesByArea());
        return largest;
    }

    @Override
    public int getOrientation() {
        return 0;
    }

    @Override
    public int getZoom() {
        return 0;
    }

    @Override
    public void setZoom(int zoomLevel) {

    }

    @Override
    public void setStateCallback(SmartCamStateCallback smartCamStateCallback) {
        mSmartCamStateCallback = smartCamStateCallback;
    }

    @Override
    public void setCaptureCallback(SmartCamCaptureCallback smartCamCaptureCallback) {
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    @Override
    public SmartCamCaptureCallback getCaptureCallback() {
        return mSmartCamCaptureCallback;
    }


    @Override
    public boolean hasFocusAuto() {
        return false;
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
    private CameraSize chooseOptimalSize(List<CameraSize> choices, int textureViewWidth,
                                         int textureViewHeight, CameraSize aspectRatio) {
//        SmartCamLog.e("textureViewWidth", textureViewWidth + "," + textureViewHeight);
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<CameraSize> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<CameraSize> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (CameraSize option : choices) {
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
            SmartCamLog.e("AbsCameraWrapper", "Couldn't find any suitable preview size");
            return choices.get(0);
        }
    }

    public void setOnClickCaptureLisenter(OnClickCaptureLisenter onClickCaptureLisenter) {
        mOnClickCaptureLisenter = onClickCaptureLisenter;
    }

    /**
     * listener for capture photo
     */
    public interface OnClickCaptureLisenter {
        void onCapture(File file);

        void onCapturePath(String filePath);

        void onCaptureUri(String fileUri);
    }

}
