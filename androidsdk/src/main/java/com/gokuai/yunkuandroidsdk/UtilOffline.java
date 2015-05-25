package com.gokuai.yunkuandroidsdk;

import com.gokuai.yunkuandroidsdk.util.UtilFile;
import com.google.gson.Gson;

public class UtilOffline {

    private static final String CACHEPATH_TEMP = ".temp/";
    private static final String CACHEPATH_ZIP_TEMP = "zips/";
    public static final String CACHE_THUMNAIL = ".thumbnail/";
    public static final String CACHE_OPEN_TEMP_PATH = "open_temp_path/";
    public static final String CACHE_FILE = "file";

    /**
     * 获取临时缓存文件路径
     *
     * @return
     */
    public static String getCacheTempPath() {
        return Config.getRootPath() + CACHEPATH_TEMP;
    }

    public static String getOpenTempPath() {
        return Config.getRootPath() + CACHE_OPEN_TEMP_PATH;
    }

    public static String getZipCachePath() {
        return getCacheTempPath() + CACHEPATH_ZIP_TEMP;
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
