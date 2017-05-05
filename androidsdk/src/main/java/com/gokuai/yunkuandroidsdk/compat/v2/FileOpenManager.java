package com.gokuai.yunkuandroidsdk.compat.v2;

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

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handle file download and open
 */
public class FileOpenManager {
    public static final int BUFFER_SIZE = 4096;

    private static FileOpenManager instance = null;

    protected FileOpenManager() {

    }

    public synchronized static FileOpenManager getInstance() {
        if (instance == null) {
            instance = new FileOpenManager();
        }
        return instance;
    }

    private TextView mTv_DialogCount;
    private TextView mTv_DialogStatus;
    private ProgressBar mPb_DialogProgress;

    private Thread mOpenTask;
    private Thread mDownloadTask;
    private AlertDialog mDialog;
    private Context mContext;

    private final static int MSG_CONNECTING = 0;
    private final static int MSG_START_TO_DOWNLOAD = 1;
    private final static int MSG_UPDATE_PROGRESS = 3;
    private final static int MSG_ERROR = 4;
    private final static int MSG_DOWNLOAD_COMPLETE = 5;


    private static class MyHandler extends Handler {
        private final WeakReference<FileOpenManager> mManager;

        public MyHandler(FileOpenManager manager) {
            super(Looper.getMainLooper());
            mManager = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            final FileOpenManager manager = mManager.get();
            if (manager != null) {
                switch (msg.what) {
                    case MSG_CONNECTING://正在链接
                        manager.mTv_DialogStatus.setText(R.string.connecting);
                        break;
                    case MSG_START_TO_DOWNLOAD://开始下载
                        manager.mTv_DialogStatus.setText(R.string.downloading);
                        break;
                    case MSG_ERROR://返回错误
                        UtilDialog.showNormalToast(msg.obj + "");
                        manager.mDialog.dismiss();
                        break;
                    case MSG_UPDATE_PROGRESS://显示进度
                        manager.mPb_DialogProgress.setIndeterminate(false);
                        manager.mPb_DialogProgress.setProgress(msg.arg1);
                        manager.mTv_DialogCount.setText(msg.arg1 + "%");
                        break;
                    case MSG_DOWNLOAD_COMPLETE://郑子啊打开
                        manager.mTv_DialogStatus.setText(R.string.opening);
                        break;
                }
            }
        }
    }

    private final Handler mHandler = new MyHandler(this);

    public void handle(final Context context, final FileData data) {
        mContext = context;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null);
        mTv_DialogStatus = (TextView) dialogView.findViewById(R.id.dialog_status_tv);
        mTv_DialogCount = (TextView) dialogView.findViewById(R.id.dialog_progress_bar_count_tv);
        mPb_DialogProgress = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_pb);

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(data.getFilename())
                .setView(dialogView).setCancelable(false).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (mOpenTask != null) {
                            mOpenTask.interrupt();
                        }
                        if (mDownloadTask != null) {
                            mDownloadTask.interrupt();
                        }
                    }
                });

        mDialog = builder.create();
        mDialog.show();

        if (Util.isCacheFileExit(data.getFilehash(), data.getFilesize())) {
            mTv_DialogStatus.setText(R.string.opening);
            openFile(context, data);
        } else {
            mDownloadTask = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        FileData fileData = FileDataManager.getInstance().getFileInfoSync(data.getFullpath());
                        downloadFile(fileData);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.what = MSG_ERROR;
                        message.obj = mContext.getString(R.string.download_failed);
                        mHandler.sendMessage(message);
                    }

                }
            };
            mDownloadTask.start();
        }
    }

    /**
     * 下载文件
     * @param data
     * @throws IOException
     */
    private void downloadFile(final FileData data) throws IOException {
        mHandler.sendEmptyMessage(MSG_CONNECTING);

        String fileLocalPath = Config.getLocalFilePath(data.getFilehash());

        //Create folder
        File file = new File(fileLocalPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }


        FileData fileData = FileDataManager.getInstance().getFileInfoSync(data.getFullpath());
        if (fileData != null) {
            URL url = new URL(fileData.getUri());
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                mHandler.sendEmptyMessage(MSG_START_TO_DOWNLOAD);
                long totalLength = httpConn.getContentLength();

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(fileLocalPath);

                long byteNow = 0;
                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    byteNow += bytesRead;
                    Message message = new Message();
                    message.what = MSG_UPDATE_PROGRESS;
                    message.arg1 = (int) (((float) byteNow / (float) totalLength) * (float) 100);
                    mHandler.sendMessage(message);
                }

                outputStream.close();
                inputStream.close();

                openFile(mContext, data);

            } else {
                Message message = new Message();
                message.what = MSG_ERROR;
                message.obj = mContext.getString(R.string.download_failed);
                mHandler.sendMessage(message);
            }
            httpConn.disconnect();

        } else {
            Message message = new Message();
            message.what = MSG_ERROR;
            message.obj = mContext.getString(R.string.get_file_info_failed);
            mHandler.sendMessage(message);

        }
    }

    /**
     * 打开文件
     * @param context
     * @param data
     */
    private void openFile(Context context, FileData data) {
        mPb_DialogProgress.setIndeterminate(true);
        mOpenTask = Util.handleLocalFile(context, data.getFilehash(), data.getFilename(), data.getFilesize(), new Util.FileOpenListener() {

            @Override
            public void onHandle(int type) {
                mDialog.dismiss();
            }

            @Override
            public void onError(String errorMsg) {
                mDialog.dismiss();
                UtilDialog.showCrossThreadToast(errorMsg);

            }
        }, Constants.HANDLE_TYPE_OPEN);
    }
}
