package com.arjinmc.smartcam.core.model;

import java.io.Serializable;

/**
 * Data for capture result
 * Created by Eminem Lo on 22/9/2020.
 * email: arjinmc@hotmail.com
 */
public class SmartCamCaptureResult implements Serializable {

    private static final long serialVersionUID = 5914959535287905416L;

    private byte[] data;
    private int orientation;
    private boolean needReverse;
    private int previewWidth;
    private int previewHeight;
    private int cameraVersion;
    /**
     * preview ratio
     */
    private String ratio;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isNeedReverse() {
        return needReverse;
    }

    public void setNeedReverse(boolean needReverse) {
        this.needReverse = needReverse;
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

    public int getCameraVersion() {
        return cameraVersion;
    }

    public void setCameraVersion(int cameraVersion) {
        this.cameraVersion = cameraVersion;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public SmartCamCaptureResult(byte[] data, int cameraVersion, int orientation
            , boolean needReverse, int previewWidth, int previewHeight, String ratio) {
        this.data = data;
        this.cameraVersion = cameraVersion;
        this.orientation = orientation;
        this.needReverse = needReverse;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.ratio = ratio;
    }
}
