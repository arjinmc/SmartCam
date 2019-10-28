package com.arjinmc.smartcam.core;

import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;

import java.util.List;

/**
 * Camera Wrapper for different version camera
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public interface ICameraWrapper {

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
     * resume the open camera
     *
     * @return
     */
    boolean resumeOpen();

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
     * check if it has flash light
     *
     * @return
     */
    boolean hasFlashMode();

    /**
     * open flash mode
     */
    void openFlashMode();

    /**
     * close flash mode
     */
    void closeFlashMode();

    /**
     * auto flahs mode
     */
    void autoFlashMode();

    /**
     * check if has focus auto mode
     *
     * @return
     */
    boolean hasFocusAuto();

    /**
     * check if current camera is back camera
     *
     * @return
     */
    boolean isBackCamera();

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

    /**
     * get zoom level
     *
     * @return
     */
    int getZoom();

    /**
     * set zoom level
     *
     * @param zoomLevel
     */
    void setZoom(int zoomLevel);
}
