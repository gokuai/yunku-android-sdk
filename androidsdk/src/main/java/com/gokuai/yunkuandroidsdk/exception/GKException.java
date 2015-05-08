package com.gokuai.yunkuandroidsdk.exception;

import android.content.Context;

import com.gokuai.yunkuandroidsdk.GKApplication;

/**
 * Gk custom exception
 */
public class GKException extends Exception {

    public static final int ERRORCODE_UNKNOWN = 0;
    public static final int ERRORCODE_ERROR_PARAM = 1;
    protected int errorCode;

    public GKException(int errorcode) {
        super("");
        this.errorCode = errorcode;
    }

    public GKException(String s) {
        super(s);
    }

    protected Context getContext() {
        return GKApplication.getInstance();
    }


    public String getErrorDescription() {
        return "Unknown exception";
    }
}
