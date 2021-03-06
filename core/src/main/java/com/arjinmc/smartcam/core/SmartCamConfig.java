package com.arjinmc.smartcam.core;

import com.arjinmc.smartcam.core.model.CameraManualFocusParams;

/**
 * Created by Eminem Lo on 2019-10-29.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamConfig {

    /**
     * timeout duration for lock for witch camera,unit:TimeUnit.MILLISECONDS
     */
    public static final long LOCK_TIMEOUT_DURATION = 1000;
    /**
     * Default capture quality
     */
    private final int DEFAULT_CAPTURE_QUALITY = 70;
    /**
     * Default output quality
     */
    private final int DEFAULT_OUTPUT_QUALITY = 100;
    /**
     * Default capture animation duration(unit:ms)
     */
    private final int DEFAULT_CAPTURE_ANIMATION_DURATION = 280;
    /**
     * Default auto dismiss manual focus view when camera orientation changed offset
     */
    private final int DEFAULT_MANUAL_FOCUS_DISMISS_DEGREE_OFFSET = 10;
    /**
     * Default moving factor for gesture to zoom
     */
    private final int DEFAULT_GESTURE_MOVING_FACTOR = 2;

    /**
     * root dir path
     */
    private String mRootDirPath;

    /**
     * root dir for saving files (default name: SmartCam)
     */
    private String mRootDirName = "SmartCam";

    /**
     * auto reset to preview after capture
     */
    private boolean mAutoReset = false;
    /**
     * capture quality
     */
    private int mCaptureQuality = DEFAULT_CAPTURE_QUALITY;
    /**
     * output image quality
     */
    private int mOutputQuality = DEFAULT_OUTPUT_QUALITY;
    /**
     * capture animation duration
     */
    private int mCaptureAnimationDuration = DEFAULT_CAPTURE_ANIMATION_DURATION;
    /**
     * make camera use manual focus
     */
    private boolean mUseManualFocus = true;

    /**
     * when the camera orientation changed above offset value then auto hide the manual focus view
     */
    private int mDismissManualFocusDegreeOffset = DEFAULT_MANUAL_FOCUS_DISMISS_DEGREE_OFFSET;

    /**
     * manual focus params
     */
    private CameraManualFocusParams mCameraManualFocusParams;

    /**
     * use gesture to zoom
     */
    private boolean mUseGestureToZoom;

    /**
     * moving factor for gesture to zoom, avoid zoom too fast that it seems not smooth enough
     * unit:px
     */
    private int mGestureMovingFactor = DEFAULT_GESTURE_MOVING_FACTOR;

    private static SmartCamConfig mSmartCamConfig;

    public static SmartCamConfig getInstance() {
        if (mSmartCamConfig == null) {
            mSmartCamConfig = new SmartCamConfig();
        }
        return mSmartCamConfig;
    }

    /**
     * get dir name for saving files
     *
     * @return
     */
    public String getRootDirName() {
        return mRootDirName;
    }

    /**
     * set dir name for saving files
     *
     * @param dirName
     */
    public void setRootDir(String dirName) {
        mRootDirName = dirName;
    }

    /**
     * get root dir path
     *
     * @return
     */
    public String getRootDirPath() {
        return mRootDirPath;
    }

    /**
     * set root dir path
     *
     * @param dirPath
     */
    public void setRootDirPath(String dirPath) {
        mRootDirPath = dirPath;
    }

    public boolean isAutoReset() {
        return mAutoReset;
    }

    public void setAutoReset(boolean autoReset) {
        mAutoReset = autoReset;
    }

    /**
     * get capture quality
     *
     * @return
     */
    public int getCaptureQuality() {
        return mCaptureQuality;
    }

    /**
     * set capture quality
     *
     * @param captureQuality
     */
    public void setCaptureQuality(int captureQuality) {
        if (captureQuality < 0 || captureQuality > DEFAULT_CAPTURE_QUALITY) {
            mCaptureQuality = DEFAULT_CAPTURE_QUALITY;
        } else {
            mCaptureQuality = captureQuality;
        }
    }

    public int getOutputQuality() {
        return mOutputQuality;
    }

    public void setOutputQuality(int outputQuality) {
        if (outputQuality < 0 || outputQuality > DEFAULT_OUTPUT_QUALITY) {
            mOutputQuality = DEFAULT_OUTPUT_QUALITY;
        } else {
            mOutputQuality = outputQuality;
        }
    }

    public int getCaptureAnimationDuration() {
        return mCaptureAnimationDuration;
    }

    public void setmCaptureAnimationDuration(int captureAnimationDuration) {
        if (captureAnimationDuration < 100) {
            captureAnimationDuration = DEFAULT_CAPTURE_ANIMATION_DURATION;
        }
        this.mCaptureAnimationDuration = captureAnimationDuration;
    }

    public boolean isUseManualFocus() {
        return mUseManualFocus;
    }

    public void setUseManualFocus(boolean useManualFocus) {
        this.mUseManualFocus = useManualFocus;
    }

    public int getDismissManualFocusDegreeOffset() {
        return mDismissManualFocusDegreeOffset;
    }

    public void setDismissManualFocusDegreeOffset(int dismissManualFocusDegreeOffset) {
        if (dismissManualFocusDegreeOffset <= 0) {
            dismissManualFocusDegreeOffset = DEFAULT_MANUAL_FOCUS_DISMISS_DEGREE_OFFSET;
        }
        this.mDismissManualFocusDegreeOffset = dismissManualFocusDegreeOffset;
    }

    public CameraManualFocusParams getCameraManualFocusParams() {
        return mCameraManualFocusParams;
    }

    public void setCameraManualFocusParams(CameraManualFocusParams cameraManualFocusParams) {
        this.mCameraManualFocusParams = cameraManualFocusParams;
    }

    public boolean isUseGestureToZoom() {
        return mUseGestureToZoom;
    }

    public void setUseGestureToZoom(boolean useGestureToZoom) {
        mUseGestureToZoom = useGestureToZoom;
    }

    public int getGestureMovingFactor() {
        return mGestureMovingFactor;
    }

    public void setGestureMovingFactor(int gestureMovingFactor) {
        mGestureMovingFactor = gestureMovingFactor;
    }

    public void setDebugLog(boolean visible) {
        SmartCamLog.setIsDebug(visible);
    }
}
