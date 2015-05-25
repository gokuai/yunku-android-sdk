package com.gokuai.yunkuandroidsdktest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.FileDataManager;
import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.MainViewBaseActivity;
import com.gokuai.yunkuandroidsdk.Option;
import com.gokuai.yunkuandroidsdk.YKMainView;

/**
 * DemoActivity 需要继承 MainViewBaseActivity
 */

public class DemoActivity extends MainViewBaseActivity implements HookCallback {
    ParamData mParamData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mParamData = (ParamData) intent.getSerializableExtra("params");
        setConfigForDebug();

        YKMainView view = new YKMainView(this);

        //设置提供的操作参数
        Option option = new Option();
        option.canDel = mParamData.funcDelete;
        option.canRename = mParamData.funcRename;
        option.canUpload = mParamData.funcUpload;
        view.setOption(option);

        setContentView(view);


        //注册hook控制文件的操作是否可以执行，这个方法需要写在initData()前面
        // ，否则HOOK_TYPE_FILE_LIST对根目录Config.ORG_ROOT_PATH的控制第一次初始化无效
        FileDataManager.getInstance().registerHook(this);

        //初始化界面数据
        view.initData();

    }

    @Override
    public boolean hookInvoke(HookType type, String fullPath) {

        //根据要过滤的类型和路径，匹配不被允许的操作
        if (mParamData.hookPath.equals(fullPath)) {
            boolean access = true;
            switch (type) {
                case HOOK_TYPE_FILE_LIST:
                    access = mParamData.hookList;
                    break;
                case HOOK_TYPE_DOWNLOAD:
                    access = mParamData.hookDownload;
                    break;
                case HOOK_TYPE_UPLOAD:
                    access = mParamData.hookUpload;
                    break;
                case HOOK_TYPE_CREATE_DIR:
                    access = mParamData.hookCreateDir;
                    break;
                case HOOK_TYPE_RENAME:
                    access = mParamData.hookRename;
                    break;
                case HOOK_TYPE_DELETE:
                    access = mParamData.hookDelete;
                    break;
            }

            if (!access) {
                Toast.makeText(DemoActivity.this, "Hook：此操作不被允许", Toast.LENGTH_LONG).show();
            }

            return access;
        }
        Log.i(DemoActivity.class.getSimpleName(), type.toString() + ":" + fullPath);
        return true;
    }


    /**
     * 以下只是做调试用，不建议在这里赋值
     */
    private void setConfigForDebug() {
        if (!TextUtils.isEmpty(mParamData.clientId)) {
            Config.ORG_CLIENT_ID = mParamData.clientId.trim();
        }

        if (!TextUtils.isEmpty(mParamData.clientSecret)) {
            Config.ORG_CLIENT_SECRET = mParamData.clientSecret.trim();
        }

        if (!TextUtils.isEmpty(mParamData.rootPath)) {
            Config.ORG_ROOT_PATH = mParamData.rootPath.trim();
        }

        if (!TextUtils.isEmpty(mParamData.rootTitle)) {
            Config.ORG_ROOT_TITLE = mParamData.rootTitle.trim();
        }

    }
}
