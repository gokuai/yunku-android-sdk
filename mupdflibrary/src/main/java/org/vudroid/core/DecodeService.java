package org.vudroid.core;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;

public interface DecodeService
{
    void setContentResolver(ContentResolver contentResolver);

    void setContainerView(View containerView);

    void open(Context context, Uri fileUri);

    void decodePage(Object decodeKey, int pageNum, DecodeCallback decodeCallback, float zoom, RectF pageSliceBounds);

    void stopDecoding(Object decodeKey);

    int getEffectivePagesWidth(int pageIndex);

    int getEffectivePagesHeight(int pageIndex);

    int getPageCount();

    int getPageWidth(int pageIndex);

    int getPageHeight(int pageIndex);

    void recycle();

    public interface DecodeCallback
    {
        void decodeComplete(Bitmap bitmap);
    }
}
