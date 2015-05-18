package com.gokuai.yunkuandroidsdk.dialog;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Brandon on 15/5/18.
 */
public abstract class DialogManger {

    public DialogManger(Context context) {
        mContext = context;
    }


    protected Context mContext;

    protected Button mOKBtn;

    public interface DialogActionListener {
        void onDone(String fullPath);
    }

    public abstract void showDialog(final String fullPath, final DialogActionListener listener);
}
