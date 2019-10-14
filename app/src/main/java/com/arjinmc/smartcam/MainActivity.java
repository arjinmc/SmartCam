package com.arjinmc.smartcam;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.smartcam.core.SmartCam;
import com.arjinmc.smartcam.core.SmartCamPreview;
import com.arjinmc.smartcam.permission.PermissionAssistant;

public class MainActivity extends AppCompatActivity {

    private String[] mPermissions = new String[]{Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SmartCam smartCam = new SmartCam();
        smartCam.open();
        Log.i("tag", smartCam.getCameraCount() + "/" + smartCam.getOrientation());
        smartCam.logFeatures();

        // Create our Preview view and set it as the content of our activity.
        SmartCamPreview smartCamPreview = new SmartCamPreview(this, smartCam);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(smartCamPreview);
//        SmartCamPreview smartCamPreview = findViewById(R.id.camera_preview);
//        smartCamPreview.setCamera(smartCam);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionAssistant.isGrantedAllPermissions(this, mPermissions)) {
            PermissionAssistant.requestPermissions(this, mPermissions, false);
        }
    }
}
