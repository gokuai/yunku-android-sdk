package com.gokuai.yunkuandroidsdktest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.PreviewActivity;
import com.gokuai.yunkuandroidsdk.PreviewInfoManager;

/**
 * 调整配置Activity
 */
public class ConfigActivity extends AppCompatActivity {

    public CheckBox mCB_funcDelete;
    public CheckBox mCB_funcRename;
    public CheckBox mCB_funcUpload;

    public EditText mET_hookPath;
    public EditText mET_previewPath;
    public EditText mEt_clientId;
    public EditText mEt_clientSecret;
    public EditText mEt_rootPath;
    public EditText mEt_rootTitle;

    public CheckBox mCB_hookFilelist;
    public CheckBox mCB_hookDownload;
    public CheckBox mCB_hookUpload;
    public CheckBox mCB_hookCreateDir;
    public CheckBox mCB_hookRename;

    public CheckBox mCB_hookDelete;
    public TextView mTv_previewPath;

    public Button mBtn_startBtn;
    public Button mBtn_previewBtn;
    public Button mBtn_getPDFUrlBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        mTv_previewPath = (TextView) findViewById(R.id.config_preview_pdf_download_url_tv);

        mCB_funcDelete = (CheckBox) findViewById(R.id.config_function_delete_cb);
        mCB_funcRename = (CheckBox) findViewById(R.id.config_function_rename_cb);
        mCB_funcUpload = (CheckBox) findViewById(R.id.config_function_upload_cb);

        mEt_clientId = (EditText) findViewById(R.id.config_client_id_et);
        mEt_clientSecret = (EditText) findViewById(R.id.config_client_secret_et);
        mEt_rootPath = (EditText) findViewById(R.id.config_root_path_et);
        mEt_rootTitle = (EditText) findViewById(R.id.config_root_title_et);
        mET_hookPath = (EditText) findViewById(R.id.config_hook_path_et);
        mET_previewPath = (EditText) findViewById(R.id.config_preview_path_et);

        mCB_hookFilelist = (CheckBox) findViewById(R.id.config_hook_list_cb);
        mCB_hookDownload = (CheckBox) findViewById(R.id.config_hook_download_cb);
        mCB_hookUpload = (CheckBox) findViewById(R.id.config_hook_upload_cb);
        mCB_hookCreateDir = (CheckBox) findViewById(R.id.config_hook_create_dir_cb);
        mCB_hookRename = (CheckBox) findViewById(R.id.config_hook_rename_cb);
        mCB_hookDelete = (CheckBox) findViewById(R.id.config_hook_delete_cb);
        mBtn_startBtn = (Button) findViewById(R.id.config_start_demo_btn);
        mBtn_previewBtn = (Button) findViewById(R.id.config_go_to_preview_btn);
        mBtn_getPDFUrlBtn = (Button) findViewById(R.id.config_get_pdf_path_btn);

        getAllCache();

        mBtn_startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigActivity.this, DemoActivity.class);
                ParamData paramData = new ParamData();
                paramData.funcDelete = mCB_funcDelete.isChecked();
                paramData.funcRename = mCB_funcRename.isChecked();
                paramData.funcUpload = mCB_funcUpload.isChecked();
                paramData.hookPath = mET_hookPath.getText().toString();

                paramData.hookDelete = mCB_hookDelete.isChecked();
                paramData.hookDownload = mCB_hookDownload.isChecked();
                paramData.hookUpload = mCB_hookUpload.isChecked();
                paramData.hookCreateDir = mCB_hookCreateDir.isChecked();
                paramData.hookRename = mCB_hookRename.isChecked();
                paramData.hookList = mCB_hookFilelist.isChecked();

                paramData.clientId = mEt_clientId.getText().toString();
                paramData.clientSecret = mEt_clientSecret.getText().toString();
                paramData.rootPath = mEt_rootPath.getText().toString();
                paramData.rootTitle = mEt_rootTitle.getText().toString();
                intent.putExtra("params", paramData);
                startActivity(intent);

                setAllCache();
            }
        });

        mBtn_previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开本地路径的pdf文件

                String filePath = "[PDF_file_fullpath]";

                Intent previewIntent = new Intent(ConfigActivity.this, PreviewActivity.class);
                previewIntent.putExtra(Constants.EXTRA_OPEN_FILE_URL, filePath);
                startActivity(previewIntent);
            }
        });

        mBtn_getPDFUrlBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //获取转化文档pdf的下载地址

                //===================测试代码 不建议这么赋值===================
                String clientId = mEt_clientId.getText().toString().trim();
                String clientSecret = mEt_clientSecret.getText().toString().trim();

                if (!TextUtils.isEmpty(clientId)) {
                    Config.ORG_CLIENT_ID = clientId;
                }

                if (!TextUtils.isEmpty(clientSecret)) {
                    Config.ORG_CLIENT_SECRET = clientSecret;
                }
                //===========================================================


                String fullPath = mET_previewPath.getText().toString();
                PreviewInfoManager.getInstance().getPreviewInfo(ConfigActivity.this, fullPath, new PreviewInfoManager.PreviewInfoListener() {


                    @Override
                    public void onStatus(String fullPath, int status) {
                        switch (status){
                            case STATUS_CODE_ANALYZE_SERVER:
                                mTv_previewPath.setText("start get preview server");
                                break;
                            case STATUS_CODE_START_TO_CONVERT_PDF:
                                mTv_previewPath.setText("start to convert pdf");
                                break;
                            case STATUS_CODE_COMPLETE:
                                mTv_previewPath.setText("compelete");
                                break;
                        }

                    }

                    @Override
                    public void onProgress(int percent) {
                        mTv_previewPath.setText("percent:" + percent);

                    }

                    @Override
                    public void onError(int errorCode, String fullPath, String message) {
                        mTv_previewPath.setText(message);
                    }

                    @Override
                    public void onGetPreviewInfo(String fullPath, String url) {
                        mTv_previewPath.setText(url);
                    }

                });

            }
        });

    }


    private static final String SP_CACHE_ROOT_DATA = "Cache";
    private static final String SP_CACHE_CLIENTID = "clientId";
    private static final String SP_CACHE_CLIENTSECRET = "clientSecret";
    private static final String SP_CACHE_ROOTPATH = "rootPath";
    private static final String SP_CACHE_ROOTTITLE = "rootTitle";

    private void getAllCache() {
        mEt_clientId.setText(getCache(this, SP_CACHE_CLIENTID));
        mEt_clientSecret.setText(getCache(this, SP_CACHE_CLIENTSECRET));
        mEt_rootPath.setText(getCache(this, SP_CACHE_ROOTPATH));
        mEt_rootTitle.setText(getCache(this, SP_CACHE_ROOTTITLE));
    }

    private void setAllCache() {
        setCache(this, SP_CACHE_CLIENTID, mEt_clientId.getText().toString());
        setCache(this, SP_CACHE_CLIENTSECRET, mEt_clientSecret.getText().toString());
        setCache(this, SP_CACHE_ROOTPATH, mEt_rootPath.getText().toString());
        setCache(this, SP_CACHE_ROOTTITLE, mEt_rootTitle.getText().toString());
    }


    private static String getCache(Context context, String key) {
        SharedPreferences debugPreference = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE);
        return debugPreference.getString(key, "");
    }

    private static void setCache(Context context, String key, String mountId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_CACHE_ROOT_DATA, Context.MODE_PRIVATE).edit();
        editor.putString(key, mountId);
        editor.apply();
    }


}
