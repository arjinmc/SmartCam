package com.arjinmc.smartcam.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.smartcam.R;
import com.arjinmc.smartcam.core.SmartCam;
import com.arjinmc.smartcam.core.SmartCamPreview;
import com.arjinmc.smartcam.core.callback.SmartCamStateCallback;
import com.arjinmc.smartcam.core.model.SmartCamError;

/**
 * Use SmartCamPreveiw as a object
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
public class PreviewFromNewActivity extends AppCompatActivity {

    private SmartCam mSmartCam;
    private SmartCamPreview mSmartCamPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_from_new);

        mSmartCam = new SmartCam(this);
        mSmartCam.setStateCallback(new SmartCamStateCallback() {
            @Override
            public void onConnected() {
                mSmartCamPreview.setCamera(mSmartCam);
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onError(SmartCamError error) {

            }
        });

        FrameLayout preview = findViewById(R.id.camera_preview);
        mSmartCamPreview = new SmartCamPreview(this);
        mSmartCamPreview.setCamera(mSmartCam);
        preview.addView(mSmartCamPreview);

        mSmartCam.open();
//        mSmartCam.logFeatures();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSmartCam != null) {
            mSmartCam.pause();
        }
        if (mSmartCamPreview != null) {
            mSmartCamPreview.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSmartCam != null) {
            mSmartCam.resume();
        }
        if (mSmartCamPreview != null) {
            mSmartCamPreview.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSmartCam != null) {
            mSmartCam.release();
        }
        if (mSmartCamPreview != null) {
            mSmartCamPreview.release();
        }
    }
}
