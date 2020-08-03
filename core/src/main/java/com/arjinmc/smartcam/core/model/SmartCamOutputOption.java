package com.arjinmc.smartcam.core.model;

import android.graphics.Matrix;
import android.media.Image;

import java.io.File;
import java.io.Serializable;

/**
 * Option for output file
 * Created by Eminem Lo on 3/8/2020.
 * email: arjinmc@hotmail.com
 */
public class SmartCamOutputOption implements Serializable {

    private static final long serialVersionUID = -1996527596137638872L;

    private Image image;
    private File file;
    private Integer degree;
    private int previewWidth;
    private int previewHeight;
    private Matrix matrix;

    public SmartCamOutputOption(Image image, File file, Integer degree, int previewWidth, int previewHeight, Matrix matrix) {
        this.image = image;
        this.file = file;
        this.degree = degree;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.matrix = matrix;
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
}
