//
// Created by arjinmc on 11/8/2020.
//

#include <jni.h>
#include <string>
#include <android/bitmap.h>
#include <android/log.h>
#include <bitset>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"print from C",__VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_arjinmc_smartcam_transfer_CropUtil_crop(JNIEnv *env, jclass clazz,
                                                 jbyteArray jImageData, jint targetWidth,
                                                 jint targetHeight,
                                                 jobject jOnTransferCallback) {

    if (jOnTransferCallback == NULL) {
        return;
    }

}