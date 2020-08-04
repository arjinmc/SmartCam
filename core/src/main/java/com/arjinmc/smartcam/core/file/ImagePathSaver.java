package com.arjinmc.smartcam.core.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;
import com.arjinmc.smartcam.core.model.SmartCamOutputOption;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * save image from Image to File
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ImagePathSaver implements Runnable {

    private final String TAG = "ImagePathSaver";

    private SmartCamOutputOption mOutputOption;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImagePathSaver(SmartCamOutputOption outputOption, SmartCamCaptureCallback smartCamCaptureCallback) {
        mOutputOption = outputOption;
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    @Override
    public void run() {

        if (mOutputOption == null) {
            return;
        }

        SmartCamLog.i(TAG, "degree:" + mOutputOption.getDegree());

        if (mOutputOption.getFile() == null || !mOutputOption.getFile().exists()) {
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError());
            }
            mOutputOption.getImage().close();
            return;
        }

        ByteBuffer buffer = mOutputOption.getImage().getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {

            Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (mOutputOption.getMatrix() != null) {
                temp = SmartCamUtils.cropBitmap(temp, mOutputOption.getPreviewWidth(), mOutputOption.getPreviewHeight());
            }
            temp = SmartCamUtils.rotateBitmap(temp, mOutputOption.getDegree(),mOutputOption.getCameraType());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            temp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            output = new FileOutputStream(mOutputOption.getFile());
            output.write(data);

            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onSuccessPath(mOutputOption.getFile().getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError(e.getMessage()));
            }
        } finally {
            mOutputOption.getImage().close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
