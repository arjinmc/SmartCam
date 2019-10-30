package com.arjinmc.smartcam.core.wrapper;

import android.content.Context;

import com.arjinmc.smartcam.core.callback.SmartCamStateListener;
import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;

import java.util.List;

/**
 * Camera Wrapper for different version camera
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public interface ICameraWrapper {


    Context getContext();

    void setContext(Context context);

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
    void open();

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
     * open flash mode
     */
    void openFlashMode();

    /**
     * close flash mode
     */
    void closeFlashMode();

    /**
     * always on
     */
    void torchFlashMode();

    /**
     * auto flahs mode
     */
    void autoFlashMode();

    int getFlashMode();

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
     * check if is lock
     * @return
     */
    boolean isLock();

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
    String getCurrentCameraId();

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

    /**
     * set state listener
     *
     * @param smartCamStateListener
     */
    void setStateListener(SmartCamStateListener smartCamStateListener);

    boolean hasFocusAuto();
}
