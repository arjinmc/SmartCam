package com.arjinmc.smartcam.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.model.CameraSupportPreviewSize;
import com.arjinmc.smartcam.core.model.CameraType;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils for SmartCam
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamUtils {

    private final static String TAG = "SmartCamUtils";

    /**
     * check device if it has camera
     *
     * @param context
     * @return
     */
    public static boolean hasCamera(Context context) {
        if (context == null) {
            return false;
        }
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否有闪光灯
     *
     * @param context
     * @return
     */
    public static boolean hasFlashLight(Context context) {
        if (context == null) {
            return false;
        }
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get window display rotation
     *
     * @param context
     * @return
     */
    public static int getWindowDisplayRotation(Context context) {

        if (context == null) {
            return -1;
        }
        int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (angle) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return -1;
        }
    }

    /**
     * get window display surface rotation like Surface.ROTATION_270
     *
     * @param context
     * @return
     */
    public static int getWindowSurfaceRotation(Context context) {

        if (context == null) {
            return -1;
        }
        int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        return angle;
    }

    /**
     * get should rotate degree
     *
     * @param context
     * @param type
     * @param cameraId
     * @param degrees
     * @return
     */
    public static int getShouldRotateDegree(Context context, @CameraType.Type int type, String cameraId, int degrees) {
        if (context == null) {
            return 0;
        }

        if (degrees == -1) {
            degrees = getWindowDisplayRotation(context);
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Integer.valueOf(cameraId), info);
        int result;
        if (type == CameraType.CAMERA_FRONT) {
            result = (info.orientation + degrees) % 360;
            // compensate the mirror
            result = (360 - result) % 360;
            // back-facing
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static int[] calculateBetterPreviewSize(int supportSizeWidth, int supportSizeHeight, int previewWidth, int previewHeight) {

        int[] compatSize = new int[2];
        if (supportSizeWidth > supportSizeHeight) {
            int temp = supportSizeHeight;
            supportSizeHeight = supportSizeWidth;
            supportSizeWidth = temp;
        }
        double ratioWidth = supportSizeWidth / (double) previewWidth;
        double ratioHeight = supportSizeHeight / (double) previewHeight;
        SmartCamLog.e(TAG, "ratioWidth:" + ratioWidth + ",ratioHeight:" + ratioHeight);
        int ratio;
        if (ratioWidth > ratioHeight) {
            ratio = Double.valueOf(ratioWidth > 1 ? ratioWidth : ratioWidth * 100).intValue();
        } else {
            ratio = Double.valueOf(ratioHeight > 1 ? ratioHeight : ratioHeight * 100).intValue();
        }
        compatSize[0] = supportSizeWidth * ratio;
        compatSize[1] = supportSizeHeight * ratio;
        SmartCamLog.e(TAG, "ratio:" + ratio);
        SmartCamLog.e(TAG, "view size:" + previewWidth + "，" + previewHeight);
        SmartCamLog.e(TAG, "calculateBetterPreviewSize:" + compatSize[0] + "," + compatSize[1]);
        return compatSize;
    }

    /**
     * convert size to CameraSupportPreviewSize
     *
     * @param sizes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<CameraSupportPreviewSize> convertSizes(Size[] sizes) {
        if (sizes == null || sizes.length <= 0) {
            return null;
        }
        List<CameraSupportPreviewSize> cameraSupportPreviewSizes = new ArrayList<>();
        int sizeLength = sizes.length;
        for (int i = 0; i < sizeLength; i++) {
            Size size = sizes[i];
            cameraSupportPreviewSizes.add(new CameraSupportPreviewSize(size.getWidth(), size.getHeight()));
//            SmartCamLog.e("convertSizes", size.getWidth() + "," + size.getHeight());
        }
        return cameraSupportPreviewSizes;
    }

    /**
     * getBetterScale matrix to show preview better
     *
     * @param targetWidth
     * @param targetHeight
     * @param previewSize
     * @return
     */
    public static Matrix getBetterPreviewScaleMatrix(int targetWidth, int targetHeight, CameraSupportPreviewSize previewSize) {
        if (previewSize == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, targetWidth, targetHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
        matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
        float scale = Math.max(
                (float) targetHeight / previewSize.getWidth(),
                (float) targetWidth / previewSize.getHeight());
//        SmartCamLog.e("scale", scale + "");
        matrix.postScale(scale, scale, centerX, centerY);
        return matrix;
    }

    /**
     * crop bitmap
     *
     * @param bitmap
     * @param previewWidth
     * @param previewHeight
     * @return
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int previewWidth, int previewHeight) {
        if (bitmap == null || bitmap.getByteCount() == 0 || previewWidth == 0 || previewHeight == 0) {
            return bitmap;
        }

        boolean needCrop = false;
        if (previewWidth <= bitmap.getWidth() && previewHeight <= bitmap.getHeight()) {
            float scale = Math.min(
                    (float) bitmap.getWidth() / previewWidth,
                    (float) bitmap.getHeight() / previewHeight);
            previewWidth *= scale;
            previewHeight *= scale;
            needCrop = true;
        }

        if (!needCrop) {
            return bitmap;
        } else {
            int alterTop = (bitmap.getHeight() - previewHeight) / 2;
            int alterLeft = (bitmap.getWidth() - previewWidth) / 2;
            Log.e("tag", "alterTop:" + alterTop + ",alterLeft:" + alterLeft
                    + ",previewWidth:" + previewWidth + ",previewHeight:" + previewHeight);
            return Bitmap.createBitmap(bitmap, alterLeft, alterTop, previewWidth, previewHeight);
        }
    }

    /**
     * get scale ratio
     *
     * @param width
     * @param height
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static float getScaleRatio(int width, int height, int targetWidth, int targetHeight) {
        float scale = Math.max(
                (float) targetHeight / height,
                (float) targetWidth / width);
        return scale;
    }
}

