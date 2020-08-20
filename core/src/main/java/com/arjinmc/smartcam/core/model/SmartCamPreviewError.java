package com.arjinmc.smartcam.core.model;

/**
 * SmartCam preview error
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public class SmartCamPreviewError extends SmartCamError {

    public SmartCamPreviewError() {
        super(SmartCamErrorCode.CAMERA_PREVIEW_ERROR, "Camera error cannot preview");
    }

    public SmartCamPreviewError(String message) {
        super(SmartCamErrorCode.CAMERA_PREVIEW_ERROR, message);
    }
}
