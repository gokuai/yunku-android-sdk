package org.vudroid.pdfdroid.codec;

import android.content.ContentResolver;
import android.content.Context;

import org.vudroid.core.codec.CodecContext;
import org.vudroid.core.codec.CodecDocument;

public class PdfContext implements CodecContext {

    public CodecDocument openDocument(Context context, String fileName) {
        return PdfDocument.openDocument(context, fileName, "");
    }

    public void setContentResolver(ContentResolver contentResolver) {
        //TODO
    }

    public void recycle() {
    }
}
