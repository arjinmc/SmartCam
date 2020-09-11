package com.arjinmc.smartcam.core.model;

import android.graphics.Color;

import androidx.annotation.IntDef;

import java.io.Serializable;

/**
 * Manual Focus params for camera preview
 * Created by Eminem Lo on 11/9/2020.
 * email: arjinmc@hotmail.com
 */
public class CameraManualFocusParams implements Serializable {

    private static final long serialVersionUID = -4986409086257152568L;

    public static final int CAMERA_MANUAL_FOCUS_SHAPE_CIRCLE = 0;
    public static final int CAMERA_MANUAL_FOCUS_SHAPE_SQUARE = 1;

    private final int DEFAULT_COLOR = Color.WHITE;
    private final int DEFAULT_BORDER_WIDTH = 2;
    private final int DEFAULT_RADIUS = 100;

    /**
     * shape for auto focus, default shape is circle
     */
    private @Shape
    int shape = CAMERA_MANUAL_FOCUS_SHAPE_CIRCLE;

    /**
     * color for border of shape
     */
    private int color = DEFAULT_COLOR;

    /**
     * width for border
     */
    private int borderWidth = DEFAULT_BORDER_WIDTH;

    /**
     * radius of circle if shape is circle
     */
    private float radius = DEFAULT_RADIUS;

    /**
     * size of focus shape
     */
    private CameraSize size;

    public int getShape() {
        return shape;
    }

    public void setShape(@Shape int shape) {
        this.shape = shape;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth <= 0) {
            borderWidth = DEFAULT_BORDER_WIDTH;
        }
        this.borderWidth = borderWidth;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        if (radius <= 0) {
            radius = DEFAULT_RADIUS;
        }
        this.radius = radius;

    }

    public CameraSize getSize() {
        return size;
    }

    public void setSize(CameraSize size) {
        if (size == null) {
            size = new CameraSize(DEFAULT_RADIUS * 2, DEFAULT_RADIUS * 2);
        }
        this.size = size;
    }

    @IntDef(value = {CAMERA_MANUAL_FOCUS_SHAPE_CIRCLE, CAMERA_MANUAL_FOCUS_SHAPE_SQUARE})
    public @interface Shape {
    }

}
