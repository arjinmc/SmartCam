package com.arjinmc.smartcam.core;

/**
 * Created by Eminem Lo on 2019-10-29.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamConfig {

    /**
     * timeout duration for lock ,unit:TimeUnit.MILLISECONDS
     */
    public static final long LOCK_TIMEOUT_DURATION = 1000;

    /**
     * root dir path
     */
    private static String rootDirPath;

    /**
     * root dir for saving files (default name: SmartCam)
     */
    private static String rootDirName = "SmartCam";

    /**
     * auto reset to preview after capture
     */
    private static boolean autoReset = false;

    public static String getRootDirName() {
        return rootDirName;
    }

    /**
     * set dir name for saving files
     *
     * @param dirName
     */
    public static void setRootDir(String dirName) {
        rootDirName = dirName;
    }

    /**
     * get root dir path
     *
     * @return
     */
    public static String getRootDirPath() {
        return rootDirPath;
    }

    /**
     * set root dir path
     *
     * @param dirPath
     */
    public static void setRootDirPath(String dirPath) {
        rootDirPath = dirPath;
    }

    public static boolean isAutoReset() {
        return autoReset;
    }

    public static void setAutoReset(boolean autoReset) {
        SmartCamConfig.autoReset = autoReset;
    }

    public static void setDebugLog(boolean visible) {
        SmartCamLog.setIsDebug(visible);
    }
}
