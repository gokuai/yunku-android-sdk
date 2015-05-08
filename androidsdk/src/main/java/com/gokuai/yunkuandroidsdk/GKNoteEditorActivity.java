package com.gokuai.yunkuandroidsdk;

import android.app.Dialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;
import com.gokuai.yunkuandroidsdk.util.UtilFile;
import com.gokuai.yunkuandroidsdk.webview.WebAppInterface;
import com.gokuai.yunkuandroidsdk.webview.WebViewCreater;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

/**
 * Created by Brandon on 14/11/3.
 */
public class GKNoteEditorActivity extends ActionBarActivity implements WebAppInterface.JsReceiver {
    @Override
    public void send(String s) {
        Gson gson=new Gson();

    }

//    private WebView mWebView;
//    private String mNoteContent = "";
//    private String mFullPath;
//    private int mMountId;
//    private Uri mUri;
//    private boolean isEditNote;//true 编辑note，false 新建note
//    private boolean isFileWrite;//有上传权限
//    private boolean isBackAction;
//    private String mEditFileName;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getSupportActionBar().show();
//
//        initView();
//        initData();
//
//    }
//
//    private AsyncTask mUnZipTask;
//
//    private void initData() {
//        mFullPath = Config.getRootFullPath(this);
//        mMountId = Config.getRootMountId(this);
//        isEditNote = getIntent().getBooleanExtra(Constants.GKNOTE_EDIT, false);
//
//        //FIXME
////        CompareMount mount = CompareMananger.getMountByMountId(mMountId);
////        isFileWrite = mount.getPropertyData().isFileWrite();
////        if (isFileWrite) {
////            //FIXME
//////            Button rightActionBarBtn = createActionbarRightButton(R.drawable.actionbar_save);
//////            rightActionBarBtn.setOnClickListener(this);
////        }
//
//        if (isEditNote) {
//            FileData data = new FileData();
//            data.setLock(0);
//
//            mUri = getIntent().getParcelableExtra(Constants.GKNOTE_URI);
//            final String filename = mUri.getLastPathSegment();
//
//            setTitle(filename);
//            mEditFileName = filename;
////            HttpEngine.getInstance().lock(mFullPath + mEditFileName, mMountId, data, null);
//
//            mUnZipTask = new AsyncTask<Void, Void, String>() {
//
//                @Override
//                protected void onPostExecute(String content) {
//                    super.onPostExecute(content);
//                    if (content != null) {
//                        mNoteContent = content;
//                        if (isFileWrite) {
//                            setEditableWebView();
//                        } else {
//                            setViewWebview();
//                        }
//                    } else {
//                        UtilDialog.showNormalToast(R.string.tip_open_file_with_excepiton);
//                    }
//                }
//
//                @Override
//                protected String doInBackground(Void... params) {
//                    String unZipPath = UtilOffline.getZipCachePath();
//
//                    try {
//                        Util.unzip(mUri.getPath(), unZipPath);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    String indexPath = unZipPath + File.separator + "index.html";
//                    return UtilFile.readFileData(indexPath, UtilFile.DEFAUT_CHARSET_ENCODING_FOR_SAVE_DATA);
//                }
//            }.execute();
//
//        } else {
//            setTitle(R.string.popupmenu_notes_upload);
//            setEditableWebView();
//        }
//
//    }
//    //FIXME
//
////
////    @Override
////    protected void onBackBtnClick() {
////        if (isFileWrite) {
////            isBackAction = true;
////            //为了兼容部分机子contentChange事件不触发，只能做内容比较
////            mWebView.loadUrl("javascript:getContent()");
////        } else {
////            ////FIXME
//////            super.onBackBtnClick();
////        }
////    }
//
//    private void initView() {
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.loading_view, null);
//        setContentView(view);
//    }
//
//    private void setEditableWebView() {
//        View contentView = WebViewCreater.getGetGknoteEditorView(this, this);
//        mWebView = (WebView) contentView.findViewWithTag("webview");
//        setContentView(contentView);
//        mWebView.loadUrl("file:///android_asset/ueditor/index.html");
//    }
//
//    private void setViewWebview() {
//        View contentView = WebViewCreater.getGetGknoteEditorView(this, this);
//        mWebView = (WebView) contentView.findViewWithTag("webview");
//        setContentView(contentView);
//        mWebView.loadDataWithBaseURL("file:///android_asset/ueditor/", mNoteContent, "text/html", "utf-8", null);
//        mWebView.getSettings().setSupportZoom(true);
//        mWebView.getSettings().setDisplayZoomControls(true);
//        mWebView.getSettings().setBuiltInZoomControls(true);
//    }
//
//    //FIXME
////    @Override
////    public void onClick(View v) {
////        super.onClick(v);
////        switch (v.getId()) {
////            case R.id.actionbar_button_right:
////                //在send方法中接受内容数据
////                mWebView.loadUrl("javascript:getContent()");
////
////                break;
////        }
////    }
//
//    @Override
//    public void send(String s) {
//        if (s.equals("ready")) {
//
//            runOnUiThread(new Runnable() {
//
//                public void run() {
//                    //Code that interact with UI
//                    mWebView.loadUrl("javascript:setContent('" + mNoteContent + "');");
//                }
//            });
//
//        } else {
//            if (!isBackAction) {
//
//                if (TextUtils.isEmpty(s)) {
//                    UtilDialog.showNormalToast(R.string.tip_content_must_not_be_empty);
//
//                } else {
//                    //保存的数据
//
//                    mNoteContent = s;
//
//                    if (isEditNote) {
//                        saveGkNote(true);
//                    } else {
//
//                        final View editView = getLayoutInflater().inflate(R.layout.alert_dialog_edit_with_check, null);
//                        final EditText editText = (EditText) editView.findViewById(R.id.dialog_edit);
//
//                        String hintText = String.format(getString(R.string.gknote_name_format), System.currentTimeMillis());
//
//                        editText.setHint(hintText);
//                        final TextView textView = (TextView) editView.findViewById(R.id.dialog_check);
//
//                        CustomAlertDialogCreater creater = CustomAlertDialogCreater.build(this).
//                                setTitle(getResources().getString(R.string.name_a_file)).setView(editView);
//                        creater.setOnNegativeListener(null)
//                                .setOnPositiveListener(new CustomAlertDialogCreater.DialogBtnListener() {
//                                    @Override
//                                    public void onClick(Dialog dialog) {
//
//                                        String fileName;
//                                        String input = editText.getText().toString();
//                                        if (TextUtils.isEmpty(input)) {
//                                            fileName = editText.getHint().toString();
//                                        } else {
//                                            fileName = editText.getText().toString() + ".gknote";
//                                        }
//
//                                        if (FileDataManager.getInstance().fileExistInCache(mFullPath + fileName, mMountId)) {
//                                            textView.setVisibility(View.VISIBLE);
//                                            textView.setText(R.string.tip_same_file_name_exist);
//                                        } else {
//                                            mUri = Uri.fromFile(new File(UtilOffline.getZipCachePath() + fileName));
//                                            saveGkNote(false);
//                                            dialog.dismiss();
//
//                                        }
//
//                                    }
//                                })
//                                .setAutoDismiss(false);
//                        final Button okBtn = creater.getPositiveButton();
//                        editText.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                boolean isContainSpecial = Util.isContainSpecail(s.toString());
//                                boolean isContainExpression = Util.isContainExpression(s);
//
//                                boolean isValid = Util.isInvaidName(s.toString());
//                                if (isContainSpecial || isContainExpression) {
//                                    textView.setText(R.string.tip_name_contain_special_char);
//                                } else if (isValid) {
//                                    textView.setText(R.string.tip_name_invalid_folder_name);
//                                }
//                                textView.setVisibility(isContainSpecial || isValid || isContainExpression ? View.VISIBLE : View.GONE);
//                                okBtn.setEnabled(!isContainSpecial && !isContainExpression && !isValid);
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable s) {
//
//                            }
//                        });
//                        creater.create().show();
//
//
//                    }
//
//                }
//            } else {
//                boolean contentChanged = !s.equals(mNoteContent);
//                if (contentChanged) {
//                    CustomAlertDialogCreater.build(this)
//                            .setTitle(getString(R.string.tip))
//                            .setMessage(getString(R.string.tip_content_has_change))
//                            .setOnPositiveListener(new CustomAlertDialogCreater.DialogBtnListener() {
//                                @Override
//                                public void onClick(Dialog dialog) {
//                                    //FIXME
////                                    GKNoteEditorActivity.super.onBackBtnClick();
//                                }
//                            }).setOnNegativeListener(null).create().show();
//
//                } else {
//                    //FIXME
////                    super.onBackBtnClick();
//                }
//                isBackAction = false;
//            }
//
//        }
//    }
//
//    private void saveGkNote(final boolean overWrite) {
//        UtilDialog.showDialogLoading(this, getString(R.string.tip_is_handling), mZipTask);
//
//
//        mZipTask = new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                String zipPath = UtilOffline.getZipCachePath();
//                String indexFile = zipPath + "index.html";
//                String resourceFolder = zipPath + "resource";
//
//                //写回到index.html
//                boolean success = UtilFile.writeFileData(indexFile, mNoteContent, UtilFile.DEFAUT_CHARSET_ENCODING_FOR_SAVE_DATA);
//                if (success) {
//                    try {
//                        //压缩原来的openpath
//                        Util.zip(new String[]{indexFile, resourceFolder}, mUri.getPath());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        success = false;
//                    }
//
//                    if (success) {
//                        success = FileDataManager.getInstance().preUploadFile(GKNoteEditorActivity.this, mUri,
//                                mMountId, mFullPath, false, 0, overWrite);
//                    }
//                }
//                return success;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean success) {
//                super.onPostExecute(success);
//                UtilDialog.dismissLoadingDialog(GKNoteEditorActivity.this);
//                if (success) {
//                    //FIXME UPLOAD
//                    FileDataManager.getInstance().addUploadingFiles();
//
////                    Intent intent = new Intent(GKNoteEditorActivity.this, MainViewActivity.class);
////                    intent.putExtra(Constants.EXTRA_KEY_REFRESH_VIEW, true);
////                    intent.putExtra(Constants.EXTRA_REDIRECT_FILE_PATH, mFullPath + UtilFile.getFileName(GKNoteEditorActivity.this, mUri));
////                    startActivity(intent);
////                    finish();
//                } else {
//                    UtilDialog.showTopToast(GKNoteEditorActivity.this, R.string.tip_upload_exception);
//                }
//
//            }
//        }.execute();
//
//    }
//
//    @Override
//    public void finish() {
//        FileData data = new FileData();
//        data.setLock(2);
////        HttpEngine.getInstance().lock(mFullPath + mEditFileName, mMountId, data, null);
//        super.finish();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
////                onBackBtnClick();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onBackPressed() {
////        onBackBtnClick();
//    }
//
//    private AsyncTask mZipTask;
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mUnZipTask != null) {
//            mUnZipTask.cancel(true);
//        }
//
//        if (mZipTask != null) {
//            mZipTask.cancel(true);
//        }
//    }
}
