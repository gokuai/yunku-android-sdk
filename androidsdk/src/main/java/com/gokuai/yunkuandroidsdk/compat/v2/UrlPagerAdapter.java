/*
 Copyright (c) 2013 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gokuai.yunkuandroidsdk.compat.v2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.gallery.touchwedgit.GalleryViewPager;

import java.util.ArrayList;


/**
 * Class wraps URLs to adapter, then it instantiates {@link UrlTouchImageView}
 * objects to paging up through them.
 */
public class UrlPagerAdapter extends BasePagerAdapter {

    private View.OnClickListener mListener;


    public UrlPagerAdapter(Context context, ArrayList<FileData> resources, int index, int galleryMode, View.OnClickListener clickListener) {
        super(context, resources, index);
        mGalleryMode = galleryMode;
        mListener = clickListener;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ((GalleryViewPager) container).mCurrentView = ((UrlTouchImageView) object).getImageView();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        final UrlTouchImageView iv = new UrlTouchImageView(mContext, mListener);
        iv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setTag("iv" + position);
        collection.addView(iv);
        return iv;
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }

}
