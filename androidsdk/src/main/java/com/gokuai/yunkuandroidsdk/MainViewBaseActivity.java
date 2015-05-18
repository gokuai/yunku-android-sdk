package com.gokuai.yunkuandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gokuai.yunkuandroidsdk.data.LocalFileData;
import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Brandon on 15/4/20.
 */
public class MainViewBaseActivity extends BaseActivity {

    private YKMainView mYKMainView;

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        getYKMainView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getYKMainView();

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getYKMainView();

    }

    private void getYKMainView() {
        mYKMainView = (YKMainView) findViewById(R.id.yk_main_view);
        if (mYKMainView != null) {
            mYKMainView.setImageFetcher(getNewImageFetcher());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_CODE_UPLOAD_SUCCESS:
                if (mYKMainView != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        int actionId = data.getIntExtra(Constants.EXTRA_ACTION_ID, -1);
                        switch (actionId) {
                            case Constants.ACTION_ID_REFRESH:
                                mYKMainView.redirectToFile(FileUploadManager.getInstance().getUploadingPath());
                                break;
                        }
                    }
                }

                break;
            case Constants.REQUEST_CODE_TAKE_AUDIO:
                if (mYKMainView != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        FileUploadManager.getInstance().upload(this, mYKMainView.getCurrentPath(), LocalFileData.create(data.getData()));
                        FileUploadManager.getInstance().setUploadCompleteListener(new FileUploadManager.UploadCompleteListener() {
                            @Override
                            public void onComplete() {
                                if (mYKMainView != null) {
                                    mYKMainView.redirectToFile(FileUploadManager.getInstance().getUploadingPath());
                                }
                            }
                        });
                    }
                }

                break;
            case Constants.REQUEST_CODE_TAKE_PIC:
                if (mYKMainView != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        FileUploadManager.getInstance().upload(this, mYKMainView.getCurrentPath(), LocalFileData.create(mYKMainView.getCameraUri()));
                        FileUploadManager.getInstance().setUploadCompleteListener(new FileUploadManager.UploadCompleteListener() {
                            @Override
                            public void onComplete() {
                                if (mYKMainView != null) {
                                    mYKMainView.redirectToFile(FileUploadManager.getInstance().getUploadingPath());
                                }
                            }
                        });
                    }
                }
                break;

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mYKMainView != null) {
            Config.setRootFullPath(this, mYKMainView.getCurrentPath());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileDataManager.getInstance().cancelFileTask();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            if (mYKMainView != null) {
                if (!mYKMainView.isRoot()) {
                    mYKMainView.onBackEvent();
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mYKMainView != null) {
            mYKMainView.onCreateOptionsMenu(getMenuInflater(), menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mYKMainView != null) {
            mYKMainView.onOptionsItemSelected(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
