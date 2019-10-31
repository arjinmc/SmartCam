package com.arjinmc.smartcam.core.callback;

import com.arjinmc.smartcam.core.model.SmartCamError;

/**
 * capture callback
 * Created by Eminem Lo on 2019-10-30.
 * email: arjinmc@hotmail.com
 */
public interface SmartCamCaptureCallback {

    void onSuccessPath(String path);

    void onSuccessUri(String uri);

    void onError(SmartCamError smartCamError);
}
