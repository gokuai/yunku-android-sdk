package com.gokuai.yunkuandroidsdk;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Brandon on 15/5/18.
 */
public class BaseActivity extends AppCompatActivity {

    private HashMap<String, ImageFetcher> mMap_ImageFetcher;

    public ImageFetcher getNewImageFetcher() {
        int imageSize = R.dimen.list_item_image_size;
        return getNewImageFetcher("loader", imageSize);
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
    }
}
