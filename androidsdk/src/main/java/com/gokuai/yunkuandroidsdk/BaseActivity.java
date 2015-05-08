package com.gokuai.yunkuandroidsdk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Brandon on 15/4/20.
 */
public class BaseActivity extends AppCompatActivity {

    private HashMap<String, ImageFetcher> mMap_ImageFetcher;
    private YKMainView mYKMainView;

    public ImageFetcher getNewImageFetcher() {
        int imageSize = R.dimen.list_item_image_size;
        return getNewImageFetcher("loader", imageSize);
    }

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
    }

    public ImageFetcher getNewImageFetcherOnSize(Context context, int imageSize) {
        ImageFetcher imageFetcher = new ImageFetcher(context, imageSize);
        String cachePath = UtilOffline.CACHE_THUMNAIL;
        imageFetcher.addImageCache(this, cachePath);
        if (mMap_ImageFetcher == null) {
            mMap_ImageFetcher = new HashMap<String, ImageFetcher>();
        }
        mMap_ImageFetcher.put("loader", imageFetcher);
        return imageFetcher;
    }

    public ImageFetcher getNewImageFetcher(String fundesc, int sizeRes) {
        ImageFetcher imageFetcher = new ImageFetcher(this, getResources().getDimensionPixelSize(sizeRes));
        String cachePath = UtilOffline.CACHE_THUMNAIL;
        imageFetcher.addImageCache(this, cachePath);
        if (mMap_ImageFetcher == null) {
            mMap_ImageFetcher = new HashMap<String, ImageFetcher>();
        }
        mMap_ImageFetcher.put(fundesc, imageFetcher);
        return imageFetcher;
    }


    private void imageFetcherActionOnDestroy() {
        if (mMap_ImageFetcher != null) {
            Iterator iter = mMap_ImageFetcher.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                ImageFetcher imageFetcher = (ImageFetcher) entry.getValue();
                imageFetcher.closeCache();
                iter.remove();
            }
            mMap_ImageFetcher = null;
        }


    }

    private void imageFetcherExitTaskEarly(boolean exit) {
        if (mMap_ImageFetcher != null) {
            Iterator iter = mMap_ImageFetcher.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                ImageFetcher imageFetcher = (ImageFetcher) entry.getValue();
                imageFetcher.setExitTasksEarly(exit);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        imageFetcherExitTaskEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageFetcherExitTaskEarly(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageFetcherActionOnDestroy();
        FileDataManager.getInstance().cancelFileTask();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            if(mYKMainView!=null){
                if(!mYKMainView.isRoot()){
                    mYKMainView.onBackEvent();
                }else{
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
