package com.gokuai.yunkuandroidsdktest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.PreviewActivity;

/**
 * 调整配置Activity
 */
public class ConfigActivity extends AppCompatActivity {

    public CheckBox mCB_funcDelete;
    public CheckBox mCB_funcRename;
    public CheckBox mCB_funcUpload;

    public EditText mET_hookPath;

    public CheckBox mCB_hookFilelist;
    public CheckBox mCB_hookDownload;
    public CheckBox mCB_hookUpload;
    public CheckBox mCB_hookCreateDir;
    public CheckBox mCB_hookRename;
    public CheckBox mCB_hookDelete;

    public TextView mTv_clientId;
    public TextView mTv_clientSecret;
    public TextView mTv_rootPath;
    public TextView mTv_rootTitle;

    public Button mBtn_startBtn;
    public Button mBtn_previewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        mTv_clientId = (TextView) findViewById(R.id.config_client_id_et);
        mTv_clientSecret = (TextView) findViewById(R.id.config_client_secret_et);
        mTv_rootPath = (TextView) findViewById(R.id.config_root_path_et);
        mTv_rootTitle = (TextView) findViewById(R.id.config_root_title_et);

        mCB_funcDelete = (CheckBox) findViewById(R.id.config_function_delete_cb);
        mCB_funcRename = (CheckBox) findViewById(R.id.config_function_rename_cb);
        mCB_funcUpload = (CheckBox) findViewById(R.id.config_function_upload_cb);

        mET_hookPath = (EditText) findViewById(R.id.config_hook_path_et);

        mCB_hookFilelist = (CheckBox) findViewById(R.id.config_hook_list_cb);
        mCB_hookDownload = (CheckBox) findViewById(R.id.config_hook_download_cb);
        mCB_hookUpload = (CheckBox) findViewById(R.id.config_hook_upload_cb);
        mCB_hookCreateDir = (CheckBox) findViewById(R.id.config_hook_create_dir_cb);
        mCB_hookRename = (CheckBox) findViewById(R.id.config_hook_rename_cb);
        mCB_hookDelete = (CheckBox) findViewById(R.id.config_hook_delete_cb);
        mBtn_startBtn = (Button) findViewById(R.id.config_start_demo_btn);
        mBtn_previewBtn =(Button)findViewById(R.id.config_go_to_preview_btn);

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

                paramData.clientId = mTv_clientId.getText().toString();
                paramData.clientSecret = mTv_clientSecret.getText().toString();
                paramData.rootPath = mTv_rootPath.getText().toString();
                paramData.rootTitle = mTv_rootTitle.getText().toString();
                intent.putExtra("params", paramData);
                startActivity(intent);

                setAllCache();
            }
        });

        mBtn_previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filePath ="[PDF_file_fullpath]";

                Intent previewIntent = new Intent(ConfigActivity.this, PreviewActivity.class);
                previewIntent.putExtra(Constants.EXTRA_OPEN_FILE_URL,filePath);
                startActivity(previewIntent);
            }
        });

    }


    private static final String SP_CACHE_ROOT_DATA = "Cache";
    private static final String SP_CACHE_CLIENTID = "clientId";
    private static final String SP_CACHE_CLIENTSECRET = "clientSecret";
    private static final String SP_CACHE_ROOTPATH = "rootPath";
    private static final String SP_CACHE_ROOTTITLE = "rootTitle";

    private void getAllCache() {
        mTv_clientId.setText(getCache(this, SP_CACHE_CLIENTID));
        mTv_clientSecret.setText(getCache(this, SP_CACHE_CLIENTSECRET));
        mTv_rootPath.setText(getCache(this, SP_CACHE_ROOTPATH));
        mTv_rootTitle.setText(getCache(this, SP_CACHE_ROOTTITLE));
    }

    private void setAllCache() {
        setCache(this, SP_CACHE_CLIENTID, mTv_clientId.getText().toString());
        setCache(this, SP_CACHE_CLIENTSECRET, mTv_clientSecret.getText().toString());
        setCache(this, SP_CACHE_ROOTPATH, mTv_rootPath.getText().toString());
        setCache(this, SP_CACHE_ROOTTITLE, mTv_rootTitle.getText().toString());
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
