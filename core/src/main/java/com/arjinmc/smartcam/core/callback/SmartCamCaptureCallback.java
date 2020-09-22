package com.arjinmc.smartcam.core.callback;

import com.arjinmc.smartcam.core.model.SmartCamCaptureResult;
import com.arjinmc.smartcam.core.model.SmartCamError;

/**
 * capture callback
 * Created by Eminem Lo on 2019-10-30.
 * email: arjinmc@hotmail.com
 */
public interface SmartCamCaptureCallback {

    /**
     * return capture result
     *
     * @param smartCamCaptureResult
     */
    void onSuccess(SmartCamCaptureResult smartCamCaptureResult);

    void onError(SmartCamError smartCamError);
}
