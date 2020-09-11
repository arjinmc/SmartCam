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
    private final int DEFAULT_CAPTURE_QUALITY = 100;
    /**
     * Default auto dismiss manual focus view when camera orientation changed offset
     */
    private final int DEFAULT_MANUAL_FOCUS_DISMISS_DEGREE_OFFSET = 10;

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
     * make camera preview as auto-focus
     */
    private boolean mIsAutoFocus = true;

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

    public boolean isIsAutoFocus() {
        return mIsAutoFocus;
    }

    public void setAutoFocus(boolean mIsAutoFocus) {
        this.mIsAutoFocus = mIsAutoFocus;
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

    public void setDebugLog(boolean visible) {
        SmartCamLog.setIsDebug(visible);
    }
}
