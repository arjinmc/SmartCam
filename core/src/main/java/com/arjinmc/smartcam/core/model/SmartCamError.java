package com.arjinmc.smartcam.core.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SmartCamError
 * Created by Eminem Lo on 2019-10-28.
 * email: arjinmc@hotmail.com
 */
public class SmartCamError extends Exception {

    private int code;
    private String message;

    public SmartCamError() {

    }

    public SmartCamError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    @Nullable
    @Override
    public String getLocalizedMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {
        return "code:" + code + ", message:" + message;
    }
}
