package com.gokuai.yunkuandroidsdk;

import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilFile;
import com.google.gson.Gson;

import java.io.File;

public class UtilOffline {

    private static final String CACHEPATH = "/cache/";
    private static final String CACHEPATH_TEMP = ".temp/";
    private static final String CACHEPATH_ZIP_TEMP = "zips/";
    private static final String CACHE_FILENAME_USERINFO = "userinfo";
    public static final String CACHE_THUMNAIL = ".thumbnail/";
    public static final String CACHE_OPEN_TEMP_PATH = "open_temp_path/";
    public static final String CACHE_FILE = "file";

    /**
     * 获取缓存路径
     *
     * @return
     */
    public static String getCachePath() {
        return Config.getRootPath() + CACHEPATH;
    }

    /**
     * 获取临时缓存文件路径
     *
     * @return
     */
    public static String getCacheTempPath() {
        return Config.getRootPath() + CACHEPATH + CACHEPATH_TEMP;
    }

    public static String getOpenTempPath() {
        return Config.getRootPath() + CACHEPATH + CACHE_OPEN_TEMP_PATH;
    }

    public static String getZipCachePath() {
        return getCacheTempPath() + CACHEPATH_ZIP_TEMP;
    }

    /**
     * 删除缓存
     *
     * @return
     */
    public static void delCache() {
        Util.deleteFile(Config.getRootPath() + CACHEPATH);
    }

    public static void delUserInfo() {
        Util.deleteFile(Config.getRootPath() + File.separator + CACHE_FILENAME_USERINFO);
    }

    /**
     *
     */
    //FIXME
    public static void delUserAppPath() {
        Util.deleteFile(Config.getUserPath());
    }

    /**
     * 对象转化成json格式
     *
     * @param object
     * @return
     */
    public static String objectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }


    /**
     * convert Json to File
     *
     * @param fileName
     * @param data
     */
    private static void convertJsonToFile(String fileName, Object data) {
        String content = objectToJson(data);
        UtilFile.writeFileData(fileName, content, UtilFile.DEFAUT_CHARSET_ENCODING_FOR_SAVE_DATA);
    }

}
