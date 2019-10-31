package com.arjinmc.smartcam.core.file;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.SmartCamLog;
import com.arjinmc.smartcam.core.callback.SmartCamCaptureCallback;
import com.arjinmc.smartcam.core.model.SmartCamCaptureError;

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
    private Image mImage;
    private String mUri;
    private Integer mDegree;
    private SmartCamCaptureCallback mSmartCamCaptureCallback;

    public ImageUriSaver(Context context,Image image, Integer degree, String uri, SmartCamCaptureCallback smartCamCaptureCallback) {
        mContext = context;
        mImage = image;
        mDegree = degree;
        mUri = uri;
        mSmartCamCaptureCallback = smartCamCaptureCallback;
    }

    @Override
    public void run() {

        SmartCamLog.i(TAG, "degree:" + mDegree);

        if (TextUtils.isEmpty(mUri)) {
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
            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(Uri.parse(mUri), "w");
            output = new FileOutputStream(pfd.getFileDescriptor());
            output.write(bytes);
            if (mSmartCamCaptureCallback != null) {
                mSmartCamCaptureCallback.onSuccessUri(mUri);
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
