package com.arjinmc.smartcam.core.model;

import androidx.annotation.IntDef;

import java.io.Serializable;

/**
 * Camera type null/back/front
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class CameraType{

    /**
     * camera not open
     */
    public static final int CAMERA_NULL = -1;
    /**
     * back camera
     */
    public static final int CAMERA_BACK = 0;
    /**
     * front camera
     */
    public static final int CAMERA_FRONT = 1;

    @IntDef(value = {CAMERA_BACK, CAMERA_FRONT})
    public @interface Type {
    }
}
