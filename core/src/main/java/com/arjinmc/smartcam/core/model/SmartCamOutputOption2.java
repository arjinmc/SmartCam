package com.arjinmc.smartcam.core.model;

import android.graphics.Matrix;
import android.media.Image;

import java.io.File;
import java.io.Serializable;

/**
 * Output option for Camera2
 * Created by Eminem Lo on 3/8/2020.
 * email: arjinmc@hotmail.com
 */
public class SmartCamOutputOption2 implements Serializable {

    private static final long serialVersionUID = -1996527596137638872L;

    /**
     * capture image result
     */
    private Image image;
    /**
     * output file
     */
    private File file;
    private Integer degree;
    private int previewWidth;
    private int previewHeight;
    private Matrix matrix;
    /**
     * {@link CameraType}
     */
    private int cameraType;
    /**
     * output uri
     */
    private String uri;

    public SmartCamOutputOption2(Image image, File file, Integer degree
            , int previewWidth, int previewHeight, Matrix matrix, @CameraType.Type int cameraType) {
        this.image = image;
        this.file = file;
        this.degree = degree;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.matrix = matrix;
        this.cameraType = cameraType;
    }

    public SmartCamOutputOption2(Image image, String uri, Integer degree
            , int previewWidth, int previewHeight, Matrix matrix, @CameraType.Type int cameraType) {
        this.image = image;
        this.uri = uri;
        this.degree = degree;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.matrix = matrix;
        this.cameraType = cameraType;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
