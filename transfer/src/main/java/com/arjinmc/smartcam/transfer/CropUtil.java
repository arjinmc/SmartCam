package com.arjinmc.smartcam.transfer;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.arjinmc.smartcam.core.SmartCamUtils;

import java.io.File;

/**
 * Crop image
 * Created by Eminem Lo on 11/8/2020.
 * email: arjinmc@hotmail.com
 */
public final class CropUtil {

    /**
     * Check image is need to crop
     *
     * @param bitmap
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static boolean isNeedCrop(Bitmap bitmap, int targetWidth, int targetHeight) {
        if (bitmap == null || !isGoodSize(bitmap.getWidth(), bitmap.getHeight())
                || !isGoodSize(targetWidth, targetHeight)) {
            return false;
        }
        if (bitmap.getWidth() < targetWidth || bitmap.getHeight() < targetHeight) {
            return false;
        }
        return true;
    }

    /**
     * Check image is need to crop
     *
     * @param file
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static boolean isNeedCrop(File file, int targetWidth, int targetHeight) {
        if (file == null) {
            return false;
        }
        if (!isGoodSize(targetWidth, targetHeight) || !SmartCamUtils.isImageFile(file)) {
            return false;
        }
        return true;
    }

    /**
     * Check image is need to crop
     *
     * @param filePath
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static boolean isNeedCrop(String filePath, int targetWidth, int targetHeight) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        if (!isGoodSize(targetWidth, targetHeight) || !SmartCamUtils.isImageFile(filePath)) {
            return false;
        }
        return true;
    }

    public static native void crop(byte[] imageData, int targetWidth, int targetHeight
            , OnTransferCallback onTransferCallback);


    /**
     * Check if the width and height of image both above zero.
     *
     * @param width
     * @param height
     * @return
     */
    private static boolean isGoodSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        return true;
    }


}
