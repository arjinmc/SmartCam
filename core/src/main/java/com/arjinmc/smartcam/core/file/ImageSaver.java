package com.arjinmc.smartcam.core.file;

import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureResult;

/**
 * Created by Eminem Lo on 22/9/2020.
 * email: arjinmc@hotmail.com
 */
public class ImageSaver extends Thread {

    private SmartCamCaptureResult mSmartCamCaptureResult;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImageSaver(SmartCamCaptureResult smartCamCaptureResult, SmartCamCaptureCallback smartCamCaptureCallback) {
        mSmartCamCaptureResult = smartCamCaptureResult;
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    @Override
    public void run() {

        if (mSmartCamCaptureCallback != null) {
            mSmartCamCaptureCallback.onSuccess(new SmartCamCaptureResult(
                    mSmartCamCaptureResult.getData(), mSmartCamCaptureResult.getCameraVersion()
                    , mSmartCamCaptureResult.getOrientation()
                    , mSmartCamCaptureResult.isNeedReverse()
                    , mSmartCamCaptureResult.getPreviewWidth()
                    , mSmartCamCaptureResult.getPreviewHeight()
                    , mSmartCamCaptureResult.getRatio()));
        }
    }
}
