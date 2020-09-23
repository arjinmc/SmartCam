package com.arjinmc.smartcam.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.arjinmc.smartcam.ui.activity.SmartCamComplexActivity;
import com.arjinmc.smartcam.ui.activity.SmartCamSimpleActivity;

/**
 * use manager to switch different ui
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamUIManager {

    /**
     * start camera view with simple ui
     */
    public static void startSimpleUI(Context context) {
        startActivity(context, SmartCamSimpleActivity.class);
    }

    /**
     * start camera view with complex ui
     */
    public static void startComplexUI(Context context) {
        startActivity(context, SmartCamComplexActivity.class);
    }

    /**
     * start camera view with professional ui
     */
    public static void startProfessionalUI(Context context) {
        Toast.makeText(context, "This part has not done yet!", Toast.LENGTH_SHORT).show();
    }


    private static void startActivity(Context context, Class clz) {
        if (context == null || clz == null) {
            return;
        }
        context.startActivity(new Intent(context, clz));
    }
}
