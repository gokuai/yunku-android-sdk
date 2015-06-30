package com.gokuai.yunkuandroidsdk;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.gokuai.yunkuandroidsdk.data.BaseData;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.FileDataKey;
import com.gokuai.yunkuandroidsdk.data.FileListData;
import com.gokuai.yunkuandroidsdk.data.ServerListData;
import com.gokuai.yunkuandroidsdk.exception.GKException;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.yunkuent.sdk.EntFileManager;
import com.yunkuent.sdk.UploadRunnable;
import com.yunkuent.sdk.upload.UploadCallBack;
import com.yunkuent.sdk.utils.URLEncoder;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Brandon on 15/4/10.
 */
public class FileDataManager {

    private static final String LOG_TAG = FileDataManager.class.getSimpleName();

    public static final int ACTION_ID_CREATE_FOLDER = 1;
    public static final int ACTION_ID_DELETE = 2;
    public static final int ACTION_ID_MOVE = 3;
    public static final int ACTION_ID_COPY = 4;
    public static final int ACTION_ID_RENAME = 5;


    private String mRootPath = Config.ORG_ROOT_PATH;
    private EntFileManager mEntFileManager;

    private static FileDataManager mInstance;

    private HookCallback mCallback;


    public void registerHook(HookCallback calback) {
        mCallback = calback;
    }

    public void unRegisterHook() {
        mCallback = null;
    }

    public boolean isHookRegisted() {
        return mCallback != null;
    }

    public synchronized static FileDataManager getInstance() {
        if (mInstance == null) {
            try {
                mInstance = new FileDataManager();
            } catch (GKException e) {
                Log.e(LOG_TAG, e.getErrorDescription());
            }
        }
        return mInstance;
    }

    public FileDataManager() throws GKException {
        if (TextUtils.isEmpty(Config.ORG_CLIENT_ID) && TextUtils.isEmpty(Config.ORG_CLIENT_SECRET)) {
            throw new GKException("You need set ORG_CLIENT_ID ORG_CLIENT_SECRET first ");
        }
        mEntFileManager = new EntFileManager(Config.ORG_CLIENT_ID, Config.ORG_CLIENT_SECRET);
    }

