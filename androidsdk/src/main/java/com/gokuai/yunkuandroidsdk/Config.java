package com.gokuai.yunkuandroidsdk;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import com.gokuai.yunkuandroidsdk.callback.ParamsCallBack;
import com.gokuai.yunkuandroidsdk.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;


public class Config {

    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();
    public static final String DCIM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    private static String ROOTPATH = SDCARD_PATH + "/gokuaicloud/";
    private static final String TRANSMIT_PATH = "/transmit/";
    private static final String TRANSMIT_IMAGE_PATH = TRANSMIT_PATH + ".image/";
    private static final String TRANSMIT_DOCS_PATH = TRANSMIT_PATH + ".doc/";
    private static final String AVATAR_TEMP_PATH = ".avatar_temp/avatar.jpg";
    private static final String CUSTOMER_DB_PATH = "App_DB/";

    public static String ORG_CLIENT_ID = "";
    public static String ORG_CLIENT_SECRET = "";
    public static String ORG_ROOT_PATH = "";
    public static String ORG_ROOT_TITLE = "";

    public static String URL_SOCKET_PREVIEW;//getPreviewSite
    public static String HTTPREFERER = "www.gokuai.com";

    public final static String WEBVIEW_USERAGENT = "GK_ANDROID;%s;Android";

    public final static String URL_HOST = "yunku.gokuai.com";

    public static final String FILE_THUMBNAIL_FORMAT = "http://" + URL_HOST + "/index/thumb?hash=%s&filehash=%s&type=%s";
    public static final String FILE_BIG_THUMBNAIL_FORMAT = "http://" + URL_HOST + "/index/thumb?hash=%s&filehash=%s&type=%s&big=1";

    private static final String SP_VERSION = "Version";
    private static final String SP_VERSION_KEY_VERSION = "account";

    private static final String SP_LOGININFO = "LoginInfo";
    private static final String SP_LOGININFO_KEY_ID = "id";
    private static final String SP_LOGININFO_KEY_REFRESHTOKEN = "refreshtoken";
    private static final String SP_LOGININFO_KEY_TOKEN = "token";

    public static final String SP_FILE_LIST = "FileList";
    public static final String SP_FILE_LIST_SORT = "ListSort";
    public static final String SP_FILE_CONTACT_PHONE_NUM = "phoneNums";

    public static final String SP_CACHE_ROOT_DATA = "CacheRootData";
    public static final String SP_CACHE_ROOT_DATA_MOUNTID = "MountId";
    public static final String SP_CACHE_ROOT_DATA_ORGID = "OrgId";
    public static final String SP_CACHE_ROOT_DATA_FULLPATH = "FullPath";


    public static final String SP_CACHE_FOR_SERVER = "server";
    public static final String SP_CACHE_FOR_SERVER_PERVIEW_SITE = "preview_site";

    private static int mId = 0;

    public static void release() {
        mId = 0;
    }

