package com.arjinmc.smartcam.core.callback;

import com.arjinmc.smartcam.core.model.SmartCamError;

/**
 * capture callback
 * Created by Eminem Lo on 2019-10-30.
 * email: arjinmc@hotmail.com
 */
public interface SmartCamCaptureCallback {

    /**
     * below android Q
     * if you use {@link com.arjinmc.smartcam.core.SmartCam capture() or capturePath()} then you will get this callback
     *
     * @param path
     */
    void onSuccessPath(String path);

    /**
     * above android Q
     * if you use {@link com.arjinmc.smartcam.core.SmartCam captureUri()} then you will get this callback
     *
     * @param uri
     */
    void onSuccessUri(String uri);

    /**
     * return image data
     * @param data
     */
    void onSuccessData(byte[] data);

    void onError(SmartCamError smartCamError);
}
