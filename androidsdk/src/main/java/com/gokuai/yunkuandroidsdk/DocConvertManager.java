package com.gokuai.yunkuandroidsdk;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.ServerData;
import com.gokuai.yunkuandroidsdk.data.ServerListData;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilFile;
import com.yunkuent.sdk.utils.URLEncoder;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Brandon on 15/7/20.
 */
public class DocConvertManager {

    private final static String LOG_TAG = DocConvertManager.class.getSimpleName();
    private static final String PREVIEW_SOCKET_SIGN_KEY = "6c01aefe6ff8f26b51139bf8f808dad582a7a864";


    private final static String KEY_ERRORCODE = "error_code";
    private final static String KEY_ERRORMSG = "error_msg";

    private static class SingletonHolder {
        private static final DocConvertManager INSTANCE = new DocConvertManager();
    }

    private DocConvertManager() {
    }

    public static DocConvertManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public interface PreviewInfoListener {

        int STATUS_CODE_ANALYZE_SERVER = 1;
        int STATUS_CODE_START_TO_CONVERT_PDF = 2;
        int STATUS_CODE_COMPLETE = 3;

        int ERROR_CODE_UNSUPPORTED = 101;
        int ERROR_CODE_GET_FILE_INFO_ERROR = 102;
        int ERROR_CODE_FILE_CONVERT_ERROR = 103;
        int ERROR_CODE_INCOMPLETE = 104;

        void onStatus(String fullPath, int status);

        void onProgress(int percent);

        void onError(int errorCode, String fullPath, String message);

        void onGetPDFUrl(String fullPath, String url);

    }

    private AsyncTask mGetServerTask;
    private AsyncTask mFileDataTask;

    private Socket mSocket;
    private PreviewInfoListener mInfoListener;
    private Context mContext;
    private boolean gettingPreview = false;
    private String mFullPath;

