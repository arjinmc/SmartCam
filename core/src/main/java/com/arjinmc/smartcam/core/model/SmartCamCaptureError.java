package com.arjinmc.smartcam.core.model;

/**
 * SmartCam open error
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public class SmartCamCaptureError extends SmartCamError {

    public SmartCamCaptureError() {
        super(SmartCamFileErrorCode.CAMERA_CAPTURE_FILE_NOT_EXIST, "File is not exist");
    }

    public SmartCamCaptureError(String message) {
        super(SmartCamFileErrorCode.CAMERA_CAPTURE_FILE_NOT_EXIST, message);
    }
}
