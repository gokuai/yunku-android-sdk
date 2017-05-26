package com.gokuai.yunkuandroidsdk;

import android.content.Context;
import android.content.SharedPreferences;
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

    public final static String WEBVIEW_USERAGENT = "GK_ANDROID;%s;Android";

    public static final String SP_FILE_LIST = "FileList";
    public static final String SP_FILE_LIST_SORT = "ListSort";

    public static final String SP_CACHE_ROOT_DATA = "CacheRootData";
    public static final String SP_CACHE_ROOT_DATA_FULLPATH = "FullPath";

    public static final String SP_CACHE_FOR_SERVER = "server";
    public static final String SP_CACHE_FOR_SERVER_PERVIEW_SITE = "preview_site";
    public static String URL_SOCKET_PREVIEW;//getPreviewSite

    public final static boolean DEBUG_MODE = false;
    public static String CLIENT_SECRET = DEBUG_MODE ? "WTPS0AXco78WWV1IYWvlyrn4P8" : "b99c26032ce5c00cebe74d2cb804db0f";



    /**
     * 设置缓存路径 lib中使用
     * @param context
     */
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


}
