package com.arjinmc.smartcam.core.comparator;

import com.arjinmc.smartcam.core.model.CameraSize;

import java.util.Comparator;

/**
 * Preview for CompareSizesByArea
 * Created by Eminem Lo on 2020-07-31.
 * email: arjinmc@hotmail.com
 */
public class CompareSizesByArea implements Comparator<CameraSize> {

    @Override
    public int compare(CameraSize lhs, CameraSize rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                (long) rhs.getWidth() * rhs.getHeight());
    }

}