    public void convertDocToPDF(final Context context, final String fullPath, final PreviewInfoListener listener) {
        String filename = Util.getNameFromPath(fullPath).replace("/", "");

        if (UtilFile.isPreviewFile(filename)) {
            if (!gettingPreview) {
                gettingPreview = true;

                mInfoListener = listener;
                mContext = context;
                mFullPath = fullPath;

                mHandler.sendEmptyMessage(MSG_GETTING_PREVIEW_SITE);
                if (TextUtils.isEmpty(Config.URL_SOCKET_PREVIEW)) {
                    String url = Config.getPreviewSite(context);
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
                                            Config.setPreviewSite(context, serverUrl);
                                            Config.URL_SOCKET_PREVIEW = serverUrl;
                                            initPreview(fullPath);
                                        } else {
                                            onError(R.string.tip_no_preview_server_available, PreviewInfoListener.ERROR_CODE_GET_FILE_INFO_ERROR);
                                        }
                                    } else {
                                        onError(data.getErrorMsg(), PreviewInfoListener.ERROR_CODE_GET_FILE_INFO_ERROR);
                                    }
                                }
                            }.execute();
                        } else {
                            initPreview(fullPath);
                        }
                    } else {
                        Config.URL_SOCKET_PREVIEW = url;
                        initPreview(fullPath);
                    }
                } else {
                    initPreview(fullPath);
                }

            } else {
                onError("Is Previewing", PreviewInfoListener.ERROR_CODE_INCOMPLETE);
            }
        } else {
            onError("Unsupported file type", PreviewInfoListener.ERROR_CODE_UNSUPPORTED);
        }


    }


    /**
     * 取消请求
     */
    public void cancel() {
        if (mFileDataTask != null) {
            mFileDataTask.cancel(true);
        }

        if (mGetServerTask != null) {
            mGetServerTask.cancel(true);
        }

        if (mInfoListener != null) {
            mInfoListener = null;
        }
        gettingPreview = false;

        socketRelease();

    }


    private void initPreview(String fullPath) {

        mFileDataTask = FileDataManager.getInstance().getFileInfoAsync(fullPath, new FileDataManager.FileInfoListener() {
            @Override
            public void onReceiveData(Object result) {
                if (result != null) {
                    FileData urlData = (FileData) result;
                    if (urlData.getCode() == HttpStatus.SC_OK) {
                        String urls = urlData.getUri();
                        if (urls != null) {

                            ArrayList<NameValuePair> params = new ArrayList<>();
                            String url = Config.URL_SOCKET_PREVIEW;
                            params.add(new BasicNameValuePair("ext", UtilFile.getExtension(urlData.getFilename())));
                            params.add(new BasicNameValuePair("filehash", urlData.getFilehash()));
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
                            DocConvertManager.this.onError(R.string.tip_connect_server_failed, PreviewInfoListener.ERROR_CODE_FILE_CONVERT_ERROR);
                        }

                    } else {
                        DocConvertManager.this.onError(urlData.getErrorMsg(), PreviewInfoListener.ERROR_CODE_FILE_CONVERT_ERROR);
                    }
                } else {
                    DocConvertManager.this.onError(R.string.tip_connect_server_failed, PreviewInfoListener.ERROR_CODE_FILE_CONVERT_ERROR);
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

                DocConvertManager.this.onError(R.string.tip_net_is_not_available, PreviewInfoListener.ERROR_CODE_GET_FILE_INFO_ERROR);
            }
        });

    }

    private void onError(int res, int code) {
        onError(mContext.getString(res), code);
    }

    private void onError(String message, int code) {
        mInfoListener.onError(code, mFullPath, message);
        socketRelease();
    }

    private final static int MSG_GETTING_PREVIEW_SITE = 0;
    private final static int MSG_CONVERT_START = 1;
    private final static int MSG_CONVERT_COMPLETE = 2;
    private final static int MSG_CONNECT_ERROR = 3;
    private final static int MSG_UPDATE_PROGRESS = 5;
    private final static int MSG_CLOSE_SOCKET = 6;

    private static class MyHandler extends Handler {
        private final WeakReference<DocConvertManager> mActivity;

        public MyHandler(DocConvertManager application) {
            super(Looper.getMainLooper());
            mActivity = new WeakReference<>(application);
        }

        @Override
        public void handleMessage(Message msg) {
            final DocConvertManager manager = mActivity.get();
            if (manager != null) {
                switch (msg.what) {
                    case MSG_GETTING_PREVIEW_SITE:
                        manager.mInfoListener.onStatus(manager.mFullPath, PreviewInfoListener.STATUS_CODE_ANALYZE_SERVER);
                        break;
                    case MSG_CONVERT_START:
                        manager.mInfoListener.onStatus(manager.mFullPath, PreviewInfoListener.STATUS_CODE_START_TO_CONVERT_PDF);
                        break;
                    case MSG_CONVERT_COMPLETE:
                        manager.mInfoListener.onStatus(manager.mFullPath, PreviewInfoListener.STATUS_CODE_COMPLETE);
                        manager.mInfoListener.onGetPDFUrl(manager.mFullPath, msg.obj.toString());
                        manager.socketRelease();
                        break;
                    case MSG_CONNECT_ERROR:
                        manager.onError(msg.obj + "", PreviewInfoListener.ERROR_CODE_FILE_CONVERT_ERROR);
                        break;
                    case MSG_UPDATE_PROGRESS:
                        manager.mInfoListener.onProgress(msg.arg1);
                        break;
                    case MSG_CLOSE_SOCKET:
                        manager.socketRelease();
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
            Message message = new Message();
            if (progress == 100) {
                message.what = MSG_CONVERT_COMPLETE;
                message.obj = url;
                mHandler.sendMessage(message);
            } else {
                message.arg1 = progress;
                message.what = MSG_UPDATE_PROGRESS;
                mHandler.sendMessage(message);

            }
            DebugFlag.log(LOG_TAG, "url:" + url + "\n progress" + progress);
        }
    };

    private void socketRelease() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("progress", mOnNewMessage);
            mSocket.off("err", mOnErrMessage);
        }
        gettingPreview = false;
    }

}
