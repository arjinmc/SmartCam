package com.arjinmc.smartcam.core;

import android.content.Context;

import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;

import java.util.List;

/**
 * Camera Wrapper for different version camera
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public interface ICameraWrapper {

    /**
     * check if has camera
     *
     * @return
     */
    boolean hasCamera(Context context);

    /**
     * get camera wrapper
     *
     * @return
     */
    Object getCameraWrapper();

    /**
     * get camera object
     *
     * @return
     */
    Object getCamera();

    /**
     * open camera
     */
    boolean open();

    /**
     * if camera is open
     *
     * @return
     */
    boolean isOpen();

    /**
     * get Camera Count
     *
     * @return count
     */
    int getCameraCount();

    /**
     * capture for photo
     */
    void capture();

    /**
     * close
     */
    void close();

    void pause();

    void resume();

    /**
     * log the features of cameara
     */
    void logFeatures();

    /**
     * open flassh mode
     */
    void openFlashMode();

    /**
     * close flast mode
     */
    void closeFlashMode();

    /**
     * check if has focus auto mode
     *
     * @return
     */
    boolean hasFocusAuto();

    /**
     * switch to front camera
     */
    void switchToFrontCamera();

    /**
     * switch to back camera
     */
    void switchToBackCamera();

    /**
     * get current camera type ：is front or back
     *
     * @return {@link com.arjinmc.smartcam.core.model.CameraType}
     */
    int getCurrentCameraType();

    /**
     * get current camera id
     *
     * @return
     */
    int getCurrentCameraId();

    /**
     * get preview sizes that current camera supported
     *
     * @return
     */
    List<CameraSupportPreviewSize> getSupperPreviewSizes();

    /**
     * get the calculate  preview size support destination width and height
     *
     * @param width
     * @param height
     * @return
     */
    CameraSupportPreviewSize getCompatPreviewSize(int width, int height);

    /**
     * get the orientation rotation
     *
     * @return
     */
    int getOrientation();
}
