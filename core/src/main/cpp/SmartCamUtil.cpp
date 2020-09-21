//
// Created by arjinmc on 21/9/2020.
//
#include <jni.h>
#include <cstring>
#include <unistd.h>
#include <android/Bitmap.h>
#include <android/log.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_ERROR,"SmartCam C",__VA_ARGS__)

/**
 * get should rotate degress for camera 1
 * @param cameraType
 * @param degree
 * @return
 */
int getShouldRotateDegreeForCamera1(int cameraType, int degree) {

    int resultDegree = 0;
    //back camera
    if (cameraType == 0) {

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
        //front camera
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
 *
 * get should rotate degress for camera 2
 * @param cameraType
 * @param degree
 * @return
 */
int getShouldRotateDegreeForCamera2(int cameraType, int degree) {

    int resultDegree = 0;

    //back camera
    if (cameraType == 0) {

        if (degree >= 0 && degree <= 44) {
            resultDegree =  0;
        }

        if (degree >= 315 && degree <= 360) {
            resultDegree =0;
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

extern "C"
JNIEXPORT jobject JNICALL
Java_com_arjinmc_smartcam_core_SmartCamUtils_rotateBitmap(JNIEnv *env, jclass clazz,
                                                          jint cameraVersion, jobject bitmap,
                                                          jint degree, jint cameraType) {

    int shouldRotateDegree;
    if (cameraVersion == 2) {
        shouldRotateDegree = getShouldRotateDegreeForCamera1(cameraType, degree);
    } else {
        shouldRotateDegree= getShouldRotateDegreeForCamera1(cameraType, degree);
    }
//    LOGD("shouldRotateDegree:%d", shouldRotateDegree);
    if (shouldRotateDegree == 0 && cameraType == 0) {
        return bitmap;
    }
    AndroidBitmapInfo bitmapInfo;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0) {
        return NULL;
    }

    void *bitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0) {
        return NULL;
    }

    uint32_t *src = (uint32_t *) bitmapPixels;
    uint32_t *tempPixels;
    if (shouldRotateDegree == 90 || shouldRotateDegree == -90) {
        tempPixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];
    } else {
        tempPixels = new uint32_t[bitmapInfo.width * bitmapInfo.height];
    }
    int stride = bitmapInfo.stride;
    int pixelsCount = bitmapInfo.height * bitmapInfo.width;
    memcpy(tempPixels, src, sizeof(uint32_t) * pixelsCount);
    AndroidBitmap_unlockPixels(env, bitmap);

    jclass bitmapCls = env->GetObjectClass(bitmap);
    jmethodID recycleFunction = env->GetMethodID(bitmapCls, "recycle", "()V");
    if (recycleFunction == 0) {
        return NULL;
    }
    env->CallVoidMethod(bitmap, recycleFunction);
    //
    //creating a new bitmap to put the pixels into it - using Bitmap Bitmap.createBitmap (int width, int height, Bitmap.Config config) :
    //
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap",
                                                            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf",
                                                                   "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass,
                                                       valueOfBitmapConfigFunction, configName);
    jobject newBitmap;
    if (shouldRotateDegree == 90 || shouldRotateDegree == -90) {
        newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction,
                                                bitmapInfo.height,
                                                bitmapInfo.width, bitmapConfig);
    } else {
        newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction,
                                                bitmapInfo.width,
                                                bitmapInfo.height, bitmapConfig);
    }
    //
    // putting the pixels into the new bitmap:
    //
    if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0) {
        return NULL;
    }
    uint32_t *newBitmapPixels = (uint32_t *) bitmapPixels;

    if (shouldRotateDegree == 90) {
        int whereToPut = 0;
        for (int x = 0; x <= bitmapInfo.width - 1; ++x) {
            for (int y = bitmapInfo.height - 1; y >= 0; --y) {
                uint32_t pixel = tempPixels[bitmapInfo.width * y + x];
                newBitmapPixels[whereToPut++] = pixel;
            }
        }
    } else if (shouldRotateDegree == 180) {
        int whereToPut = 0;
        for (int x = bitmapInfo.height - 1; x >= 0; --x) {
            for (int y = bitmapInfo.width - 1; y >= 0; --y) {
                uint32_t pixel = tempPixels[bitmapInfo.width * x + y];
                newBitmapPixels[whereToPut++] = pixel;
            }
        }
    } else if (shouldRotateDegree == -90) {
        int whereToPut = 0;
        for (int x = bitmapInfo.width - 1; x >= 0; --x) {
            for (int y = 0; y < bitmapInfo.height; ++y) {
                uint32_t pixel = tempPixels[bitmapInfo.width * y + x];
                newBitmapPixels[whereToPut++] = pixel;
            }
        }
    } else {
        int whereToPut = 0;
        for (int x = 0; x < bitmapInfo.height; ++x) {
            for (int y = 0; y < bitmapInfo.width; ++y) {
                uint32_t pixel = tempPixels[bitmapInfo.width * x + y];
                newBitmapPixels[whereToPut++] = pixel;
            }
        }
    }

    //if it front camera need reverse
//    if (cameraType == 1) {
//        int whereToPut = 0;
//
//        uint32_t *reversePixels;
//        if (shouldRotateDegree == 90 || shouldRotateDegree == -90) {
//            reversePixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];
//        } else {
//            reversePixels = new uint32_t[bitmapInfo.width * bitmapInfo.height];
//        }
//
//        for (int x = 0; x < bitmapInfo.height; ++x) {
//            for (int y = bitmapInfo.width - 1; y >= 0; --y) {
//                uint32_t pixel = reversePixels[bitmapInfo.width * x + y];
//                newBitmapPixels[whereToPut++] = pixel;
//            }
//        }
//    }

    AndroidBitmap_unlockPixels(env, newBitmap);
    //
    // freeing the native memory used to store the pixels
    //
    delete[] tempPixels;
    return newBitmap;
}
