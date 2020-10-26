package com.arjinmc.smartcam;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference Manager
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamSPManager {

    private static final String SP_TABLE = "smartcam";
    private final String SP_RATIO = "preview_ratio";

    private static SmartCamSPManager mSmartCamSPManager;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public static synchronized SmartCamSPManager getInstance(Context context) {

        if (mSmartCamSPManager == null) {
            mSmartCamSPManager = new SmartCamSPManager();
            mSharedPreferences = context.getSharedPreferences(SP_TABLE, Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }
        return mSmartCamSPManager;
    }

    public void setRatio(String ratio) {
        mEditor.putString(SP_RATIO, ratio);
        mEditor.apply();
    }

    public String getRatio() {
        return mSharedPreferences.getString(SP_RATIO, SmartCamUIConstants.RATIO_FIX_WINDOW);
    }

}
