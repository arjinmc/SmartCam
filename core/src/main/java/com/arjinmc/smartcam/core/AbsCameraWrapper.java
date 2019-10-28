package com.arjinmc.smartcam.core;

import com.arjinmc.smartcam.core.model.CameraFlashMode;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.model.CameraType;

import java.util.List;

/**
 * Abstact Camera Wrapper
 * define camera with functions
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class AbsCameraWrapper implements ICameraWrapper {

    /**
     * mark current camera type
     */
    protected int mCurrentCameraType = CameraType.CAMERA_NULL;
    protected int mCurrentCameraId = -1;

    @Override
    public Object getCameraWrapper() {
        return null;
    }

    @Override
    public Object getCamera() {
        return null;
    }

    @Override
    public boolean open() {
        return false;
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
    public void capture() {

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
    public int getCurrentCameraType() {
        return 0;
    }

    @Override
    public int getCurrentCameraId() {
        return mCurrentCameraId;
    }


    @Override
    public List<CameraSupportPreviewSize> getSupperPreviewSizes() {
        return null;
    }

    @Override
    public CameraSupportPreviewSize getCompatPreviewSize(int width, int height) {
        List<CameraSupportPreviewSize> supportPreviewSizes = getSupperPreviewSizes();
        if (supportPreviewSizes == null || supportPreviewSizes.isEmpty()) {
            return null;
        }

        if (height == 0) {
            return null;
        }

        int postion = -1;
        double minOffset = 0;
        int size = supportPreviewSizes.size();
        for (int i = 0; i < size; i++) {
            CameraSupportPreviewSize cameraSupportPreviewSize = supportPreviewSizes.get(i);
            double offset = Math.abs(cameraSupportPreviewSize.getWidth() / cameraSupportPreviewSize.getHeight() - width / height);
            if (offset == 0) {
                postion = i;
                break;
            }

            if (minOffset == 0) {
                minOffset = offset;
                postion = i;
            }
            if (minOffset > offset) {
                minOffset = offset;
                postion = i;
            }
        }
        if (postion == -1) {
            return null;
        }
        return supportPreviewSizes.get(postion);
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
    public boolean hasFocusAuto() {
        return false;
    }

}
