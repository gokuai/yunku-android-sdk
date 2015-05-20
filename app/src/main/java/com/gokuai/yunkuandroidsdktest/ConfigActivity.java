package com.gokuai.yunkuandroidsdktest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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

    public Button mCB_startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

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
        mCB_startBtn = (Button) findViewById(R.id.config_start_demo_btn);

        mCB_startBtn.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("params",paramData);
                startActivity(intent);
            }
        });

    }


}
