package com.arjinmc.smartcam.core.file;

import android.media.Image;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ImageSaver implements Runnable {

    /**
     * The JPEG image
     */
    private Image mImage;
    private File mFile;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImageSaver(Image image, File file, SmartCamCaptureCallback smartCamCaptureCallback) {
        mImage = image;
        mFile = file;
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    public ImageSaver(Image image, String filePath, SmartCamCaptureCallback smartCamCaptureCallback) {
        mImage = image;
        if (!TextUtils.isEmpty(filePath)) {
            mFile = new File(filePath);
        }
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    @Override
    public void run() {
        if (mFile == null || !mFile.exists()) {
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError());
            }
            mImage.close();
            return;
        }

        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(bytes);
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onSuccessPath(mFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError(e.getMessage()));
            }
        } finally {
            mImage.close();
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
