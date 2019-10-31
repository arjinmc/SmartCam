package com.arjinmc.smartcam.core.callback;

import android.content.Context;
import android.view.OrientationEventListener;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

/**
 * Detect orientation of camera
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
public class SmartCamOrientationEventListener extends OrientationEventListener {

    private ICameraPreviewWrapper mCameraPreivewWrapper;

    public SmartCamOrientationEventListener(Context context, ICameraPreviewWrapper cameraPreviewWrapper) {
        super(context);
        mCameraPreivewWrapper = cameraPreviewWrapper;
    }

    @Override
    public void onOrientationChanged(int orientation) {
//        SmartCamLog.i("onOrientationChanged", orientation + "");
        if (mCameraPreivewWrapper != null) {
            mCameraPreivewWrapper.onOrientationChange(orientation);
        }
    }
}
