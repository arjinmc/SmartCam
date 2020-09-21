package com.arjinmc.smartcam.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
     * rotate the bitmap to the right direction (camera1)
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap1(Bitmap bitmap, int degree, @CameraType.Type int type) {
        if (bitmap == null) {
            return null;
        }

        if (CameraType.CAMERA_BACK == type) {

            int resultDegree = 0;

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

            Matrix matrix = new Matrix();
            matrix.postRotate(resultDegree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0
                    , bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {

            int resultDegree = 0;

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
            Matrix matrix = new Matrix();
            matrix.postRotate(resultDegree);
            matrix.postScale(-1, 1);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0
                    , bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }


    /**
     * rotate the bitmap to the right direction (camera2)
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap2(Bitmap bitmap, int degree, @CameraType.Type int type) {
        if (bitmap == null) {
            return null;
        }

        if (CameraType.CAMERA_BACK == type) {

            if (degree >= 0 && degree <= 44) {
                return bitmap;
            }

            if (degree >= 315 && degree <= 360) {
                return bitmap;
            }

            int resultDegree = 0;

            if (degree >= 45 && degree <= 135) {
                resultDegree = 90;
            }
            if (degree >= 226 && degree <= 314) {
                resultDegree = -90;
            }
            if (degree >= 136 && degree <= 225) {
                resultDegree = 180;
            }

            //emulator
//            int resultDegree = 0;
//
//            if (degree >= 0 && degree <= 44) {
//                resultDegree = 90;
//            }
//
//            if (degree >= 315 && degree <= 360) {
//                resultDegree = 90;
//            }
//
//            if (degree >= 45 && degree <= 135) {
//                resultDegree = 180;
//            }
//            if (degree >= 226 && degree <= 314) {
//                resultDegree = 0;
//            }
//            if (degree >= 136 && degree <= 225) {
//                resultDegree = -90;
//            }

            Matrix matrix = new Matrix();
            matrix.postRotate(resultDegree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0
                    , bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {

            int resultDegree = 0;

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
            Matrix matrix = new Matrix();
            matrix.postRotate(resultDegree);
            matrix.postScale(-1, 1);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0
                    , bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

    /**
     * scale
     *
     * @param bitmap
     * @return
     */
    public static Bitmap postScaleFroFrontCamera(Bitmap bitmap, @CameraType.Type int type) {
        if (bitmap == null || type != CameraType.CAMERA_FRONT) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        return bitmap = Bitmap.createBitmap(bitmap, 0, 0
                , bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    public static final int YUV420P = 0;
    public static final int YUV420SP = 1;
    public static final int NV21 = 2;

    /***
     * 此方法內注釋以640*480為例
     * 未考慮CropRect的
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static byte[] getBytesFromImageAsType(Image image, int type) {
        try {
            //獲取源數據，如果是YUV格式的數據planes.length = 3
            //plane[i]里面的實際數據可能存在byte[].length <= capacity (緩沖區總大小)
            final Image.Plane[] planes = image.getPlanes();

            //數據有效寬度，一般的，圖片width <= rowStride，這也是導致byte[].length <= capacity的原因
            // 所以我們只取width部分
            int width = image.getWidth();
            int height = image.getHeight();

            //此處用來裝填最終的YUV數據，需要1.5倍的圖片大小，因為Y U V 比例為 4:1:1
            byte[] yuvBytes = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            //目標數組的裝填到的位置
            int dstIndex = 0;

            //臨時存儲uv數據的
            byte uBytes[] = new byte[width * height / 4];
            byte vBytes[] = new byte[width * height / 4];
            int uIndex = 0;
            int vIndex = 0;

            int pixelsStride, rowStride;
            for (int i = 0; i < planes.length; i++) {
                pixelsStride = planes[i].getPixelStride();
                rowStride = planes[i].getRowStride();

                ByteBuffer buffer = planes[i].getBuffer();

                //如果pixelsStride==2，一般的Y的buffer長度=640*480，UV的長度=640*480/2-1
                //源數據的索引，y的數據是byte中連續的，u的數據是v向左移以為生成的，兩者都是偶數位為有效數據
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);

                int srcIndex = 0;
                if (i == 0) {
                    //直接取出來所有Y的有效區域，也可以存儲成一個臨時的bytes，到下一步再copy
                    for (int j = 0; j < height; j++) {
                        System.arraycopy(bytes, srcIndex, yuvBytes, dstIndex, width);
                        srcIndex += rowStride;
                        dstIndex += width;
                    }
                } else if (i == 1) {
                    //根據pixelsStride取相應的數據
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            uBytes[uIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                } else if (i == 2) {
                    //根據pixelsStride取相應的數據
                    for (int j = 0; j < height / 2; j++) {
                        for (int k = 0; k < width / 2; k++) {
                            vBytes[vIndex++] = bytes[srcIndex];
                            srcIndex += pixelsStride;
                        }
                        if (pixelsStride == 2) {
                            srcIndex += rowStride - width;
                        } else if (pixelsStride == 1) {
                            srcIndex += rowStride - width / 2;
                        }
                    }
                }
            }

            //   image.close();

            //根據要求的結果類型進行填充
            switch (type) {
                case YUV420P:
                    System.arraycopy(uBytes, 0, yuvBytes, dstIndex, uBytes.length);
                    System.arraycopy(vBytes, 0, yuvBytes, dstIndex + uBytes.length, vBytes.length);
                    break;
                case YUV420SP:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = uBytes[i];
                        yuvBytes[dstIndex++] = vBytes[i];
                    }
                    break;
                case NV21:
                    for (int i = 0; i < vBytes.length; i++) {
                        yuvBytes[dstIndex++] = vBytes[i];
                        yuvBytes[dstIndex++] = uBytes[i];
                    }
                    break;
            }
            return yuvBytes;
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.i(TAG, e.toString());
        }
        return null;
    }

    /***
     * YUV420 轉化成 RGB
     */
    public static int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int rgb[] = new int[frameSize];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) {
                    y = 0;
                }
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) {
                    r = 0;
                } else if (r > 262143) {
                    r = 262143;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 262143) {
                    g = 262143;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 262143) {
                    b = 262143;
                }
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }
}

