package com.arjinmc.smartcam.core;

import android.content.Context;
import android.content.pm.PackageManager;

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
    public boolean hasCamera(Context context) {
        if (context == null) {
            return false;
        }
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
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
    public boolean open() {
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
    public boolean hasFocusAuto() {
        return false;
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
    public List<CameraSupportPreviewSize> getSupperPrieviewSizes() {
        return null;
    }

    @Override
    public int getOrientation() {
        return 0;
    }

}
