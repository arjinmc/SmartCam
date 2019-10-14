package com.arjinmc.smartcam.core;


import android.os.Build;

import com.arjinmc.smartcam.core.camera1.Camera1Wrapper;

/**
 * SmartCam
 * the camera compat for camera v1 and v2
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class SmartCam extends AbsCameraWrapper {

    private AbsCameraWrapper mCameraWrapper;

    @Override
    public boolean open() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        }
        mCameraWrapper = new Camera1Wrapper();
        return mCameraWrapper.open();
    }

    @Override
    public Object getCamera() {
        return mCameraWrapper.getCamera();
    }

    @Override
    public int getCameraCount() {
        return mCameraWrapper.getCameraCount();
    }

    @Override
    public int getOrientation() {
        return mCameraWrapper.getOrientation();
    }

    @Override
    public void logFeatures() {
        mCameraWrapper.logFeatures();
    }
}
