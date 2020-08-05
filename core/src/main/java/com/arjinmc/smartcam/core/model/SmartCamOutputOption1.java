package com.arjinmc.smartcam.core.model;

import java.io.File;
import java.io.Serializable;

/**
 * Output option for Camera1
 * Created by Eminem Lo on 5/8/2020.
 * email: arjinmc@hotmail.com
 */
public class SmartCamOutputOption1 implements Serializable {

    private static final long serialVersionUID = -1608316824364563709L;

    private byte[] imageData;
    private File file;
    private Integer degree;
    private int cameraType;
    private int previewWidth;
    private int previewHeight;

    public SmartCamOutputOption1(byte[] imageData, File file, Integer degree, int cameraType
            , int previewWidth, int previewHeight) {
        this.imageData = imageData;
        this.file = file;
        this.degree = degree;
        this.cameraType = cameraType;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }
}
