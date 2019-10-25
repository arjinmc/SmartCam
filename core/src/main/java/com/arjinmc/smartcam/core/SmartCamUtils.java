package com.arjinmc.smartcam.core;

import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import com.arjinmc.smartcam.core.model.CameraRotateType;
import com.arjinmc.smartcam.core.model.CameraType;

/**
 * Utils for SmartCam
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamUtils {

    private static final int OFFSET_DEGREE = 10;

    /**
     * get window display rotatrion
     *
     * @param context
     * @return
     */
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

    /**
     * get should rotate degree
     *
     * @param context
     * @param type
     * @param cameraId
     * @param degrees
     * @return
     */
    public static int getShouldRotateDegree(Context context, @CameraType.Type int type, int cameraId, int degrees) {
        if (context == null) {
            return 0;
        }

        if (degrees == -1) {
            degrees = getWindowDisplayRotation(context);
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (type == CameraType.CAMERA_FRONT) {
            result = (info.orientation + degrees) % 360;
            // compensate the mirror
            result = (360 - result) % 360;
            // back-facing
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * get should roate camera degree
     *
     * @param degree
     * @return
     */
    public static int getWindowDisplayShouldRotationDegree(int degree) {
        if (isShouldRotate(CameraRotateType.TYPE_0, degree)) {
            return CameraRotateType.TYPE_0;
        } else if (isShouldRotate(CameraRotateType.TYPE_90, degree)) {
            return CameraRotateType.TYPE_270;
        } else if (isShouldRotate(CameraRotateType.TYPE_270, degree)) {
            return CameraRotateType.TYPE_90;
        } else {
            return CameraRotateType.TYPE_UNKNOWN;
        }
    }

    /**
     * check if should rotate camera
     *
     * @param keyDegree
     * @param degree
     * @return
     */
    private static boolean isShouldRotate(int keyDegree, int degree) {
        return Math.abs(keyDegree - degree) <= OFFSET_DEGREE;
    }
}

