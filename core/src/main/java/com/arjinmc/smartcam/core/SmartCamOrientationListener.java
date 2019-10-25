package com.arjinmc.smartcam.core;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * OrientationListener for SmartCam
 * if you need camera rotates to fit phone auto rotate,use this Listener
 * Created by Eminem Lo on 2019-10-23.
 * email: arjinmc@hotmail.com
 */
public class SmartCamOrientationListener extends OrientationEventListener {

    private SmartCamPreview mSmartCamPreview;

    public SmartCamOrientationListener(Context context, SmartCamPreview smartCamPreview) {
        super(context);
        mSmartCamPreview = smartCamPreview;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (mSmartCamPreview != null) {
            mSmartCamPreview.onOrientationChanged(orientation);
        }
    }

}
