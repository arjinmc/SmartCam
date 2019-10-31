package com.arjinmc.smartcam.core.file;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;

import java.io.File;

/**
 * save image from Image to File (below android Kitkat)
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
public class ImageFileSaver implements Runnable {

    private final String TAG = "ImagePathSaver";

    /**
     * The JPEG image
     */
    private byte[] mImage;
    private File mFile;
    private Integer mDegree;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImageFileSaver(byte[] image, Integer degree, File file, SmartCamCaptureCallback smartCamCaptureCallback) {
        mImage = image;
        mDegree = degree;
        mFile = file;
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    @Override
    public void run() {

        SmartCamLog.i(TAG, "degree:" + mDegree);

        if (mFile == null || !mFile.exists()) {
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError());
            }
            mImage = null;
            return;
        }

        boolean isSave = SmartCamFileUtils.saveFile(mImage, mFile);
        if (mSmartCamCaptureCallback != null) {
            if (isSave) {
                mSmartCamCaptureCallback.onSuccessPath(mFile.getAbsolutePath());
            } else {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError());
            }
        }
    }

}
