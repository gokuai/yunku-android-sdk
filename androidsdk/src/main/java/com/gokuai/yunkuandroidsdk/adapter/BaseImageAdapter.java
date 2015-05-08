package com.gokuai.yunkuandroidsdk.adapter;

import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;
import com.gokuai.yunkuandroidsdk.imageutils.Utils;


/**
 * Created by Brandon on 14-3-12.
 */
public abstract class BaseImageAdapter extends BaseAdapter {

    private ImageFetcher mImageFetcher;

    protected void setOnscrollListner(AbsListView listView, ImageFetcher imageFetcher) {
        mImageFetcher = imageFetcher;
        listView.setOnScrollListener(mScrollListener);
    }

    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    if (Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    mImageFetcher.setPauseWork(false);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
        }
    };
}
