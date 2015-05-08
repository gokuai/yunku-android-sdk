package com.gokuai.yunkuandroidsdk.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.gokuai.yunkuandroidsdk.GKApplication;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.callback.CallBack;

public class UtilDialog {

    public static void showNormalToast(String message) {
        Toast.makeText(GKApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showNormalToast(int messageRes) {
        Toast.makeText(GKApplication.getInstance(), messageRes, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跨越线程通知
     *
     * @param string
     */
    public static void showCrossThreadToast(String string) {
        Message msg = new Message();
        msg.what = GKApplication.MSG_CROSS_THREAD_TOAST;
        if (GKApplication.getInstance() != null) {
            msg.obj = string;
            GKApplication.getInstance().getHandler().sendMessage(msg);
        }

    }

    public static void showCrossThreadToast(int resId) {
        Message msg = new Message();
        msg.what = GKApplication.MSG_CROSS_THREAD_TOAST;
        if (GKApplication.getInstance() != null) {
            msg.obj = GKApplication.getInstance().getString(resId);
            GKApplication.getInstance().getHandler().sendMessage(msg);
        }


    }


    private static ProgressDialog loadingDialog;
    private static boolean willShow;

    public static synchronized void dismissLoadingDialog(final Context context) {
        if (context != null && !((Activity) context).isFinishing()
                && loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
            willShow = false;
        }

    }

    /**
     * 顶部toast
     *
     * @param context
     * @param message
     */
    public static void showTopToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -300);
        toast.show();
    }

    /**
     * 顶部toast
     *
     * @param context
     * @param stringRes
     */
    public static void showTopToast(Context context, int stringRes) {
        Toast toat = Toast.makeText(context, stringRes, Toast.LENGTH_SHORT);
        toat.setGravity(Gravity.CENTER_HORIZONTAL, 0, -300);
        toat.show();
    }

    /**
     * 显示正在...对话框
     */
    public static void showDialogLoading(Context context, String message, DialogInterface.OnKeyListener listener, boolean cancelAble) {
        willShow = true;
        dismissLoadingDialog(context);
        loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage(message);
        loadingDialog.setOnKeyListener(listener);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(cancelAble);
        if (!((Activity) context).isFinishing() && willShow) {
            loadingDialog.show();
        }
    }

    public static void showDialogLoading(Context context, String message, boolean cancelAble) {
        showDialogLoading(context, message, null, cancelAble);
    }

    /**
     * 执行loading的任务，需要先执行showDialogLoading，再执行asyncTask
     *
     * @param context
     * @param message
     * @param asyncTask
     */
    public static void showDialogLoading(final Context context, String message, final AsyncTask asyncTask) {

        showDialogLoading(context, message, new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (asyncTask != null) {
                        asyncTask.cancel(true);
                    }
                    UtilDialog.dismissLoadingDialog(context);
                }
                return false;
            }
        }, false);

    }


    public static void showDialogSameFileExist(Context context, final CallBack callBack) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.tip).setMessage(context.getString(R.string.tip_replace_for_same_file))
                .setPositiveButton(R.string.replace, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.call();
                    }
                }).setNegativeButton(R.string.cancel, null).setCancelable(false).create().show();

    }
}
