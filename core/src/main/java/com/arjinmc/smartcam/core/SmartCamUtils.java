package com.arjinmc.smartcam.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.file.SmartCamFileUtils;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraType;
import com.arjinmc.smartcam.core.model.SmartCamCaptureResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utils for SmartCam
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public final class SmartCamUtils {

    private final static String TAG = "SmartCamUtils";

    static {
        System.loadLibrary("smartcam-core");
    }

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
     * check device if it has flash light
     *
     * @param context
     * @return
     */
    public static boolean hasFlashLight(Context context) {
        if (context == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR
                && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * check device if has auto focus feature
     *
     * @param context
     * @return
     */
    public static boolean hasAutoFocus(Context context) {
        if (context == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR
                && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
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

    public static int getShouldRotateDegree(@CameraType.Type int type, int orientation) {

        int rotation;
        if (type == CameraType.CAMERA_FRONT) {
            if (orientation >= 45 && orientation < 135) {
                rotation = 180;
            } else if (orientation >= 135 && orientation < 225) {
                rotation = 90;
            } else if (orientation >= 225 && orientation < 315) {
                rotation = 0;
            } else {
                rotation = 270;
            }
        } else {
            if (orientation >= 45 && orientation < 135) {
                rotation = 180;
            } else if (orientation >= 135 && orientation < 225) {
                rotation = 270;
            } else if (orientation >= 225 && orientation < 315) {
                rotation = 0;
            } else {
                rotation = 90;
            }
        }
        return rotation;
    }

    /**
     * convert size to CameraSupportPreviewSize
     *
     * @param sizes
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<CameraSize> convertSizes(Size[] sizes) {
        if (sizes == null || sizes.length <= 0) {
            return null;
        }
        List<CameraSize> cameraSizes = new ArrayList<>();
        int sizeLength = sizes.length;
        for (int i = 0; i < sizeLength; i++) {
            Size size = sizes[i];
            cameraSizes.add(new CameraSize(size.getWidth(), size.getHeight()));
//            SmartCamLog.e(TAG,"convertSizes:"+ size.getWidth() + "," + size.getHeight());
        }
        return cameraSizes;
    }

    /**
     * getBetterScale matrix to show preview better
     *
     * @param targetWidth
     * @param targetHeight
     * @param previewSize
     * @return
     */
    public static Matrix getBetterPreviewScaleMatrix(int targetWidth, int targetHeight, CameraSize previewSize) {
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
//        SmartCamLog.e(TAG,"scale:"+scale);
        matrix.postScale(scale, scale, centerX, centerY);
        return matrix;
    }

    /**
     * get photo orientation
     *
     * @param path
     * @return
     */
    public static int getPhotoOrientation(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        SmartCamLog.i(TAG, "getPictureDegree:" + degree);
        return degree;
    }

    /**
     * check if should reverse photo for capture
     *
     * @param cameraType
     * @return
     */
    public static boolean isShouldReverse(int cameraType) {
        if (cameraType == CameraType.CAMERA_FRONT) {
            return true;
        }
        return false;
    }

    /**
     * rotate the bitmap to the right direction (camera1)
     *
     * @param degree
     * @return
     */
    public static int getShouldRotateOrientationForCamera1(int degree, @CameraType.Type int type) {

        int resultDegree = 0;

        if (CameraType.CAMERA_BACK == type) {

            if (degree >= 0 && degree <= 44) {
                resultDegree = 90;
            }

            if (degree >= 315 && degree <= 360) {
                resultDegree = 90;
            }

            if (degree >= 45 && degree <= 135) {
                resultDegree = 180;
            }
            if (degree >= 226 && degree <= 314) {
                resultDegree = 0;
            }
            if (degree >= 136 && degree <= 225) {
                resultDegree = -90;
            }
        } else {

            if (degree >= 0 && degree <= 44) {
                resultDegree = -90;
            }

            if (degree >= 315 && degree <= 360) {
                resultDegree = -90;
            }

            if (degree >= 45 && degree <= 135) {
                resultDegree = 180;
            }
            if (degree >= 226 && degree <= 314) {
                resultDegree = 0;
            }
            if (degree >= 136 && degree <= 225) {
                resultDegree = 90;
            }
        }
        return resultDegree;
    }


    /**
     * rotate the bitmap to the right direction (camera2)
     *
     * @param degree
     * @return
     */
    public static int getShouldRotateOrientationForCamera2(int degree, @CameraType.Type int type) {

        int resultDegree = 0;

        if (CameraType.CAMERA_BACK == type) {

            if (degree >= 0 && degree <= 44) {
                resultDegree = 0;
            }

            if (degree >= 315 && degree <= 360) {
                resultDegree = 0;
            }

            if (degree >= 45 && degree <= 135) {
                resultDegree = 90;
            }
            if (degree >= 226 && degree <= 314) {
                resultDegree = -90;
            }
            if (degree >= 136 && degree <= 225) {
                resultDegree = 180;
            }

        } else {

            if (degree >= 0 && degree <= 44) {
                resultDegree = 180;
            }

            if (degree >= 315 && degree <= 360) {
                resultDegree = 180;
            }

            if (degree >= 45 && degree <= 135) {
                resultDegree = 90;
            }
            if (degree >= 226 && degree <= 314) {
                resultDegree = -90;
            }
            if (degree >= 136 && degree <= 225) {
                resultDegree = 0;
            }
        }

        return resultDegree;
    }


    /**
     * crop bitmap
     *
     * @param bitmap
     * @param previewWidth
     * @param previewHeight
     * @return
     */
    public static Bitmap cropBitmap1(Bitmap bitmap, int previewWidth, int previewHeight) {
        if (bitmap == null || bitmap.getByteCount() == 0 || previewWidth == 0 || previewHeight == 0) {
            return bitmap;
        }

        SmartCamLog.i(TAG, "bitmap:" + bitmap.getWidth() + ",height:" + bitmap.getHeight()
                + ",previewWidth:" + previewWidth + ",previewHeight:" + previewHeight);

        if (previewWidth <= bitmap.getWidth() && previewHeight <= bitmap.getHeight()) {

            float scale = Math.min(
                    (float) bitmap.getWidth() / previewWidth,
                    (float) bitmap.getHeight() / previewHeight);
            previewWidth *= scale;
            previewHeight *= scale;

            int alterTop = (bitmap.getHeight() - previewHeight) / 2;
            int alterLeft = (bitmap.getWidth() - previewWidth) / 2;
            SmartCamLog.i(TAG, "alterTop:" + alterTop + ",alterLeft:" + alterLeft
                    + ",previewWidth:" + previewWidth + ",previewHeight:" + previewHeight);

            return Bitmap.createBitmap(bitmap, alterLeft, alterTop, previewWidth, previewHeight);

        } else {
            float scale = Math.max(
                    (float) previewWidth / bitmap.getWidth(),
                    (float) previewHeight / bitmap.getHeight());
            SmartCamLog.i(TAG, "scale:" + scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), false);
            return bitmap;
        }
    }

    /**
     * crop bitmap
     *
     * @param bitmap
     * @param previewWidth
     * @param previewHeight
     * @return
     */
    public static Bitmap cropBitmap2(Bitmap bitmap, int previewWidth, int previewHeight) {
        if (bitmap == null || bitmap.getByteCount() == 0 || previewWidth == 0 || previewHeight == 0) {
            return bitmap;
        }

        SmartCamLog.i(TAG, "bitmap:" + bitmap.getWidth() + ",height:" + bitmap.getHeight()
                + ",previewWidth:" + previewWidth + ",previewHeight:" + previewHeight);

        if (bitmap.getWidth() > bitmap.getHeight()) {
            int temp = previewWidth;
            previewWidth = previewHeight;
            previewHeight = temp;
        }

        if (previewWidth <= bitmap.getWidth() && previewHeight <= bitmap.getHeight()) {

            float scale = Math.min(
                    (float) bitmap.getWidth() / previewWidth,
                    (float) bitmap.getHeight() / previewHeight);
            previewWidth *= scale;
            previewHeight *= scale;

            int alterTop = (bitmap.getHeight() - previewHeight) / 2;
            int alterLeft = (bitmap.getWidth() - previewWidth) / 2;
            SmartCamLog.i(TAG, "alterTop:" + alterTop + ",alterLeft:" + alterLeft
                    + ",previewWidth:" + previewWidth + ",previewHeight:" + previewHeight);

            return Bitmap.createBitmap(bitmap, alterLeft, alterTop, previewWidth, previewHeight);

        } else {
            float scale = Math.max(
                    (float) previewWidth / bitmap.getWidth(),
                    (float) previewHeight / bitmap.getHeight());
            SmartCamLog.i(TAG, "scale:" + scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), false);
            return bitmap;
        }
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

        SmartCamLog.i(TAG, "bitmap:" + bitmap.getWidth() + ",height:" + bitmap.getHeight()
                + ",previewWidth:" + previewWidth + ",previewHeight:" + previewHeight);

        if (bitmap.getWidth() > bitmap.getHeight()) {
            int temp = previewWidth;
            previewWidth = previewHeight;
            previewHeight = temp;
        }

        if (previewWidth <= bitmap.getWidth() && previewHeight <= bitmap.getHeight()) {

            float scale = Math.min(
                    (float) bitmap.getWidth() / previewWidth,
                    (float) bitmap.getHeight() / previewHeight);
            previewWidth *= scale;
            previewHeight *= scale;

            int alterTop = (bitmap.getHeight() - previewHeight) / 2;
            int alterLeft = (bitmap.getWidth() - previewWidth) / 2;
            SmartCamLog.i(TAG, "alterTop:" + alterTop + ",alterLeft:" + alterLeft
                    + ",previewWidth:" + previewWidth + ",previewHeight:" + previewHeight);

            return Bitmap.createBitmap(bitmap, alterLeft, alterTop, previewWidth, previewHeight);

        } else {
            float scale = Math.max(
                    (float) previewWidth / bitmap.getWidth(),
                    (float) previewHeight / bitmap.getHeight());
            SmartCamLog.i(TAG, "scale:" + scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale), false);
            return bitmap;
        }
    }

    /**
     * reverse image for horizontal orientation for front camerea
     *
     * @param bitmap
     * @return
     */
    public static native Bitmap reverseHorizontal(Bitmap bitmap);

    /**
     * use native to rotate bitmap
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static native Bitmap rotateBitmap(Bitmap bitmap, int degree);

    /**
     * deal bitmap after capture
     *
     * @param bitmap
     * @param captureResult
     * @return
     */
    public static Bitmap dealAfterCapture(Bitmap bitmap, SmartCamCaptureResult captureResult) {
        if (bitmap == null || captureResult == null) {
            return bitmap;
        }
        //1. Cut the rest that not preview
        bitmap = cropBitmap(bitmap, captureResult.getPreviewWidth(), captureResult.getPreviewHeight());
        //2. Rotate the right orientation if it need
        bitmap = rotateBitmap(bitmap, captureResult.getOrientation());
        //3. If need to reverse image
        if (captureResult.isNeedReverse()) {
            bitmap = reverseHorizontal(bitmap);
        }
        return bitmap;
    }

    /**
     * deal after capture and save jpeg file
     *
     * @param captureResult
     * @param file
     * @return
     */
    public static boolean dealAndSaveJpegFile(SmartCamCaptureResult captureResult, File file) {
        SmartCamLog.i(TAG, "dealAndSaveJpegFile");
        if (captureResult == null || captureResult.getData() == null || file == null || !file.exists()) {
            return false;
        }
        Bitmap temp = BitmapFactory.decodeByteArray(captureResult.getData(), 0, captureResult.getData().length);
        temp = dealAfterCapture(temp, captureResult);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            temp.compress(Bitmap.CompressFormat.JPEG, SmartCamConfig.getInstance().getOutputQuality()
                    , byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            return SmartCamFileUtils.saveFile(data, file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * check if it's a image file
     *
     * @param file
     * @return
     */
    public static boolean isImageFile(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        return isImageFile(file.getAbsolutePath());
    }

    /**
     * check if it's a image file
     *
     * @param filePath
     * @return
     */
    public static boolean isImageFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return isEditableImageFile(filePath);
    }


    /**
     * check if it's a image file
     *
     * @param filePath
     * @return
     */
    public static boolean isEditableImageFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        String fileType = getFileTypeValue(filePath);
        if (TextUtils.isEmpty(fileType)) {
            return false;
        } else {
            HashMap<String, String> fileTypes = getEditableFileTypeList();
            if (fileTypes.containsKey(fileType)) {
                return true;
            }
            return false;
        }
    }

    /**
     * get
     *
     * @return
     */
    private static HashMap<String, String> getEditableFileTypeList() {
        HashMap<String, String> fileTypes = new HashMap<>(5);
        fileTypes.put("FFD8FF", "jpg");
        fileTypes.put("89504E47", "png");
        fileTypes.put("47494638", "gif");
        fileTypes.put("49492A00", "tif");
        fileTypes.put("424D", "bmp");
        return fileTypes;
    }

    /**
     * get file header value
     *
     * @param filePath
     * @return
     */
    private static String getFileTypeValue(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * bytes convert to Hex string
     *
     * @param src
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}

