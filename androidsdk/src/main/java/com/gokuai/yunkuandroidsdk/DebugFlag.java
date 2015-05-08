package com.gokuai.yunkuandroidsdk;

import android.util.Log;

/**
 * 打印项目日志的类
 */
public class DebugFlag {
    public static boolean IS_DEBUG = true;


    public static void log(String message) {
        if (DebugFlag.IS_DEBUG) {
            Log.v("YunkuAndroidSDK", message);

        }
    }

    public static void err(String message){
        if (DebugFlag.IS_DEBUG) {
            Log.e("YunkuAndroidSDK", message);

        }
    }

    public static void log(String tag, String message) {
        if (DebugFlag.IS_DEBUG) {
            Log.v("YunkuAndroidSDK", tag + ":" + message);

        }
    }

}
