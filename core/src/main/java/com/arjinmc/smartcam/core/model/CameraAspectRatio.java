package com.arjinmc.smartcam.core.model;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Aspect ratio for camera preview
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public class CameraAspectRatio implements Comparable<CameraAspectRatio> {

    private final String PATTERN = "^\\d{1,2}:\\d{1,2}$";

    /**
     * ratio:x
     */
    private int x;
    /**
     * ratio:y
     */
    private int y;

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
        }
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
