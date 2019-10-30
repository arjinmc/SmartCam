package com.arjinmc.smartcam.core;

import android.util.Log;

/**
 * Created by Eminem Lo on 2019-10-30.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamLog {

    private static boolean isDebug = false;

    public static void i(Class clz, String message) {
        if (isDebug) {
            Log.i(clz.getName(), message);
        }
    }

    public static void d(Class clz, String message) {
        if (isDebug) {
            Log.d(clz.getName(), message);
        }
    }

    public static void e(Class clz, String message) {
        if (isDebug) {
            Log.e(clz.getName(), message);
        }
    }

    public static void i(String tag, String message) {
        if (isDebug) {
            Log.i(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (isDebug) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug) {
            Log.e(tag, message);
        }
    }
}
