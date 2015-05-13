package com.gokuai.yunkuandroidsdk;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.gokuai.yunkuandroidsdk.data.BaseData;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.FileListData;
import com.gokuai.yunkuandroidsdk.exception.GKException;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.yunkuent.sdk.EntFileManager;
import com.yunkuent.sdk.upload.UploadCallBack;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Iterator;

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

    private String mOrgClientId = Config.ORG_CLIENT_ID;
    private String mOrgClientSecret = Config.ORG_CLIENT_SECRET;

    private EntFileManager mEntFileManager;

    private static FileDataManager mInstance;

    private HookCallback mCallback;

    public void registHook(HookCallback calback) {
        mCallback = calback;
    }

    public void unRegistHook() {
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
        if (TextUtils.isEmpty(mOrgClientId) && TextUtils.isEmpty(mOrgClientSecret)) {
            throw new GKException("You need set ORG_CLIENT_ID ORG_CLIENT_SECRET first ");
        }
        mEntFileManager = new EntFileManager(mOrgClientId, mOrgClientSecret);
    }

    public AsyncTask rename(final String fullPath, final String newName, final DataListener listener) {
        if (!Util.isNetworkAvailableEx()) {
            listener.onNetUnable();
            return null;
        }


        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_MOVE, fullPath)) {
            return null;
        }


        return new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {

                String parentPath = Util.getParentPath(fullPath);
                String newPath = parentPath + (TextUtils.isEmpty(parentPath) ? "" : "/") + newName;

                return mEntFileManager.move((int) Util.getUnixDateline(), fullPath,
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
        return null;
    }

    public AsyncTask move(final String fullPath, final String targetPath, final DataListener listener) {
        if (!Util.isNetworkAvailableEx()) {
            listener.onNetUnable();
            return null;
        }


        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_MOVE, fullPath)) {
            return null;
        }


        return new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {
                return mEntFileManager.move((int) Util.getUnixDateline(), fullPath, targetPath, "");
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                BaseData baseData = BaseData.create(o.toString());
                if (baseData != null) {
                    if (baseData.getCode() == HttpStatus.SC_OK) {
                        listener.onReceiveHttpResponse(ACTION_ID_MOVE);
                    } else {
                        listener.onError(baseData.getErrorMsg());
                    }
                } else {
                    listener.onError(GKApplication.getInstance().getString(R.string.tip_connect_server_failed));
                }
            }
        }.execute();

    }

    public FileData getFileInfoSync(String fullPath) {
        return FileData.create(mEntFileManager.getFileInfo((int) Util.getUnixDateline(), fullPath));
    }

    public void addFile(String fullPath, String localPath, UploadCallBack callBack) {
        mEntFileManager.uploadByBlock((int) Util.getUnixDateline(), fullPath, "", 0, localPath, true, callBack);
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
            return null;
        }

        return new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {
                return mEntFileManager.createFolder((int) Util.getUnixDateline(), fullPath, "");
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
            return null;
        }

        return new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {
                return mEntFileManager.del((int) Util.getUnixDateline(), fullPath, "");
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

    public boolean fileExistInCache(String fullPath) {
        ArrayList<FileData> list = getFilesFromMemory(Util.getParentPath(fullPath));
        if (list != null) {
            for (FileData data : list) {
                if (data.getFullpath().equals(fullPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface DataListener {
        void onReceiveHttpResponse(int actionId);

        void onError(String errorMsg);

        void onNetUnable();
    }

    public interface FileDataListener extends DataListener {
        void onReceiveCacheData(ArrayList<FileData> list);

        void onReceiveHttpData(ArrayList<FileData> list, String parentPath);

    }

    /**
     * 获取文件列表
     *
     * @param fullPath
     * @param listener
     * @param dir
     */
    public void getFileList(final String fullPath, final FileDataListener listener, final int dir) {

        if (isHookRegisted() && !mCallback.hookInvoke(HookCallback.HookType.HOOK_TYPE_FILE_LIST, fullPath)) {
            return;
        }

        mFileTask = new Thread() {

            @Override
            public void run() {
                ArrayList<FileData> cacheList = getFilesFromPath(fullPath);
                if (dir == FileData.DIRIS) {
                    filterFiles(cacheList);
                }
                listener.onReceiveCacheData(cacheList);

                if (!Util.isNetworkAvailableEx()) {
                    listener.onNetUnable();
                } else {
                    String result = mEntFileManager.getFileList((int) Util.getUnixDateline(), 0, fullPath);
                    FileListData fileListData = FileListData.create(result);
                    if (fileListData.getCode() == HttpStatus.SC_OK) {
                        ArrayList<FileData> httpList = (ArrayList<FileData>) fileListData.getList().clone();

                        if (dir == FileData.DIRIS) {
                            filterFiles(httpList);
                        }

                        listener.onReceiveHttpData(fileListData.getList(), fullPath);
                        mFilesMap.put(fullPath, fileListData.getList());

                    } else {
                        listener.onError(fileListData.getErrorMsg());
                    }
                }
            }
        };

        mFileTask.start();
    }

    private Thread mFileTask;

    public void cancelFileTask() {
        if (mFileTask != null) {
            mFileTask.interrupt();
            mFileTask = null;
        }
    }

    public ArrayList<FileData> getFilesFromPath(String fullPath) {
        //get cache from memory
        ArrayList<FileData> list = getFilesFromMemory(fullPath);
        if (list != null) {
            DebugFlag.log(LOG_TAG, "return from memory cache");
            if (list.size() > 0) {
                if (list.get(0).isHeader()) {
                    list.remove(0);
                }
            }
            return list;
        }
        return new ArrayList<>();
    }

    private void filterFiles(ArrayList<FileData> list) {
        Iterator<FileData> iterator = list.iterator();
        while (iterator.hasNext()) {
            FileData fileData = iterator.next();
            if (fileData.getDir() != FileData.DIRIS && !fileData.isHeader()) {
                iterator.remove();
            }
        }
    }


    private static final int CACHE_CAPACITY = 64;
    private final LruCache<String, ArrayList<FileData>> mFilesMap = new LruCache<>(CACHE_CAPACITY);

    private ArrayList<FileData> getFilesFromMemory(String fullPath) {
        return mFilesMap.get(fullPath);
    }

    private boolean isRootPath(String fullPath) {
        return fullPath.equals(mRootPath);
    }

}
