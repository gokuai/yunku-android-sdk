package com.gokuai.yunkuandroidsdk;

import android.util.Log;

import com.yunkuent.sdk.DebugConfig;


/**
 * 打印项目日志的类
 */
public class DebugFlag{
    public static boolean IS_DEBUG = true;

    static {
        DebugConfig.PRINT_LOG = true;
        DebugConfig.PRINT_LOG_TYPE = DebugConfig.LOG_TYPE_DETECTOR;

        DebugConfig.setListener(new DebugConfig.LogDetector() {
            @Override
            public void getLog(String s) {
                if (DebugFlag.IS_DEBUG) {
                    Log.v("YunkuJavaSDK", s);
                }
            }
        });
    }

    public static void log(String message) {
        if (DebugFlag.IS_DEBUG) {
            Log.v("YunkuAndroidSDK", message);

        }
    }

    public static void err(String message) {
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
