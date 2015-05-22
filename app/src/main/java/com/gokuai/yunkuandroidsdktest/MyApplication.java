package com.gokuai.yunkuandroidsdktest;

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.GKApplication;

/**
 * 需要继承GKApplication
 */
public class MyApplication extends GKApplication {

    //======================== 这部分需要预先设置==========================
    static {

        Config.ORG_CLIENT_ID = "[预先申请的CLIENT_ID]";
        Config.ORG_CLIENT_SECRET = "[预先申请的CLIENT_SECRET]";

        Config.ORG_ROOT_PATH = "";//访问文件的根目录
        Config.ORG_ROOT_TITLE = "MyTitle";//根目录
        Config.ORG_OPT_NAME = "Brandon";//操作人，例如文件上传、改名、删除等，
    }

    //===================================================================



    //在这里添加自己要执行的代码

}
