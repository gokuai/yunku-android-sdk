package com.gokuai.yunkuandroidsdk.webview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.gokuai.yunkuandroidsdk.DebugFlag;


public  class WebAppInterface {
        public interface JsReceiver {
            public void send(String s);
        }

        private JsReceiver mJsReceiver;

        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        public WebAppInterface(Context c, JsReceiver receiver) {
            mJsReceiver = receiver;
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void send(String string) {
            mJsReceiver.send(string);
        }

        @JavascriptInterface
        public void processHTML(String html) {
            // process the html as needed by the app
            DebugFlag.log("BaseWebAtivity", html);
        }

    }