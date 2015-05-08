package com.gokuai.yunkuandroidsdk.exception;


import com.gokuai.yunkuandroidsdk.R;

/**
 * Created by Brandon on 14-8-28.
 */
public class FileOperationException extends GKException {

    public static final int ERRORCODE_FILE_NO_EXIST = 101;

    public FileOperationException(int errorcode) {
        super(errorcode);
    }

    public FileOperationException(String s) {
        super(s);
    }


    @Override
    public String getErrorDescription() {
        switch (errorCode) {
            case ERRORCODE_FILE_NO_EXIST:
                return getContext().getString(R.string.tip_file_not_exist);
        }
        return super.getErrorDescription();
    }
}
