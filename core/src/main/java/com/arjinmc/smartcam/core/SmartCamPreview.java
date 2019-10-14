package com.arjinmc.smartcam.core;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.camera1.Camera1Preview;

/**
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class SmartCamPreview extends FrameLayout {

    public SmartCamPreview(@NonNull Context context) {
        super(context);
    }

    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SmartCamPreview(Context context, SmartCam smartCam) {
        super(context);
        init(smartCam);

    }

    public void setCamera(SmartCam smartCam) {
        init(smartCam);
    }

    private void init(SmartCam smartCam) {

        if (getChildCount() != 0) {
            removeAllViews();
        }

        //        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//
//        }else{
        Camera1Preview camera1Preview = new Camera1Preview(getContext(), (Camera) smartCam.getCamera());
        addView(camera1Preview);
//        }
    }
}
