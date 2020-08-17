package com.arjinmc.smartcam.transfer;

/**
 * TransferCallback
 * Created by Eminem Lo on 11/8/2020.
 * email: arjinmc@hotmail.com
 */
public interface OnTransferCallback {

    /**
     * transfer success
     */
    void onSuccess();

    /**
     * transfer failure
     */
    void onFailure();
}