    /**
     * 重命名
     *
     * @param fullPath
     * @param newName
     * @param listener
     * @return
     */
    public AsyncTask rename(final String fullPath, final String newName, final DataListener listener) {
        if (!Util.isNetworkAvailableEx()) {
            listener.onNetUnable();
            return null;
        }


        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_RENAME, fullPath)) {
            listener.onHookError(HookCallback.HookType.HOOK_TYPE_RENAME);
            return null;
        }


        return new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {

                String parentPath = Util.getParentPath(fullPath);
                String newPath = parentPath + (TextUtils.isEmpty(parentPath) ? "" : "/") + newName;

                return mEntFileManager.move(fullPath,
                        newPath, "");
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                BaseData baseData = BaseData.create(o.toString());
                if (baseData != null) {
                    if (baseData.getCode() == HttpStatus.SC_OK) {
                        listener.onReceiveHttpResponse(ACTION_ID_RENAME);
                    } else {
                        listener.onError(baseData.getErrorMsg());
                    }
                } else {
                    listener.onError(GKApplication.getInstance().getString(R.string.tip_connect_server_failed));
                }
            }
        }.execute();
    }

    public AsyncTask copy(String fullPath, String targetPath, DataListener callBack) {
        //todo
        throw new UnsupportedOperationException();
    }

    public AsyncTask move(final String fullPath, final String targetPath, final DataListener listener) {
//        if (!Util.isNetworkAvailableEx()) {
//            listener.onNetUnable();
//            return null;
//        }
//
//
//        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_MOVE, fullPath)) {
//            return null;
//        }
//
//
//        return new AsyncTask<Void, Void, Object>() {
//
//            @Override
//            protected Object doInBackground(Void... params) {
//                return mEntFileManager.move((int) Util.getUnixDateline(), fullPath, targetPath, "");
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                BaseData baseData = BaseData.create(o.toString());
//                if (baseData != null) {
//                    if (baseData.getCode() == HttpStatus.SC_OK) {
//                        listener.onReceiveHttpResponse(ACTION_ID_MOVE);
//                    } else {
//                        listener.onError(baseData.getErrorMsg());
//                    }
//                } else {
//                    listener.onError(GKApplication.getInstance().getString(R.string.tip_connect_server_failed));
//                }
//            }
//        }.execute();
        //TODO
        throw new UnsupportedOperationException();

    }

    public FileData getFileInfoSync(String fullPath) {
        return FileData.create(mEntFileManager.getFileInfo(fullPath));
    }

    /**
     * API签名,SSO签名
     *
     * @param params
     * @return
     */
    protected String generateSignOrderByKey(ArrayList<NameValuePair> params) {
        return generateSignOrderByKey(params, Config.CLIENT_SECRET, true);
    }

    public String generateSignOrderByKey(ArrayList<NameValuePair> params, String secret, boolean needEncode) {
        Collections.sort(params, comparator);
        int size = params.size();
        String string_to_sign = "";

        if (size > 0) {
            for (int i = 0; i < size - 1; i++) {
                string_to_sign += params.get(i).getValue() + "\n";
            }
            string_to_sign += params.get(size - 1).getValue();
        }
        return needEncode ? URLEncoder.encodeUTF8(com.yunkuent.sdk.utils.Util.getHmacSha1(string_to_sign, secret)) : com.yunkuent.sdk.utils.Util.getHmacSha1(string_to_sign, secret);
    }

    private Comparator<NameValuePair> comparator = new Comparator<NameValuePair>() {
        public int compare(NameValuePair p1, NameValuePair p2) {
            return p1.getName().compareTo(p2.getName());
        }
    };


    /**
     * 添加文件
     *
     * @param fullPath
     * @param localPath
     * @param callBack
     * @return
     */
    public UploadRunnable addFile(String fullPath, String localPath, UploadCallBack callBack) {
        if (!Util.isNetworkAvailableEx()) {
            return null;
        }

        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_UPLOAD, fullPath)) {
            callBack.onFail(0, "");
            return null;
        }
        return mEntFileManager.uploadByBlock(fullPath, Config.ORG_OPT_NAME, 0, localPath, true, callBack);
    }

    /**
     * 添加文件夹
     *
     * @param fullPath
     * @param listener
     * @return
     */
    public AsyncTask addDir(final String fullPath, final DataListener listener) {
        if (!Util.isNetworkAvailableEx()) {
            listener.onNetUnable();
            return null;
        }


        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_CREATE_DIR, fullPath)) {
            listener.onHookError(HookCallback.HookType.HOOK_TYPE_CREATE_DIR);
            return null;
        }

        return new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {
                return mEntFileManager.createFolder(fullPath, Config.ORG_OPT_NAME);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                BaseData baseData = BaseData.create(o.toString());
                if (baseData != null) {
                    if (baseData.getCode() == HttpStatus.SC_OK) {
                        listener.onReceiveHttpResponse(ACTION_ID_CREATE_FOLDER);
                    } else {
                        listener.onError(baseData.getErrorMsg());
                    }
                } else {
                    listener.onError(GKApplication.getInstance().getString(R.string.tip_connect_server_failed));
                }
            }
        }.execute();
    }

    /**
     * 删除文件
     *
     * @param fullPath
     * @param listener
     * @return
     */
    public AsyncTask del(final String fullPath, final DataListener listener) {

        if (!Util.isNetworkAvailableEx()) {
            listener.onNetUnable();
            return null;
        }


        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_DELETE, fullPath)) {
            listener.onHookError(HookCallback.HookType.HOOK_TYPE_DELETE);
            return null;
        }

        return new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {
                return mEntFileManager.del(fullPath, Config.ORG_OPT_NAME);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                BaseData baseData = BaseData.create(o.toString());
                if (baseData != null) {
                    if (baseData.getCode() == HttpStatus.SC_OK) {
                        listener.onReceiveHttpResponse(ACTION_ID_DELETE);
                    } else {
                        listener.onError(baseData.getErrorMsg());
                    }
                } else {
                    listener.onError(GKApplication.getInstance().getString(R.string.tip_connect_server_failed));
                }

            }
        }.execute();
    }

    /**
     * 是否存在列表缓存中
     *
     * @param fullPath
     * @return
     */
    public boolean fileExistInCache(String fullPath) {
        String parentPath = Util.getParentPath(fullPath);
        parentPath += (TextUtils.isEmpty(parentPath) ? "" : "/");

        int start = 0;

        while (true) {
            ArrayList<FileData> list = getFilesFromMemory(start, parentPath);
            if (list != null) {
                for (FileData data : list) {
                    if (data.getFullpath().equals(fullPath)) {
                        return true;
                    }
                }
                start += PAGE_SIZE;
            } else {
                break;
            }
        }
        return false;
    }

    public interface DataListener {
        void onReceiveHttpResponse(int actionId);

        void onError(String errorMsg);

        void onHookError(HookCallback.HookType type);

        void onNetUnable();
    }

    public interface FileDataListener extends DataListener {
        void onReceiveCacheData(int start, ArrayList<FileData> list);

        void onReceiveHttpData(ArrayList<FileData> list, int start, String parentPath);

    }


    /**
     * 获取文件列表
     *
     * @param fullPath
     * @param listener
     * @param start
     */
    public void getFileList(final String fullPath, final FileDataListener listener, final int start) {


        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_FILE_LIST, fullPath)) {
            listener.onHookError(HookCallback.HookType.HOOK_TYPE_FILE_LIST);
            return;
        }

        mFileTask = new Thread() {

            @Override
            public void run() {
                ArrayList<FileData> cacheList = getFilesFromPath(start, fullPath);
                listener.onReceiveCacheData(start, cacheList);

                if (!Util.isNetworkAvailableEx()) {
                    listener.onNetUnable();
                } else {
                    String result = mEntFileManager.getFileList(start, fullPath);
                    FileListData fileListData = FileListData.create(result);
                    if (fileListData.getCode() == HttpStatus.SC_OK) {

                        ArrayList<FileData> list = fileListData.getList();

                        listener.onReceiveHttpData(list, start, fullPath);

                        //FIXME 暂时只支持100条内的数据缓存，如果要对100条外缓存，要考虑到100条外缓存数据和网络获取数据的更替
                        if (start == 0) {
                            mFilesMap.put(new FileDataKey(start, fullPath), list);
                        }

                        mFullPath = fullPath;
                        mStart = start;
                    } else {
                        listener.onError(fileListData.getErrorMsg());
                    }
                }
            }
        };

        mFileTask.start();
    }

    /**
     * 获取预览服务器地址
     *
     * @return
     */
    public ServerListData getPreviewServerSite() {
        ServerListData data = ServerListData.create(getServerSite("doc"));
        return data;
    }

    private String getServerSite(String type) {
        return mEntFileManager.getServerSite(type);
    }


    public static final int PAGE_SIZE = 100;//默认列表文件数量

    //文件列表开始位置
    private int mStart = 0;
    private String mFullPath = "";


    public void getListMore(FileDataListener listener) {
        mStart += PAGE_SIZE;
        getFileList(mFullPath, listener, mStart);

    }


    private Thread mFileTask;

    public void cancelFileTask() {
        if (mFileTask != null) {
            mFileTask.interrupt();
            mFileTask = null;
        }
    }

    public ArrayList<FileData> getFilesFromPath(int start, String fullPath) {
        //get cache from memory
        ArrayList<FileData> list = getFilesFromMemory(start, fullPath);
        if (list != null) {
            DebugFlag.log(LOG_TAG, "return from memory cache");
            if (list.size() > 0) {
                if (list.get(0).isHeader()) {
                    list.remove(0);
                }

                int index = list.size() - 1;
                if (list.get(index).isFooter()) {
                    list.remove(index);
                }
            }


            return list;
        }
        return new ArrayList<>();
    }

//    private void filterFiles(ArrayList<FileData> list) {
//        Iterator<FileData> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            FileData fileData = iterator.next();
//            if (fileData.getDir() != FileData.DIRIS && !fileData.isHeader()) {
//                iterator.remove();
//            }
//        }
//    }


    private static final int CACHE_CAPACITY = 64;
    private final LruCache<FileDataKey, ArrayList<FileData>> mFilesMap = new LruCache<>(CACHE_CAPACITY);

    private ArrayList<FileData> getFilesFromMemory(int start, String fullPath) {
        return mFilesMap.get(new FileDataKey(start, fullPath));
    }

    public boolean isRootPath(String fullPath) {
        return fullPath.equals(mRootPath);
    }

    protected static void release() {
        mInstance = null;
    }

}
