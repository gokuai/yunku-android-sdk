package org.vudroid.core.codec;

import android.content.ContentResolver;
import android.content.Context;

public interface CodecContext {
    CodecDocument openDocument(Context context, String fileName);

    void setContentResolver(ContentResolver contentResolver);

    void recycle();
}
