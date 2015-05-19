package com.gokuai.yunkuandroidsdktest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gokuai.yunkuandroidsdk.FileDataManager;
import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.MainViewBaseActivity;
import com.gokuai.yunkuandroidsdk.Option;
import com.gokuai.yunkuandroidsdk.YKMainView;

/**
 * MainActivity 需要继承 MainViewBaseActivity
 */

public class DemoActivity extends MainViewBaseActivity implements HookCallback {
    ParamData mParamData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mParamData = (ParamData) intent.getSerializableExtra("params");
        YKMainView view = new YKMainView(this);

        //设置提供的操作参数
        Option option = new Option();
        option.canDel = mParamData.funcDelete;
        option.canRename = mParamData.funcRename;
        option.canUpload = mParamData.funcUpload;
        view.setOption(option);

        setContentView(view);

        //初始化界面数据
        view.initData();

        //注册hook控制文件的操作是否可以执行
        FileDataManager.getInstance().registerHook(this);
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
                Toast.makeText(DemoActivity.this,"Hook：此操作不被允许",Toast.LENGTH_LONG).show();
            }

            return access;
        }
        Log.i(DemoActivity.class.getSimpleName(), type.toString() + ":" + fullPath);
        return true;
    }
}
