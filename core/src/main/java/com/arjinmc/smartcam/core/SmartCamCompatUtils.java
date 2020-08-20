package com.arjinmc.smartcam.core;

import android.os.Build;
import android.text.TextUtils;

/**
 * CompatUtils for some special device model
 * Created by Eminem Lo on 20/8/2020.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamCompatUtils {

    private static String getDeviceModel() {
        return Build.MODEL;
    }

    private static String getDeviceBrand() {
        return Build.MANUFACTURER;
    }

    public static boolean isXiaomi8() {
        String brand = getDeviceBrand();
        String model = getDeviceModel();

        if (TextUtils.isEmpty(brand) || TextUtils.isEmpty(model)) {
            return false;
        }
        if ("XIAOMI".equals(brand.toUpperCase()) && "MI 8".equals(model.toUpperCase())) {
            return true;
        }
        return false;
    }
}
