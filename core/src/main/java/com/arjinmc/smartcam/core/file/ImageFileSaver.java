package com.arjinmc.smartcam.core.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arjinmc.smartcam.core.SmartCamConfig;
import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;
import com.arjinmc.smartcam.core.model.SmartCamOutputOption1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * save image from Image to File (below android Kitkat)
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
public class ImageFileSaver implements Runnable {

    private final String TAG = "ImageFileSaver";

    private SmartCamOutputOption1 mOutputOption;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImageFileSaver(SmartCamOutputOption1 outputOption, SmartCamCaptureCallback smartCamCaptureCallback) {

        mOutputOption = outputOption;
        mSmartCamCaptureCallback = smartCamCaptureCallback;

    }

    @Override
    public void run() {

        if (mOutputOption == null) {
            return;
        }
        SmartCamLog.i(TAG, "degree:" + mOutputOption.getDegree());

        if (mOutputOption.getDegree() == null || mOutputOption.getDegree() == -1) {
            mOutputOption.setDegree(0);
        }

        if (mOutputOption.getFile() == null || !mOutputOption.getFile().exists()
                || mOutputOption.getImageData() == null) {
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError());
            }
            mOutputOption.setImageData(null);
            return;
        }

        Bitmap temp = BitmapFactory.decodeByteArray(mOutputOption.getImageData()
                , 0, mOutputOption.getImageData().length);
        temp = SmartCamUtils.cropBitmap1(temp, mOutputOption.getPreviewWidth(), mOutputOption.getPreviewHeight());
        temp = SmartCamUtils.rotateBitmap1(temp, mOutputOption.getDegree(), mOutputOption.getCameraType());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        temp.compress(Bitmap.CompressFormat.JPEG, SmartCamConfig.getInstance().getOutputQuality()
                , byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        boolean isSave = SmartCamFileUtils.saveFile(data, mOutputOption.getFile());
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        temp.recycle();
        mOutputOption.setImageData(null);
        if (mSmartCamCaptureCallback != null) {
            if (isSave) {
                mSmartCamCaptureCallback.onSuccessPath(mOutputOption.getFile().getAbsolutePath());
            } else {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError());
            }
        }
    }

}
