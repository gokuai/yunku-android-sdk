package com.gokuai.yunkuandroidsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;


public class Config {

    //======================================
    /**
     * those params will be written after application start
     */
    public static String CACHE_PATH = "";

    //======================================

    /**
     * those params will should be set by user
     */
    public static String ORG_CLIENT_ID = "";
    public static String ORG_CLIENT_SECRET = "";
    public static String ORG_ROOT_PATH = "";
    public static String ORG_ROOT_TITLE = "";
    public static String ORG_OPT_NAME = "";

    //======================================

    public static final String DCIM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();

    private static final String CUSTOMER_DB_PATH = "App_DB/";

    public static String URL_SOCKET_PREVIEW;//getPreviewSite
    public static String HTTPREFERER = "www.gokuai.com";

    public final static String WEBVIEW_USERAGENT = "GK_ANDROID;%s;Android";

    public final static String URL_HOST = "yunku.gokuai.com";

    public static final String FILE_THUMBNAIL_FORMAT = "http://" + URL_HOST + "/index/thumb?hash=%s&filehash=%s&type=%s";
    public static final String FILE_BIG_THUMBNAIL_FORMAT = "http://" + URL_HOST + "/index/thumb?hash=%s&filehash=%s&type=%s&big=1";

    public static final String SP_FILE_LIST = "FileList";
    public static final String SP_FILE_LIST_SORT = "ListSort";

    public static final String SP_CACHE_ROOT_DATA = "CacheRootData";
    public static final String SP_CACHE_ROOT_DATA_ORGID = "OrgId";
    public static final String SP_CACHE_ROOT_DATA_FULLPATH = "FullPath";


    public static final String SP_CACHE_FOR_SERVER = "server";
    public static final String SP_CACHE_FOR_SERVER_PERVIEW_SITE = "preview_site";

    public static void setCachePath(Context context) {
        String path = "";
        File file = context.getExternalCacheDir();
        if (file != null) {
            path = file.getPath();
        }
        if (TextUtils.isEmpty(path)) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
        Config.CACHE_PATH = path;
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
        String path = getCustomerDbPath();
        File file = new File(path);
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        return path;
    }


    /**
     * 获得本地文件跟目录
     *
     * @return
     */
    public static String getRootPath() {
        return CACHE_PATH;
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


}
