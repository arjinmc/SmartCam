package com.arjinmc.smartcam.core.callback;

import com.arjinmc.smartcam.core.model.SmartCamError;

/**
 * State for camera connection
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public interface SmartCamStateListener {

    void onConnected();

    void onDisconnected();

    void onError(SmartCamError error);
}
