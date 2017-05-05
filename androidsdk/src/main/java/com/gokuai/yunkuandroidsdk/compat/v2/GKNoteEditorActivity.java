package com.gokuai.yunkuandroidsdk.compat.v2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.gokuai.yunkuandroidsdk.BaseActivity;
import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.UtilOffline;
import com.gokuai.yunkuandroidsdk.compat.v2.dialog.GknoteNameDialogManager;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.LocalFileData;
import com.gokuai.yunkuandroidsdk.dialog.DialogManger;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;
import com.gokuai.yunkuandroidsdk.util.UtilFile;
import com.gokuai.yunkuandroidsdk.webview.WebAppInterface;
import com.gokuai.yunkuandroidsdk.webview.WebViewCreater;

import java.io.File;
import java.io.IOException;

/**
 * Gnote 笔记显示和编辑
 */
public class GKNoteEditorActivity extends BaseActivity implements WebAppInterface.JsReceiver {

    private WebView mWebView;
    private String mRemoteContent = "";//当前内容
    private String mFullPath;
    private Uri mUri;
    private boolean isEditNote;//true 编辑note，false 新建note
    private boolean isBackAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initView();
        initData();

    }

    private AsyncTask mUnZipTask;

    private void initData() {
        mFullPath = Config.getRootFullPath(this);
        isEditNote = getIntent().getBooleanExtra(Constants.GKNOTE_EDIT, false);


        if (isEditNote) {
            FileData data = new FileData();
            data.setLock(0);

            mUri = getIntent().getParcelableExtra(Constants.GKNOTE_URI);
            final String filename = mUri.getLastPathSegment();

            setTitle(filename);

            mUnZipTask = new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPostExecute(String content) {
                    super.onPostExecute(content);
                    if (content != null) {
                        mRemoteContent = content;
                        setEditableWebView();
                    } else {
                        UtilDialog.showNormalToast(R.string.tip_open_file_with_excepiton);
                    }
                }

                @Override
                protected String doInBackground(Void... params) {
                    String unZipPath = UtilOffline.getZipCachePath();

                    try {
                        Util.unzip(mUri.getPath(), unZipPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String indexPath = unZipPath + File.separator + "index.html";
                    return UtilFile.readFileData(indexPath, UtilFile.DEFAUT_CHARSET_ENCODING_FOR_SAVE_DATA);
                }
            }.execute();

        } else {
            setTitle(R.string.notes_upload);
            setEditableWebView();
        }

    }


    private void initView() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.loading_view, null);
        setContentView(view);
    }

    private void setEditableWebView() {
        View contentView = WebViewCreater.getGetGknoteEditorView(this, this);
        mWebView = (WebView) contentView.findViewWithTag("webview");
        setContentView(contentView);
        mWebView.loadUrl("file:///android_asset/ueditor/index.html");
    }


    @Override
    public void send(final String s) {
        if (s.equals("ready")) {

            runOnUiThread(new Runnable() {

                public void run() {
                    //Code that interact with UI
                    mWebView.loadUrl("javascript:setContent('" + mRemoteContent + "');");
                }
            });

        } else {
            if (!isBackAction) {

                if (TextUtils.isEmpty(s)) {
                    UtilDialog.showNormalToast(R.string.tip_content_must_not_be_empty);

                } else {
                    //保存的数据

//                    mRemoteContent = s;

                    if (isEditNote) {
                        saveGkNote(s);
                    } else {
                        new GknoteNameDialogManager(this).showDialog(mFullPath, new DialogManger.DialogActionListener() {
                            @Override
                            public void onDone(String fullPath) {
                                mUri = Uri.fromFile(new File(UtilOffline.getZipCachePath() + Util.getNameFromPath(fullPath)));
                                saveGkNote(s);
                            }
                        });

                    }

                }
            } else {
                boolean contentChanged = !s.equals(mRemoteContent);
                if (contentChanged) {
                    new AlertDialog.Builder(this).setTitle(R.string.tip).setMessage(R.string.tip_content_has_change)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create().show();

                } else {
                    finish();
                }
                isBackAction = false;
            }

        }
    }

    private void saveGkNote(final String content) {
        UtilDialog.showDialogLoading(this, getString(R.string.tip_is_handling), mZipTask);

        mZipTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                String zipPath = UtilOffline.getZipCachePath();
                String indexFile = zipPath + "index.html";
                String resourceFolder = zipPath + "resource";

                //写回到index.html
                boolean success = UtilFile.writeFileData(indexFile, content, UtilFile.DEFAUT_CHARSET_ENCODING_FOR_SAVE_DATA);
                if (success) {
                    try {
                        //压缩原来的openpath
                        Util.zip(new String[]{indexFile, resourceFolder}, mUri.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        success = false;
                    }

                    mRemoteContent = content;

                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                UtilDialog.dismissLoadingDialog(GKNoteEditorActivity.this);
                if (success) {
                    FileUploadManager.getInstance().upload(GKNoteEditorActivity.this, mFullPath, LocalFileData.create(mUri));

                    FileUploadManager.getInstance().setUploadCompleteListener(new FileUploadManager.UploadCompleteListener() {
                        @Override
                        public void onComplete() {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.EXTRA_ACTION_ID, Constants.ACTION_ID_REFRESH);
                            setResult(RESULT_OK, intent);

                            FileUploadManager.getInstance().resetSuccessStatus();
                            finish();
                        }
                    });

                } else {
                    UtilDialog.showTopToast(GKNoteEditorActivity.this, R.string.tip_upload_exception);
                }

            }
        }.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackBtnClick();
            return true;
        } else if (i == R.id.item_save) {
            //在send方法中接受内容数据
            mWebView.loadUrl("javascript:getContent()");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gknote_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        onBackBtnClick();
    }

    protected void onBackBtnClick() {

        isBackAction = true;
        //为了兼容部分机子contentChange事件不触发，只能做内容比较
        if (mWebView != null) {
            mWebView.loadUrl("javascript:getContent()");
        } else {
            finish();
        }

    }


    private AsyncTask mZipTask;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnZipTask != null) {
            mUnZipTask.cancel(true);
        }

        if (mZipTask != null) {
            mZipTask.cancel(true);
        }
    }
}
