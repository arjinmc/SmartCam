package com.arjinmc.smartcam.core.model;

/**
 * SmartCam open error
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public class SmartCamOpenError extends SmartCamError {

    public SmartCamOpenError() {
        super(SmartCamFileErrorCode.CAMERA_OPEN_ERROR, "Camera Open Error");
    }

    public SmartCamOpenError(String message) {
        super(SmartCamFileErrorCode.CAMERA_OPEN_ERROR, message);
    }
}
