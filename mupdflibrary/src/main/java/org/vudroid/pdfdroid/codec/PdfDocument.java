package org.vudroid.pdfdroid.codec;

import android.content.Context;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.OutlineActivityData;

import org.vudroid.core.codec.CodecDocument;
import org.vudroid.core.codec.CodecPage;

public class PdfDocument implements CodecDocument {
    MuPDFCore core;

    public void setCore(MuPDFCore core) {
        this.core = core;
        if (null != core) {
            OutlineActivityData.get().items = core.getOutline();
        }
    }

    public MuPDFCore getCore() {
        return core;
    }


    public CodecPage getPage(int pageNumber) {
        //return PdfPage.createPage(docHandle, pageNumber + 1);
        return PdfPage.createPage(core, pageNumber);
    }

    public int getPageCount() {
        //return getPageCount(docHandle);
        return core.countPages();
    }

    static PdfDocument openDocument(Context context, String fname, String pwd) {
        //return new PdfDocument(open(FITZMEMORY, fname, pwd));
        PdfDocument document = new PdfDocument();
        MuPDFCore core = null;
        try {
            core = new MuPDFCore(context, fname);
            // New file: drop the old outline data
            OutlineActivityData.set(null);
            document.setCore(core);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    private static native long open(int fitzmemory, String fname, String pwd);

    private static native void free(long handle);

    private static native int getPageCount(long handle);

    @Override
    protected void finalize() throws Throwable {
        recycle();
        super.finalize();
    }

    public synchronized void recycle() {
        /*if (docHandle != 0) {
            free(docHandle);
            docHandle = 0;
        }*/
        if (null != core) {
            core.onDestroy();
        }
    }
}
