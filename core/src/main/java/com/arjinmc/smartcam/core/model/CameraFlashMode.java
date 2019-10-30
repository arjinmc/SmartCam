package com.arjinmc.smartcam.core.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Camera Flash Mode
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public final class CameraFlashMode implements Serializable {

    public static final int MODE_UNKNOWN = -1;
    public static final int MODE_OFF = 0;
    public static final int MODE_AUTO = 1;
    public static final int MODE_ON = 2;
    public static final int MODE_RED_EYE = 3;
    public static final int MODE_TORCH = 4;

    public static final String FLASH_MODE_OFF = "off";
    public static final String FLASH_MODE_AUTO = "auto";
    public static final String FLASH_MODE_ON = "on";
    public static final String FLASH_MODE_RED_EYE = "red-eye";
    public static final String FLASH_MODE_TORCH = "torch";

    public static final int getMode(String mode) {
        if (TextUtils.isEmpty(mode)) {
            return MODE_UNKNOWN;
        }
        switch (mode) {
            case FLASH_MODE_OFF:
                return MODE_OFF;
            case FLASH_MODE_AUTO:
                return MODE_AUTO;
            case FLASH_MODE_ON:
                return MODE_ON;
            case FLASH_MODE_RED_EYE:
                return MODE_RED_EYE;
            case FLASH_MODE_TORCH:
                return MODE_TORCH;
            default:
                return MODE_UNKNOWN;
        }
    }

}