    public static int getRootMountId(Context context) {
        SharedPreferences debugPreference = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE);
        return debugPreference.getInt(SP_CACHE_ROOT_DATA_MOUNTID, 0);
    }

    public static void setRootMoundId(Context context, int mountId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE).edit();
        editor.putInt(SP_CACHE_ROOT_DATA_MOUNTID, mountId);
        editor.apply();
    }

    public static int getRootOrgId(Context context) {
        SharedPreferences debugPreference = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE);
        return debugPreference.getInt(SP_CACHE_ROOT_DATA_ORGID, 0);
    }

    public static void setRootOrgId(Context context, int orgId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE).edit();
        editor.putInt(SP_CACHE_ROOT_DATA_ORGID, orgId);
        editor.apply();
    }

    public static String getRootFullPath(Context context) {
        SharedPreferences debugPreference = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE);
        return debugPreference.getString(SP_CACHE_ROOT_DATA_FULLPATH, "");
    }

    public static void setRootFullPath(Context context, String fullPath) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE).edit();
        editor.putString(SP_CACHE_ROOT_DATA_FULLPATH, fullPath);
        editor.apply();

    }

    public static void saveListSortType(Context context, int sortType) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_FILE_LIST, Context.MODE_PRIVATE).edit();
        editor.putInt(SP_FILE_LIST_SORT, sortType);
        editor.apply();
    }

    public static int getListSortType(Context context) {
        SharedPreferences debugPreference = context.getSharedPreferences(
                SP_FILE_LIST, Context.MODE_PRIVATE);
        return debugPreference.getInt(SP_FILE_LIST_SORT, 0);
    }


    public static String getAvatarPath() {
        return getRootPath() + "/" + AVATAR_TEMP_PATH;
    }

    public static void saveVersion(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_VERSION, Context.MODE_PRIVATE).edit();
        editor.putString(SP_VERSION_KEY_VERSION, Util.getVersion(context));
        editor.apply();
    }

    public static String getVersion(Context context) {
        SharedPreferences loginPreference = context.getSharedPreferences(
                SP_VERSION, Context.MODE_PRIVATE);
        return loginPreference.getString(SP_VERSION_KEY_VERSION, "");
    }


    public static void saveToken(String token, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_LOGININFO, Context.MODE_PRIVATE).edit();

        editor.putString(SP_LOGININFO_KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences loginPreference = context.getSharedPreferences(
                SP_LOGININFO, Context.MODE_PRIVATE);
        return loginPreference.getString(SP_LOGININFO_KEY_TOKEN, null);
    }


    public static String getRefreshToken(Context context) {
        SharedPreferences loginPreference = context.getSharedPreferences(
                SP_LOGININFO, Context.MODE_PRIVATE);
        return loginPreference.getString(SP_LOGININFO_KEY_REFRESHTOKEN, null);

    }

    public static void saveRefreshToken(Context context, String refreshToken) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_LOGININFO, Context.MODE_PRIVATE).edit();

        editor.putString(SP_LOGININFO_KEY_REFRESHTOKEN, refreshToken);
        editor.apply();
    }


    public static String getApplicationPath(Context context) {
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return s;
    }

    /**
     * 自定义数据库路径
     *
     * @return
     */
    public static String getCustomerDbPath() {

        String path = getApplicationPath(GKApplication.getInstance()) + "/" + CUSTOMER_DB_PATH;
        File file = new File(path);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 用户路径
     *
     * @return
     */
    public static String getUserPath() {
        if (mId == 0) {
            getAccountId(GKApplication.getInstance());
        }
        String path = getCustomerDbPath() + mId + "/";
        File file = new File(path);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 对应mount的路径
     *
     * @param mountID
     * @return
     */
    public static String getMountDBPath(int mountID) {
        return getUserPath() + mountID + "/";
    }


    public synchronized static void saveAccountId(Context context, int id) {

        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_LOGININFO, Context.MODE_PRIVATE).edit();
        editor.putInt(SP_LOGININFO_KEY_ID, id);
        editor.apply();
    }

    public synchronized static int getAccountId(Context context) {
        SharedPreferences preference = context.getSharedPreferences(SP_LOGININFO, Context.MODE_PRIVATE);
        mId = preference.getInt(SP_LOGININFO_KEY_ID, 0);
        return mId;
    }


    /**
     * 获得本地文件跟目录
     *
     * @return
     */
    public static String getRootPath() {
        if (mId == 0) {
            getAccountId(GKApplication.getInstance());
        }

        return ROOTPATH + mId;
    }


    public static String getImagesPath() {
        return ROOTPATH + TRANSMIT_IMAGE_PATH;
    }

    public static String getDocsPath() {
        return ROOTPATH + TRANSMIT_DOCS_PATH;
    }

    public static String getLocalFilePath(String filehash) {
        return Config.getOfflineFilePath() + filehash;
    }

    public static String getPdfFilePath(String filehash) {
        return getLocalFilePath(filehash) + "_pdf";
    }

    public static String getBigThumbPath(String filehash) {
        return getLocalFilePath(filehash) + "_thumbnail";
    }

    public static String getOfflineFilePath() {
        return Config.getRootPath()
                + File.separator
                + UtilOffline.CACHE_FILE + File.separator;
    }


    public static String getPreviewSite(Context context) {
        SharedPreferences debugPreference = context.getSharedPreferences(
                SP_CACHE_FOR_SERVER, Context.MODE_PRIVATE);
        return debugPreference.getString(SP_CACHE_FOR_SERVER_PERVIEW_SITE, "");
    }

    public static void setPreviewSite(Context context, String site) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_CACHE_FOR_SERVER, Context.MODE_PRIVATE).edit();
        editor.putString(SP_CACHE_FOR_SERVER_PERVIEW_SITE, site);
        editor.apply();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static ArrayList<String> getPhoneNumList(Context context) {
        if (mId == 0) {
            getAccountId(context);
        }
        SharedPreferences preference = context.getSharedPreferences(mId
                + SP_FILE_CONTACT_PHONE_NUM, Context.MODE_PRIVATE);
        ArrayList<String> list = new ArrayList<String>();
        Set<String> phoneNums;
        if ((phoneNums = preference.getStringSet(SP_FILE_CONTACT_PHONE_NUM, null)) != null)
            list.addAll(phoneNums);
        return list;
    }

    public static void getApplicationCache(final ParamsCallBack paramsCallBack) {
        new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                String fileCachePath = Config.getOfflineFilePath();

                String fileThumbNailPath = UtilOffline.getCachePath() + UtilOffline.CACHE_THUMNAIL;

                String fileTempCachePath = UtilOffline.getCacheTempPath();

                long fileSize = Util.getFolderSize(new File(fileCachePath));
                fileSize += Util.getFolderSize(new File(fileThumbNailPath));
                fileSize += Util.getFolderSize(new File(fileTempCachePath));
                return fileSize;
            }

            @Override
            protected void onPostExecute(Object aVoid) {
                super.onPostExecute(aVoid);
                if (paramsCallBack != null) {
                    paramsCallBack.callBack(aVoid);
                }
            }
        }.execute();

    }

}
