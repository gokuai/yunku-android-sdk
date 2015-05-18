package com.gokuai.yunkuandroidsdktest;

import android.os.Bundle;

import com.gokuai.yunkuandroidsdk.MainViewBaseActivity;
import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.DebugFlag;
import com.gokuai.yunkuandroidsdk.FileDataManager;
import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.Option;
import com.gokuai.yunkuandroidsdk.YKMainView;

public class MainActivity extends MainViewBaseActivity implements HookCallback {

    /**
     *
     */
    //======================== 这部分需要预先设置==========================
    static {

        Config.ORG_CLIENT_ID = "294925cc5b65f075677a3227141b9467";
        Config.ORG_CLIENT_SECRET = "e195dbb3f9c263890a269010f18bea50";

        Config.ORG_ROOT_PATH = "test/";//访问文件的根目录
        Config.ORG_ROOT_TITLE = "MyTitle";//根目录
        Config.ORG_OPT_NAME = "Brandon";//操作人，例如文件上传、改名、删除等
    }

    //===================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YKMainView view = new YKMainView(this);

        Option option = new Option();
        option.canDel = true;
        option.canRename = true;
        option.canUpload = true;

        view.setOption(option);
        setContentView(view);

        view.initData();

        FileDataManager.getInstance().registerHook(this);
    }

    @Override
    public boolean hookInvoke(HookType type, String... params) {
        String paramsString = "";
        for (String str : params) {
            paramsString += str;
        }
        DebugFlag.log(type.toString() + ":" + paramsString);
        return true;
    }
}
