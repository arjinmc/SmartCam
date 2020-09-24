package com.arjinmc.smartcam.core.model;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Pattern;

/**
 * Aspect ratio for camera preview
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public class CameraAspectRatio implements Comparable<CameraAspectRatio> {

    private final String PATTERN = "^\\d+:\\d+$";

    /**
     * ratio:x
     */
    private int x;
    /**
     * ratio:y
     */
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return x + ":" + y;
    }

    public void parse(String aspectRatio) {
        if (TextUtils.isEmpty(aspectRatio)) {
            return;
        }
        if (Pattern.compile(PATTERN).matcher(aspectRatio).matches()) {
            int index = aspectRatio.indexOf(":");
            x = Integer.parseInt(aspectRatio.substring(0, index));
            y = Integer.parseInt(aspectRatio.substring(index + 1));
            Log.e("result", x + ":" + y);
        }
    }

    public boolean isValid() {
        if (x > 0 && y > 0) {
            return true;
        }
        return false;
    }

    public void toRatio(int width, int height) {
        int gcd = gcd(width, height);
        x = width / gcd;
        y = height / gcd;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }
        return a;
    }

    public float toFloat() {
        return (float) x / y;
    }

    @Override
    public int compareTo(CameraAspectRatio aspectRatio) {
        if (equals(aspectRatio)) {
            if (x > aspectRatio.x || y > aspectRatio.y) {
                return 1;
            }
            return 0;
        } else if (toFloat() - aspectRatio.toFloat() > 0) {
            return 1;
        }
        return -1;
    }

}
