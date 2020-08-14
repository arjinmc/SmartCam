package com.arjinmc.smartcam.core;

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
    private static final int DEFAULT_CAPTURE_QUALITY = 100;

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
        mCaptureQuality = captureQuality;
    }

    public void setDebugLog(boolean visible) {
        SmartCamLog.setIsDebug(visible);
    }
}
