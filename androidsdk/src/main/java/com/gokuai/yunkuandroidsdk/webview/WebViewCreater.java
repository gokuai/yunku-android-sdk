package com.gokuai.yunkuandroidsdk.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.util.Util;

/**
 * 快速获取建议设置好得webview
 */
public class WebViewCreater {

    public static final String LOG_TAG = "WebViewCreater";

    @SuppressLint("SetJavaScriptEnabled")
    public static View getGetGknoteEditorView(final Context context, WebAppInterface.JsReceiver receiver) {
        WebView webView = new WebView(context);
        webView.setTag("webview");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //webview下载
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        webView.addJavascriptInterface(new WebAppInterface(context, receiver), "Android");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(String.format(Config.WEBVIEW_USERAGENT, Util.getVersion(context)));
        webSettings.setJavaScriptEnabled(true);
        return webView;
    }

}
