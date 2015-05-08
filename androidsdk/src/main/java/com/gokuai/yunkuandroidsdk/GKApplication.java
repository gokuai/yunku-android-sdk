package com.gokuai.yunkuandroidsdk;

import android.app.Application;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.Message;
import android.webkit.MimeTypeMap;

import com.gokuai.yunkuandroidsdk.mime.MimeTypeParser;
import com.gokuai.yunkuandroidsdk.mime.MimeTypes;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;
import com.gokuai.yunkuandroidsdk.util.UtilFile;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Brandon on 15/3/31.
 */
public class GKApplication extends Application {

    private final static String LOG_TAG = "CustomApplication";

    protected static GKApplication instance;
    private GKApplicationStatusListener mGkStatusListener;


    private MimeTypes mMimeTypes;


    public interface GKApplicationStatusListener {
        public void statChanged(boolean isVisible);
    }

    public boolean isActivityVisible() {
        return activityVisible;
    }


    public void activityResumed() {
        DebugFlag.log(LOG_TAG, "activityResumed");
        activityVisible = true;
        if (mGkStatusListener != null) {
            mGkStatusListener.statChanged(true);
        }

    }

    public void activityPaused() {
        DebugFlag.log(LOG_TAG, "activityPaused");
        activityVisible = false;
        if (mGkStatusListener != null) {
            mGkStatusListener.statChanged(false);
        }

    }

    public void setStatusListener(GKApplicationStatusListener listener) {
        mGkStatusListener = listener;
    }

    private boolean activityVisible;

    public static GKApplication getInstance() {
        return instance;
    }

    public GKApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }


    public String getFileMimeType(String filename) {
        MimeTypes mimeType = getMimeTypes();
        String type = null;
        if (mimeType != null) {
            type = mimeType.getMimeType(filename);
        }
        if (type == null) {
            filename = Util.getNameFromPath(filename).replace("/", "");
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(UtilFile.getExtension(filename));
        }

        if (type == null) {
            type = "*/*";
        }
        return type;
    }

    public MimeTypes getMimeTypes() {
        if (mMimeTypes == null) {
            MimeTypeParser mtp = new MimeTypeParser();
            XmlResourceParser in = getResources().getXml(R.xml.mimetypes);

            try {
                mMimeTypes = mtp.fromXmlResource(in);
            } catch (XmlPullParserException e) {

                throw new RuntimeException("PreselectedChannelsActivity: XmlPullParserException");
            } catch (IOException e) {

                throw new RuntimeException("PreselectedChannelsActivity: IOException");
            }
        }

        return mMimeTypes;
    }

    public final static int MSG_CROSS_THREAD_TOAST = 2;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CROSS_THREAD_TOAST:
                    UtilDialog.showNormalToast(msg.obj.toString());
                    break;
            }
        }
    };

    public Handler getHandler() {
        return mHandler;
    }


}
