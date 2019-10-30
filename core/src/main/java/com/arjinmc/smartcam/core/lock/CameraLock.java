package com.arjinmc.smartcam.core.lock;

import com.arjinmc.smartcam.core.SmartCamConfig;

import java.util.concurrent.Semaphore;

/**
 * Camera lock
 * Created by Eminem Lo on 2019-10-29.
 * email: arjinmc@hotmail.com
 */
public class CameraLock extends Semaphore {

    private long lastTimestamp;

    public CameraLock() {
        super(1);
    }

    public boolean isLock() {
        if (lastTimestamp == 0) {
            lastTimestamp = System.currentTimeMillis();
            return false;
        }
        if (System.currentTimeMillis() - lastTimestamp > SmartCamConfig.LOCK_TIMEOUT_DURATION) {
            lastTimestamp = System.currentTimeMillis();
            return false;
        }
        return true;
    }
}
