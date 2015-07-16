package com.gokuai.yunkuandroidsdk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.ServerData;
import com.gokuai.yunkuandroidsdk.data.ServerListData;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilFile;
import com.yunkuent.sdk.utils.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Slf on 2015/6/25.
 */
public class PreviewActivity extends BaseActivity implements View.OnClickListener {

    private String LOG_TAG = PreviewActivity.class.getSimpleName();

    private final static String KEY_ERRORCODE = "error_code";
    private final static String KEY_ERRORMSG = "error_msg";

    private static final String PREVIEW_SOCKET_SIGN_KEY = "6c01aefe6ff8f26b51139bf8f808dad582a7a864";

    private Socket mSocket;
    private String mFileHash;
    private String mOpenFileUrl;
    private MuPDFCore core;
    private MuPDFReaderView mDocView;
    private FileData mFileData;


    private TextView mTV_fileName;
    private TextView mTv_fileSize;
    private ImageView mIv_imageView;
    private ProgressBar mPB_progress;
    private View mConvertErrorView;
    private TextView mTV_ErrorDescription;
    private Button mBtn_Retry;

    private boolean willShowMenu;
    private boolean isStop;
    private boolean isSocketConnecting;

    private AsyncTask mGetServerTask;
    private AsyncTask mFileDataTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().show();
        setContentView(R.layout.preview_view_convert_layout);

        Intent intent = getIntent();
        mOpenFileUrl = intent.getStringExtra(Constants.EXTRA_OPEN_FILE_URL);

