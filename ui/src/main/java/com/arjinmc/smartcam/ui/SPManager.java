package com.arjinmc.smartcam.ui;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference Manager
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public final class SPManager {

    private final String SP_TABLE = "smartcam";
    private final String SP_RATIO = "preview_ratio";

    private static SPManager mSPManager;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public synchronized SPManager getInstance(Context context) {

        if (mSPManager == null) {
            mSPManager = new SPManager();
            mSharedPreferences = context.getSharedPreferences(SP_TABLE, Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }
        return mSPManager;
    }

//    public void setRatio()


}
