package com.arjinmc.smartcam.core.wrapper;

import android.content.Context;

import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.model.CameraSize;

import java.util.List;
import java.util.Map;

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

    void release();

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
     *
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
    List<CameraSize> getSupportPreviewSizes();

    /**
     * get support preview size ratio map
     *
     * @return
     */
    Map<String, List<CameraSize>> getSupportPreviewSizeRatioMap();

    /**
     * get support preview size ratio list
     *
     * @return
     */
    List<String> getSupportPreviewSizeRatioList();

    /**
     * get support preview size list by ratio;
     *
     * @param ratio
     * @return
     */
    List<CameraSize> getSupportPreviewSizeListByRatio(String ratio);

    /**
     * get best preview size that compat ui view with width and height
     *
     * @param ratio
     * @param width
     * @param height
     * @return
     */
    CameraSize getCompatPreviewSizeByRatio(String ratio, int width, int height);

    /**
     * get the calculate  preview size support destination width and height
     *
     * @param width
     * @param height
     * @return
     */
    CameraSize getCompatPreviewSize(int width, int height);

    /**
     * get output sizes that camera supported
     *
     * @return
     */
    List<CameraSize> getOutputSizes();

    /**
     * get the biggest output size that camera supported
     *
     * @return
     */
    CameraSize getMaxOutputSize();

    /**
     * get the orientation rotation
     *
     * @return
     */
    int getOrientation();

    /**
     * check if it can zoom
     *
     * @return
     */
    boolean isZoomAvailable();

    /**
     * get zoom level
     *
     * @return
     */
    float getZoom();

    /**
     * set zoom level
     *
     * @param zoomLevel
     */
    void setZoom(float zoomLevel);

    /**
     * get max zoom
     *
     * @return
     */
    float getMaxZoom();

    /**
     * set state callback
     *
     * @param smartCamStateCallback
     */
    void setStateCallback(SmartCamStateCallback smartCamStateCallback);


    /**
     * set capture callback
     *
     * @param smartCamCaptureCallback
     */
    void setCaptureCallback(SmartCamCaptureCallback smartCamCaptureCallback);


    /**
     * get capture callback
     *
     * @return
     */
    SmartCamCaptureCallback getCaptureCallback();

    /**
     * check if can use camera auto focus(some devices not support)
     *
     * @return
     */
    boolean canFocusAuto();

}