        if (!TextUtils.isEmpty(mOpenFileUrl)) {
            openPDFFile(mOpenFileUrl);
        } else {
            mFileData = intent.getParcelableExtra(Constants.EXTRA_FILEDATA);

            if (mFileData != null) {
                mFileHash = mFileData.getFilehash();
                setTitle(mFileData.getFilename());
                createConvertViewer();
                initData();
            }
        }

    }


    private void createConvertViewer() {
        setContentView(R.layout.preview_view_convert_layout);
        mTV_fileName = (TextView) findViewById(R.id.preview_file_name_tv);
        mTv_fileSize = (TextView) findViewById(R.id.preview_file_size_tv);
        mIv_imageView = (ImageView) findViewById(R.id.preview_image_iv);
        mPB_progress = (ProgressBar) findViewById(R.id.preview_convert_progress_pb);
        mConvertErrorView = findViewById(R.id.preview_error_view);
        mTV_ErrorDescription = (TextView) findViewById(R.id.preview_error_description_tv);
        mBtn_Retry = (Button) findViewById(R.id.preview_retry_btn);

        long filesize = mFileData.getFilesize();
        mTV_fileName.setText(mFileData.getFilename());
        mTv_fileSize.setText(Util.formatFileSize(this, filesize));
        mIv_imageView.setImageResource(mFileData.getExt(this));
        mPB_progress.setIndeterminate(true);
        mBtn_Retry.setOnClickListener(this);

        if (filesize > 104857600l) {
            onError(R.string.preview_file_too_large);
        }

    }

    private void onError(String errorString) {
        showErrorView(true);
        willShowMenu = true;
        mTV_ErrorDescription.setText(errorString);
        supportInvalidateOptionsMenu();
    }

    private void onError(int stringRes) {
        onError(getString(stringRes));
    }


    private void initData() {
        showErrorView(false);
        if (TextUtils.isEmpty(Config.URL_SOCKET_PREVIEW)) {
            String url = Config.getPreviewSite(this);
            if (TextUtils.isEmpty(url)) {
                if (Util.isNetworkAvailableEx()) {
                    mGetServerTask = new AsyncTask<Void, Void, Object>() {
                        @Override
                        protected Object doInBackground(Void... params) {
                            return FileDataManager.getInstance().getPreviewServerSite();
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            ServerListData data = (ServerListData) o;
                            if (data.getCode() == HttpStatus.SC_OK) {
                                ArrayList<ServerData> list = data.getServerList();
                                if (list.size() > 0) {
                                    ServerData serverData = list.get(0);
                                    String serverUrl = "http://" + serverData.getHost() + ":" + serverData.getPort();
                                    Config.setPreviewSite(PreviewActivity.this, serverUrl);
                                    Config.URL_SOCKET_PREVIEW = serverUrl;
                                    initPreview();
                                } else {
                                    onError(R.string.tip_no_preview_server_available);
                                }
                            } else {
                                onError(data.getErrorMsg());
                            }
                        }
                    }.execute();
                } else {
                    onError(R.string.tip_net_is_not_available);
                }
            } else {
                Config.URL_SOCKET_PREVIEW = url;
                initPreview();
            }
        } else {
            initPreview();
        }
    }


    private void initPreview() {

        mFileDataTask = FileDataManager.getInstance().getFileInfoAsync(mFileData.getFullpath(), new FileDataManager.FileInfoListener() {
            @Override
            public void onReceiveData(Object result) {
                if (result != null) {
                    FileData urlData = (FileData) result;
                    if (urlData.getCode() == HttpStatus.SC_OK) {
                        String localFilePath = Config.getPdfFilePath(mFileData.getFilehash());
                        if (new File(localFilePath).exists()) {
                            openPDFFile(localFilePath);
                        } else {
                            String urls = urlData.getUri();
                            if (urls != null) {

                                ArrayList<NameValuePair> params = new ArrayList<>();
                                String url = Config.URL_SOCKET_PREVIEW;
                                params.add(new BasicNameValuePair("ext", UtilFile.getExtension(mFileData.getFilename())));
                                params.add(new BasicNameValuePair("filehash", mFileHash));
                                params.add(new BasicNameValuePair("url", urlData.getUri()));
                                params.add(new BasicNameValuePair("sign", FileDataManager.getInstance().generateSignOrderByKey(params, PREVIEW_SOCKET_SIGN_KEY, false)));

                                DebugFlag.log(LOG_TAG, "params:" + params);

                                if (params.size() > 0) {
                                    url += "?";
                                    for (int i = 0; i < params.size(); i++) {
                                        url += params.get(i).getName() + "=" + URLEncoder.encodeUTF8(params.get(i).getValue()) + ((i == params.size() - 1) ? "" : "&");
                                    }
                                }

                                try {
                                    DebugFlag.log(LOG_TAG, "connect url:" + url);
                                    IO.Options options = new IO.Options();
                                    options.forceNew = true;
                                    mSocket = IO.socket(url, options);
                                    mSocket.on("progress", mOnNewMessage);
                                    mSocket.on("err", mOnErrMessage);
                                    mSocket.connect();
                                    mHandler.sendEmptyMessage(MSG_CONVERT_START);
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                PreviewActivity.this.onError(R.string.tip_connect_server_failed);
                            }

                        }
                    } else {
                        PreviewActivity.this.onError(urlData.getErrorMsg());
                    }
                } else {
                    PreviewActivity.this.onError(R.string.tip_connect_server_failed);
                }

            }

            @Override
            public void onReceiveHttpResponse(int actionId) {

            }

            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onHookError(HookCallback.HookType type) {

            }

            @Override
            public void onNetUnable() {
                String localFilePath = Config.getPdfFilePath(mFileData.getFilehash());

                if (new File(localFilePath).exists()) {
                    openPDFFile(localFilePath);
                }

                PreviewActivity.this.onError(R.string.tip_net_is_not_available);
            }
        });

    }


    private final static int MSG_CONVERT_START = 0;
    private final static int MSG_CONVERT_COMPLETE = 1;
    private final static int MSG_UPDATE_DOWNLOAD_TO_LOCAL = 2;
    private final static int MSG_CONNECT_ERROR = 3;
    private final static int MSG_DOWNLOAD_ERROR = 4;
    private final static int MSG_UPDATE_PROGRESS = 5;
    private final static int MSG_CLOSE_SOCKET = 6;


    private static class MyHandler extends Handler {
        private final WeakReference<PreviewActivity> mActivity;

        public MyHandler(PreviewActivity application) {
            mActivity = new WeakReference<>(application);
        }

        @Override
        public void handleMessage(Message msg) {
            final PreviewActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_CONVERT_START:
                        activity.isSocketConnecting = true;
                        break;
                    case MSG_CONVERT_COMPLETE:
                        activity.socketRelease();
                        break;
                    case MSG_UPDATE_DOWNLOAD_TO_LOCAL:
                        break;
                    case MSG_CONNECT_ERROR:
                        activity.mTV_ErrorDescription.setText(msg.obj + "");
                        activity.socketRelease();
                        activity.showErrorView(true);
                        break;
                    case MSG_DOWNLOAD_ERROR:
                        activity.onError(R.string.tip_preview_file_download_failed);
                        activity.socketRelease();
                        break;
                    case MSG_UPDATE_PROGRESS:
                        activity.mPB_progress.setIndeterminate(false);
                        activity.mPB_progress.setProgress(msg.arg1);
                        break;
                    case MSG_CLOSE_SOCKET:
                        activity.onError(R.string.tip_connect_out_time);
                        activity.socketRelease();
                        break;
                }
            }
        }
    }

    private final Handler mHandler = new MyHandler(this);

    private Emitter.Listener mOnErrMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            String errorMessage = "";
            int errorCode = 0;
            try {
                errorCode = data.getInt(KEY_ERRORCODE);
                errorMessage = data.getString(KEY_ERRORMSG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = MSG_CONNECT_ERROR;
            message.obj = errorCode + ":" + errorMessage;
            mHandler.sendMessage(message);

            DebugFlag.log(LOG_TAG, "err code:" + errorCode + "\t errorMessage:" + errorMessage);
        }
    };


    private Emitter.Listener mOnNewMessage = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            int progress;
            String url;
            try {
                progress = data.getInt("progress");
                url = data.getString("url");
            } catch (JSONException e) {
                return;
            }

            if (progress == 100) {
                mHandler.sendEmptyMessage(MSG_CONVERT_COMPLETE);
                onConvertDone(url);
            }
            DebugFlag.log(LOG_TAG, "url:" + url + "\n progress" + progress);
        }
    };

    private void onConvertDone(final String url) {
        final String localFilePath = Config.getPdfFilePath(mFileHash);
        File file = new File(localFilePath);
        if (!file.exists()) {
            if (!file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }

        }

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);


        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            Header header = response.getFirstHeader("Content-Length");
            long totalLength = Long.parseLong(header.getValue());

            InputStream in = response.getEntity().getContent();
            FileOutputStream fos = new FileOutputStream(new File(localFilePath));

            byte[] buffer = new byte[4096];
            int length;
            long byteNow = 0;
            while ((length = in.read(buffer)) > 0) {
                if (isStop) {
                    break;
                }
                fos.write(buffer, 0, length);
                byteNow += length;

                Message message = new Message();
                message.what = MSG_UPDATE_PROGRESS;
                message.arg1 = (int) (((float) byteNow / (float) totalLength) * (float) 100);
                mHandler.sendMessage(message);

            }
        } catch (IOException e) {
            mHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR);
            e.printStackTrace();
        }

        openPDFFile(localFilePath);

    }


    private void openPDFFile(final String filePath) {
        DebugFlag.log(LOG_TAG, "local path:" + filePath);
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    willShowMenu = true;
                    supportInvalidateOptionsMenu();

                    setContentView(R.layout.preview_view_layout);


                    RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.pdflayout);

                    core = openFile(filePath);

                    if (core != null && core.countPages() == 0) {
                        core = null;
                    }
                    if (core == null || core.countPages() == 0 || core.countPages() == -1) {
                        Log.e(LOG_TAG, "Document Not Opening");
                    }
                    if (core != null) {
                        mDocView = new MuPDFReaderView(PreviewActivity.this) {
                            @Override
                            protected void onMoveToChild(int i) {
                                if (core == null)
                                    return;
                                super.onMoveToChild(i);
                            }

                            @Override
                            protected void onTapMainDocArea() {
                                if (getSupportActionBar().isShowing()) {
                                    getSupportActionBar().hide();
                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                                } else {
                                    getSupportActionBar().show();
                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                                }
                                super.onTapMainDocArea();
                            }
                        };

                        mDocView.setAdapter(new MuPDFPageAdapter(PreviewActivity.this, core));
                        mainLayout.addView(mDocView);
                    }

                }
            });

        } catch (Exception e) {
            createConvertViewer();
            onError(R.string.convert_error);
        }

    }


    private MuPDFCore openFile(String path) {
        try {
            core = new MuPDFCore(this, path);
            // New file: drop the old outline data
        } catch (Exception e) {
            return null;
        }
        return core;
    }


    private void socketRelease() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("progress", mOnNewMessage);
            mSocket.off("err", mOnErrMessage);
        }
        isSocketConnecting = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (willShowMenu) {
            getMenuInflater().inflate(R.menu.menu_preview, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_btn_send) {
            FileOpenManager.getInstance().handle(PreviewActivity.this, mFileData);
        } else if (i == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.preview_retry_btn) {
            initData();
        }
    }

    private void showErrorView(boolean show) {
        if (mConvertErrorView != null) {
            mConvertErrorView.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (mPB_progress != null) {
            mPB_progress.setVisibility(show ? View.GONE : View.VISIBLE);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isSocketConnecting) {
            mHandler.sendEmptyMessageDelayed(MSG_CLOSE_SOCKET, 120000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (core != null) {
            core.onDestroy();
            core = null;
        }

        socketRelease();

        if (mFileDataTask != null) {
            mFileDataTask.cancel(true);
        }

        if (mGetServerTask != null) {
            mGetServerTask.cancel(true);
        }

        isStop = true;

        //unregisterScreenReceiver();
    }
}
