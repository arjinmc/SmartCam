package com.arjinmc.smartcam.core.model;

import java.io.Serializable;

/**
 * the camera size
 * for preview size / output size
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class CameraSize implements Serializable {

    private static final long serialVersionUID = -4574686753967297581L;

    private int width;
    private int height;

    public CameraSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
