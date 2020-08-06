package com.arjinmc.smartcam.core.wrapper;

/**
 * interface fot CameraPreviewWrapper
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public interface ICameraPreviewWrapper {

    /**
     * dispatch degree when Orientation change
     *
     * @param degree
     */
    void onOrientationChange(int degree);

    /**
     * start to preview when capture stop
     */
    void startPreview();

    /**
     * stop preview
     */
    void stopPreview();

    /**
     * destory preview
     */
    void destroy();
}
