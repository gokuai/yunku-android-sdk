package com.gokuai.yunkuandroidsdk;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.data.LocalFileData;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;
import com.yunkuent.sdk.upload.UploadCallBack;

import java.lang.ref.WeakReference;

/**
 * Created by Brandon on 15/5/13.
 */
public class FileUploadManager implements UploadCallBack {


    private static FileUploadManager mInstance;

    public synchronized static FileUploadManager getInstance() {
        if (mInstance == null) {
            mInstance = new FileUploadManager();
        }
        return mInstance;
    }

//
//    /**
//     * 添加到上传队列中
//     *
//     * @param fullPath
//     * @param localPath
//     */
//    public void addUploadQueue(String fullPath, LocalFileData localPath) {
//        //TODO
//    }

    private final static int MSG_UPDATE_PROGRESS = 3;
    private final static int MSG_ERROR = 4;
    private final static int MSG_SUCCESS = 5;

    private static class MyHandler extends Handler {
        private final WeakReference<FileUploadManager> mManager;

        public MyHandler(FileUploadManager manager) {
            super(Looper.getMainLooper());
            mManager = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            final FileUploadManager manager = mManager.get();
            if (manager != null) {
                switch (msg.what) {
                    case MSG_ERROR:
                        UtilDialog.showNormalToast(msg.obj + "");
                        manager.mDialog.dismiss();
                        break;
                    case MSG_UPDATE_PROGRESS:
                        manager.mPb_DialogProgress.setIndeterminate(false);
                        manager.mPb_DialogProgress.setProgress(msg.arg1);
                        manager.mTv_DialogCount.setText(msg.arg1 + "%");
                        break;
                    case MSG_SUCCESS:
                        UtilDialog.showNormalToast(R.string.upload_success);
                        manager.mDialog.dismiss();
                        break;

                }
            }
        }
    }

    private Handler mHandler = new MyHandler(this);

    private TextView mTv_DialogCount;
    private TextView mTv_DialogStatus;
    private ProgressBar mPb_DialogProgress;

    private Thread mUploadThread;
    private AlertDialog mDialog;

    /**
     * 上传单个文件
     *
     * @param context
     * @param fullPath
     * @param localPath
     */
    public void upload(Context context, String fullPath, LocalFileData localPath) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
        mTv_DialogStatus = (TextView) dialogView.findViewById(R.id.dialog_status_tv);
        mTv_DialogCount = (TextView) dialogView.findViewById(R.id.dialog_progress_bar_count_tv);
        mPb_DialogProgress = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_pb);

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(localPath.getFilename())
                .setView(dialogView).setCancelable(false).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mUploadThread != null) {
                            mUploadThread.interrupt();
                        }
                    }
                });

        mDialog = builder.create();
        mDialog.show();

        mTv_DialogStatus.setText(R.string.uploading);
        mUploadThread = FileDataManager.getInstance().addFile(fullPath + localPath.getFilename(), localPath.getFullpath(), this);
    }

    @Override
    public void onSuccess(long l) {
        mHandler.sendEmptyMessage(MSG_SUCCESS);
    }

    @Override
    public void onFail(long l, String s) {
        Message message = new Message();
        message.what = MSG_ERROR;
        message.obj = s;
        mHandler.sendMessage(message);
    }

    @Override
    public void onProgress(long l, float percent) {

        Message message = new Message();
        message.what = MSG_UPDATE_PROGRESS;
        message.arg1 = (int) (percent * 100);
        mHandler.sendMessage(message);

    }
}
