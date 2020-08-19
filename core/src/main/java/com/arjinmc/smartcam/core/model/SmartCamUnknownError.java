package com.arjinmc.smartcam.core.model;

/**
 * SmartCam unknown error
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public class SmartCamUnknownError extends SmartCamError {

    public SmartCamUnknownError() {
        super(SmartCamFileErrorCode.CAMERA_UNKNOWN_ERROR, "Camera unknown error");
    }

    public SmartCamUnknownError(String message) {
        super(SmartCamFileErrorCode.CAMERA_CAPTURE_FILE_NOT_EXIST, message);
    }
}
