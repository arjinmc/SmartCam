package com.arjinmc.smartcam.core;

import android.content.Context;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Utils for SmartCam
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamUtils {

    public static int getWindowDisplayRotation(Context context) {

        if (context == null) {
            return -1;
        }
        int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (angle) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return -1;
        }
    }

    public static int getShouldRotateDegree(Context context, int angle) {
        if (context == null) {
            return 0;
        }

        if (angle == -1) {
            angle = getWindowDisplayRotation(context);
        }

        switch (angle) {
            case 0:
                return 90;
            case 90:
                return 0;
            case 180:
                return 270;
            case 270:
                return 180;
            default:
                return 0;
        }
    }

}

