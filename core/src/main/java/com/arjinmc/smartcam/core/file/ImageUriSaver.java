package com.arjinmc.smartcam.core.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;
import com.arjinmc.smartcam.core.model.SmartCamOutputOption2;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * save image from Image to Uri
 * Created by Eminem Lo on 2019-10-31.
 * email: arjinmc@hotmail.com
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ImageUriSaver implements Runnable {

    private final String TAG = "ImagePathSaver";

    /**
     * The JPEG image
     */
    private Context mContext;
    private SmartCamOutputOption2 mOutputOption;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImageUriSaver(Context context, SmartCamOutputOption2 outputOption, SmartCamCaptureCallback smartCamCaptureCallback) {
        mContext = context;
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

        if (TextUtils.isEmpty(mOutputOption.getUri())) {
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
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {

            Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            if (mOutputOption.getMatrix() != null) {
                temp = SmartCamUtils.cropBitmap2(temp, mOutputOption.getPreviewWidth(), mOutputOption.getPreviewHeight());
            }

//            if (SmartCamCompatUtils.isXiaomi8()) {
//                temp = SmartCamUtils.rotateBitmap1(temp, mOutputOption.getDegree(), mOutputOption.getCameraType());
//            } else {
//                temp = SmartCamUtils.rotateBitmap2(temp, mOutputOption.getDegree(), mOutputOption.getCameraType());
//            }

            temp = SmartCamUtils.postScaleFroFrontCamera(temp, mOutputOption.getCameraType());

            byteArrayOutputStream = new ByteArrayOutputStream();
            temp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(Uri.parse(mOutputOption.getUri()), "w");
            output = new FileOutputStream(pfd.getFileDescriptor());
            output.write(data);
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onSuccessUri(mOutputOption.getUri());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onError(new SmartCamCaptureError(e.getMessage()));
            }
        } finally {
            mOutputOption.getImage().close();
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
