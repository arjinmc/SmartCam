package com.arjinmc.smartcam.core.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * save image from Image to File (below android Kitkat)
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
public class ImageFileSaver implements Runnable {

    private final String TAG = "ImageFileSaver";

    /**
     * The JPEG image
     */
    private byte[] mImage;
    private File mFile;
    private Integer mDegree;
    private int mCameraType;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImageFileSaver(byte[] image, Integer degree, int cameraType, File file, SmartCamCaptureCallback smartCamCaptureCallback) {
        mImage = image;
        mDegree = degree;
        mCameraType = cameraType;
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

        Bitmap temp = BitmapFactory.decodeByteArray(mImage, 0, mImage.length);
//        if (mOutputOption.getMatrix() != null) {
//            temp = SmartCamUtils.cropBitmap(temp, mOutputOption.getPreviewWidth(), mOutputOption.getPreviewHeight());
//        }
        temp = SmartCamUtils.rotateBitmap1(temp, mDegree, mCameraType);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        temp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        boolean isSave = SmartCamFileUtils.saveFile(data, mFile);
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mSmartCamCaptureCallback != null) {
            if (isSave) {
                mSmartCamCaptureCallback.onSuccessPath(mFile.getAbsolutePath());
            } else {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError());
            }
        }
    }

}
