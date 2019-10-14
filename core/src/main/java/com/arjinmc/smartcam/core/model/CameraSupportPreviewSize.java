package com.arjinmc.smartcam.core.model;

import java.io.Serializable;

/**
 * thie size that camera support preview
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class CameraSupportPreviewSize implements Serializable {

    private int width;
    private int height;

    public CameraSupportPreviewSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
