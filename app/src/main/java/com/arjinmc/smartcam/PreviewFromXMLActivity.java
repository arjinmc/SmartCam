package com.arjinmc.smartcam;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.smartcam.core.SmartCam;
import com.arjinmc.smartcam.core.SmartCamPreview;

/**
 * Use SmartCamPreveiw in xml
 * Created by Eminem Lo on 2019-10-15.
 * email: arjinmc@hotmail.com
 */
public class PreviewFromXMLActivity extends AppCompatActivity {

    private SmartCam mSmartCam;
    private SmartCamPreview mSmartCamPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_from_xml);

        mSmartCam = new SmartCam(this);
        mSmartCam.open();
        Log.i("tag", mSmartCam.getCameraCount() + "/" + mSmartCam.getOrientation());
        mSmartCam.logFeatures();

        mSmartCamPreview = findViewById(R.id.camera_preview);
        mSmartCamPreview.setCamera(mSmartCam);
    }
}
