package com.arjinmc.smartcam.core.camera2;

import android.content.Context;
import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.wrapper.ICameraPreviewWrapper;

/**
 * Preview for camera2
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
public class Camera2Preview extends TextureView implements ICameraPreviewWrapper {

    private CameraDevice mCamera;

    public Camera2Preview(Context context, CameraDevice cameraDevice) {
        super(context);
        mCamera = cameraDevice;
    }

    public Camera2Preview(Context context) {
        super(context);
    }

    public Camera2Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Camera2Preview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Camera2Preview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {

    }

    @Override
    public void destroy() {

    }
}
