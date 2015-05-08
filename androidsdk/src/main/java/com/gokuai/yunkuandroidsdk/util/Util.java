package com.gokuai.yunkuandroidsdk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gokuai.yunkuandroidsdk.Author;
import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.GKApplication;
import com.gokuai.yunkuandroidsdk.GKNoteEditorActivity;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.UtilOffline;
import com.gokuai.yunkuandroidsdk.exception.FileOperationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class Util {

    /**
     * 获取系统的json格式信息
     *
     * @return
     */
    public static String osInfo() {
        JSONObject osinfoJson = new JSONObject();
        try {
            osinfoJson.put("DISPLAY", "Android");
            osinfoJson.put("BRAND", Build.BRAND);
            osinfoJson.put("VERSION", Build.VERSION.RELEASE);
            osinfoJson.put("CLIENT_ID", Author.CLIENT_ID);
        } catch (JSONException e) {

        }
        return osinfoJson.toString();
    }

    /**
     * 获取系统的IMIE
     *
     * @return
     */
    public static String osIMIE() {
        try {
            TelephonyManager tm = (TelephonyManager) GKApplication
                    .getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String phoneModel() {
        return Build.MODEL;
    }

    /**
     * 异或加密解密
     *
     * @param str
     * @return
     */
    public static String strEnDecodeXor(String str) {
        String key = Author.CLIENT_SECRET;
        int len = key.length();

        String strReturn = "";
        for (int i = 0; i < str.length(); i++) {
            int index = i % len;
            strReturn += String.valueOf((char) (str.charAt(i) ^ key
                    .charAt(index)));
        }
        return strReturn;
    }

    /**
     * MD5加密
     *
     * @param str
     * @return MD5加密后的32位
     */
    public static String convert2MD532(String str) {
        String md5Str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            md5Str = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return md5Str;
    }

    /**
     * ArrayList 转 string
     *
     * @param arraylist
     * @param conv
     * @return
     */
    public static String arrayListToString(ArrayList<String> arraylist,
                                           String conv) {
        String strReturn = "";
        int size = arraylist.size();
        if (size > 0) {
            for (int i = 0; i < size - 1; i++) {
                strReturn += arraylist.get(i) + conv;
            }
            strReturn += arraylist.get(size - 1);
        }
        return strReturn;
    }

    /**
     * String[] 转 string
     *
     * @param strArray
     * @param conv
     * @return
     */
    public static String strArrayToString(String[] strArray, String conv) {
        String strReturn = "";
        int length = strArray.length;
        if (length > 0) {
            for (int i = 0; i < length - 1; i++) {
                strReturn += strArray[i] + conv;
            }
            strReturn += strArray[length - 1];
        }
        return strReturn;
    }

    /**
     * 判断当前网络是否可用
     *
     * @param context
     * @return true可用, false不可用
     */
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isNetworkAvailableEx() {
        return isNetworkAvailable(GKApplication.getInstance());
    }


    /**
     * 判断当前网络是否开启wifi
     *
     * @param context
     * @return true-wifi, false-其它
     */
    public static boolean isNetworkWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo activeNetInfo = connectivityManager
                    .getActiveNetworkInfo();// 获取网络的连接情况
            if (activeNetInfo == null) return false;

            if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // WIFI
                return true;
            }
        }
        return false;
    }


    public static void hideSoftKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        //FIXME NULL EXCEPTION
        try {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            }

        } catch (NullPointerException e) {

        } catch (Exception e) {

        }

    }

    /**
     * hide with edittext
     *
     * @param context
     * @param editText
     */
    public static void hideSoftKeyBoard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            if (editText != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        } catch (NullPointerException e) {

        } catch (Exception e) {

        }

    }

    public static void showSoftKeyBoard(final Context context, final EditText text) {
        text.requestFocus();

        text.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) context.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(text, 0);
            }
        }, 200);

    }


    /**
     * return service status
     *
     * @param context
     * @param servicePkg
     * @return
     */
    public static boolean isServiceRunning(Context context, String servicePkg) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (servicePkg.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }

    /**
     * 打开本地filehash缓存的文件
     *
     * @param context
     * @param filehash
     * @param fileName
     * @param filesize 为了防止打开不正确的0字节文件，如果是不需要判断传0
     */
    public static boolean openLocalFile(Context context, String filehash, String fileName, long filesize) throws FileOperationException {
        if (TextUtils.isEmpty(filehash)) {
            return false;
        }
        String openPath = UtilOffline.getOpenTempPath() + filehash + "/" + fileName;

        Util.copyToOpenTempPath(openPath, filehash, filesize);

        return openLocalFile(context, openPath, filesize);
    }

    public interface FileOpenListener {
        public void onHandle(int type);

        public void onError(String errorMsg);
    }

    public static AsyncTask handleLocalFile(final Context context,
                                            final String filehash, final String fileName, final long filesize,
                                            final FileOpenListener listener, final int handleType) {
        if (TextUtils.isEmpty(filehash)) {
            listener.onError(context.getString(R.string.tip_open_file_with_excepiton));
            return null;
        }
        final String openPath = UtilOffline.getOpenTempPath() + filehash + "/" + fileName;

        AsyncTask task = new AsyncTask<Object, Void, Void>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Object... params) {
                try {
                    Util.copyToOpenTempPath(openPath, filehash, filesize);
                    if (handleType == Constants.HANDLE_TYPE_OPEN) {
                        Util.openLocalFile(context, openPath, filesize);
                    } else if (handleType == Constants.HANDLE_TYPE_SEND) {
                        Util.send(context, openPath);
                    }


                } catch (FileOperationException e) {
                    listener.onError(e.getErrorDescription());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                listener.onHandle(handleType);

            }
        }.execute("");
        return task;
    }

    public interface FileCopyListener {
        public void onCopy();
    }

    public static AsyncTask copyFileAsync(final String openPath, final String filehash, final long filesize, final FileCopyListener listener) {
        AsyncTask task = new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... params) {

                try {
                    copyToOpenTempPath(openPath, filehash, filesize);
                } catch (FileOperationException e) {
                    UtilDialog.showCrossThreadToast(e.getErrorDescription());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onCopy();
            }
        }.execute();
        return task;
    }

    public static boolean isCacheFileExit(String filehash, long filesize) {
        if (TextUtils.isEmpty(filehash)) {
            return false;
        }
        String cachePath = Config.getLocalFilePath(filehash);
        File file = new File(cachePath);

        return file.exists() && file.length() == filesize;
    }

    /**
     * 打开本地存在的文件
     *
     * @param context
     * @param openPath
     * @param filesize @return
     */
    public static boolean openLocalFile(Context context, String openPath, long filesize) {
        Uri uri = Uri.parse(openPath);
        // If there is no scheme, then it must be a file
        if (uri.getScheme() == null) {
            File file = new File(openPath);
            if (!file.exists()) {
                return false;
            }

            if (file.length() == 0 && filesize > 0) {//避免打开错误的0字节文件
                return false;
            }

            uri = Uri.fromFile(file);
        }

        String type = GKApplication.getInstance().getFileMimeType(openPath);

        openFile(context, uri, type);
        return true;

    }


    /**
     * 打开文件
     *
     * @param context
     * @param uri
     * @param type
     */
    public static void openFile(Context context, Uri uri, String type) {
        if (uri == null) {
            return;
        }
        if (type == null) {
            type = "";
        }

        if (type.contains("image/")) {
            viewImage(context, uri);
        } else if (type.contains("audio/")) {
            viewAudio(context, uri);
        } else if (type.contains("video/")) {
            viewVideo(context, uri);
        } else if (type.equals("text/gknote")) {
            viewGKnote(context, uri);
        } else {
            viewFile(context, uri, type);
        }
    }

    /**
     * 打开图片
     *
     * @param context
     * @param uri
     */
    private static void viewImage(Context context, Uri uri) {
        if (uri == null) {
            return;
        }
        // createThread a image viewer test
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "image/*");

        try {
            context.startActivity(Intent.createChooser(intent,
                    context.getText(R.string.title_open_image)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_open_image,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开音频文件
     *
     * @param context
     * @param uri
     */
    private static void viewAudio(Context context, Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "audio/*");

        try {
            context.startActivity(Intent.createChooser(intent,
                    context.getText(R.string.title_open_audio)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_open_audio,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开视频文件
     *
     * @param context
     * @param uri
     */
    private static void viewVideo(Context context, Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "video/*");
        try {
            context.startActivity(Intent.createChooser(intent,
                    context.getText(R.string.title_open_video)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_open_video,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查看GKNote文件
     *
     * @param context
     * @param uri
     */
    private static void viewGKnote(Context context, Uri uri) {
        Intent intent = new Intent(context, GKNoteEditorActivity.class);
        intent.putExtra(Constants.GKNOTE_URI, uri);
        intent.putExtra(Constants.GKNOTE_EDIT, true);
        context.startActivity(intent);
    }

    /**
     * 打开其他文件， 非图片，视频，音频
     *
     * @param context
     * @param uri
     * @param type
     */
    private static void viewFile(Context context, Uri uri, String type) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, type);
        try {
            context.startActivity(Intent.createChooser(intent,
                    context.getText(R.string.title_open_file)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_open_file,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 交予系统下载
     *
     * @param context
     * @param url
     */
    public static void downloadFile(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 在浏览器中打开
     *
     * @param context
     * @param url
     */
    public static void invokeWebBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            UtilDialog.showNormalToast(R.string.web_browser_not_find);
        }
    }

    /**
     * 发送至其他应用
     *
     * @param context
     * @param path    本地文件路径
     */
    public static void send(Context context, String path) {
        if (path == null) {
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        Uri uri = Uri.fromFile(file);
        String type = GKApplication.getInstance().getFileMimeType(path);
        send(context, uri, type);
    }

    /**
     * 发送至其他应用
     *
     * @param context
     * @param uri
     * @param mimeType
     */
    private static void send(Context context, Uri uri, String mimeType) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);


        if (mimeType.equals("text/plain")) {
            intent.setType(mimeType);
            String str = UtilFile.readFileData(uri.getPath(), UtilFile.DEFAUT_CHARSET_ENCODING_FOR_TXT);
            intent.putExtra(Intent.EXTRA_TEXT, str);
        } else {
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        try {
            context.startActivity(Intent.createChooser(intent, context.getText(R.string.title_send_file)));
            // context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_send_file,
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 得到路径分隔符在文件路径中最后出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
     *
     * @param fileName 文件路径
     * @return 路径分隔符在路径中最后出现的位置，没有出现时返回-1。
     */
    private static int getPathLastIndex(String fileName) {
        int point = fileName.lastIndexOf('/');
        if (point == -1) {
            point = fileName.lastIndexOf('\\');
        }
        return point;
    }

    /**
     * 得到路径分隔符在文件路径中指定位置前最后出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
     *
     * @param fileName  文件路径
     * @param fromIndex 开始查找的位置
     * @return 路径分隔符在路径中指定位置前最后出现的位置，没有出现时返回-1。
     */
    private static int getPathLastIndex(String fileName, int fromIndex) {
        int point = fileName.lastIndexOf('/', fromIndex);
        if (point == -1) {
            point = fileName.lastIndexOf('\\', fromIndex);
        }
        return point;
    }

    /**
     * 得到文件名中的父路径部分。 对两种路径分隔符都有效。 不存在时返回""。
     *
     * @param fulpath 文件名
     * @return 父路径，不存在或者已经是父目录时返回""
     */
    public static String getParentPath(String fulpath) {
        int point = getPathLastIndex(fulpath);
        int length = fulpath.length();
        if (point == -1) {
            return "";
        } else if (point == length - 1) {
            int secondPoint = getPathLastIndex(fulpath, point - 1);
            if (secondPoint == -1) {
                return "";
            } else {
                return fulpath.substring(0, secondPoint);
            }
        } else {
            return fulpath.substring(0, point);
        }
    }


    /**
     * 得到文件路径中的文件名
     *
     * @param filePath 文件路径名
     * @return 文件名
     */
    public static String getNameFromPath(String filePath) {
        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }
        int point = getPathLastIndex(filePath);
        int length = filePath.length();
        if (point == -1) {
            return filePath;
        } else {
            return filePath.substring(point, length);
        }
    }

    /**
     * 获取文件夹文件名
     *
     * @param folderPath
     * @return
     */
    public static String getTargetFolderName(String folderPath) {
        if (folderPath.equals("")) return "";

        folderPath = folderPath.substring(0, folderPath.length() - 1);
        return getNameFromPath(folderPath);
    }

//    /**
//     * 格式化空间
//     *
//     * @param context
//     * @param used
//     * @param total
//     * @param orgId
//     * @param orgSize
//     * @return
//     */
//    public static String formatSpace(Context context, long used, long total,
//                                     long orgSize, int orgId) {
//        String space = "";
//        if (orgId == 0) {
//            space = String.format(
//                    context.getResources().getString(
//                            R.string.space_formate_no_org),
//                    formatFileSize(context, used),
//                    formatFileSize(context, total));
//        } else {
//            if (total == 0) {
//                space = String.format(
//                        context.getResources().getString(
//                                R.string.space_formate_have_org),
//                        formatFileSize(context, used),
//                        formatFileSize(context, orgSize));
//            } else {
//                space = String.format(
//                        context.getResources().getString(
//                                R.string.space_formate_have_org_without_pay),
//                        formatFileSize(context, used),
//                        formatFileSize(context, orgSize),
//                        formatFileSize(context, total));
//            }
//        }
//        // test
//        return space;
//    }

    /**
     * 获取IP地址
     *
     * @return
     */
    public static int getWifiIp() {
        WifiManager wifiMgr = (WifiManager) GKApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getIpAddress();
    }

    public static String getWifiSSID() {
        WifiManager wifiMgr = (WifiManager) GKApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getSSID();

    }

    /**
     * 整数转成ip地址
     *
     * @param i
     * @return
     */
    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public final static String TIMEFORMAT_YMD = "yyyy-MM-dd";
    public final static String TIMEFORMAT = "yyyy/MM/dd HH:mm:ss";
    public final static String TIMEFORMAT_WITHOUT_SECONDS = "yyyy/M/d HH:mm";
    public final static String TIMEFORMAT_WITH_DATE = "yy-MM-dd";
    public final static String TIMEFORMAT_HS = "HH:mm";
    public final static String TIMEFORMAT_YEAR_MONTH_DAY_HOUR = "yyyy-MM-dd HH:mm";
    public final static String TIMEFORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";
    public final static String TIMEFORMAT_MONTH_DAY_HOUR = "MM-dd HH:mm";
    private final static SimpleDateFormat TIMEFORMATRFC822 = new SimpleDateFormat(
            "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT_YMD);
        return sdf.format(new Date());
    }

    /**
     * 格式化时间
     *
     * @param secondes
     * @return
     */
    public static String formateTime(long secondes) {
        long milliseconds = secondes * 1000;
        SimpleDateFormat formatter = new SimpleDateFormat(
                TIMEFORMAT_WITHOUT_SECONDS);
        Date date = new Date(milliseconds);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String time = formatter.format(date);
        return time;
    }

    /**
     * 格式化时间
     *
     * @param milliseconds
     * @param context
     * @return
     */
    public static String formateTime(long milliseconds, String timeFormat, Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        Date date = new Date(milliseconds);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return formatter.format(date);
    }

    /**
     * 今日以内显示 **：**
     * 昨天 显示昨天
     * 超过昨天 显示星期几
     * <p/>
     * 超过一星期显示具体日期
     *
     * @param dateline seconds
     * @param context
     * @return
     */
    public static String customFormat(long dateline, Context context) {
        long todayZero = getTodayZeroTime(false);
        long diff = dateline - todayZero;
        String returnString = "";
        if (diff >= 0) {
            returnString = Util.formateTime(dateline * 1000, TIMEFORMAT_HS, context);
        } else if (diff < 0 && diff >= -86400) {
            returnString = context.getString(R.string.date_yesterday);
        } else if (diff >= -604800 && diff < -86400) {//7天内
            returnString = getWeekOfDay(dateline, context);
        } else {
            returnString = Util.formateTime(dateline * 1000, TIMEFORMAT_WITH_DATE, context);
        }
        return returnString;
    }

    public static String getWeekOfDay(long dateline, Context context) {
        String dayOfWeekStr = "";
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(dateline * 1000);
        int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayOfWeekStr = context.getString(R.string.date_sunday);
                break;
            case Calendar.MONDAY:
                dayOfWeekStr = context.getString(R.string.date_monday);
                break;
            case Calendar.TUESDAY:
                dayOfWeekStr = context.getString(R.string.date_tuesday);
                break;
            case Calendar.WEDNESDAY:
                dayOfWeekStr = context.getString(R.string.date_wednesday);
                break;
            case Calendar.THURSDAY:
                dayOfWeekStr = context.getString(R.string.date_thursday);
                break;
            case Calendar.FRIDAY:
                dayOfWeekStr = context.getString(R.string.date_friday);
                break;
            case Calendar.SATURDAY:
                dayOfWeekStr = context.getString(R.string.date_saturday);
                break;
        }

        return dayOfWeekStr;
    }

    /**
     * 获取今天时间戳（秒）
     *
     * @return
     */
    public static long getTodayZeroTime(boolean isMs) {
        Calendar ca = (Calendar) Calendar.getInstance().clone();
        ca.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DATE), 0, 0, 0);
        return isMs ? ca.getTimeInMillis() : (int) (ca.getTimeInMillis() / 1000);
    }

    /**
     * 获取当前RFC822时间
     *
     * @return
     */
    public static String getDateAsRFC822String() {
        Date date = new Date();
        String time = TIMEFORMATRFC822.format(date);
        return time;
    }

    /**
     * ListView高度自动调整
     *
     * @param baseAdapter
     * @param listview
     * @param height
     */
    public static void autoListViewHeight(BaseAdapter baseAdapter,
                                          ListView listview, int height) {
        int totalHeight = 0;
        for (int i = 0; i < baseAdapter.getCount(); i++) {
            View listItem = baseAdapter.getView(i, null, listview);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalHeight
                + (listview.getDividerHeight() * (baseAdapter.getCount() - 1));
        params.height += height;
        listview.setLayoutParams(params);
    }

    public static void autoListViewItem(BaseAdapter listAdapter, ListView listView, int height) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height += height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    /**
     * 重命名文件或者文件夹
     *
     * @param oldPath
     * @param newName
     */
    public static void renameFile(String oldPath, String newName) {
        File file = new File(oldPath);
        if (!file.exists()) {
            return;
        }
        String newPath = newName;

        if (file.getParent() != null) {
            newPath = file.getParent() + File.separator + newName;
        }
        file.renameTo(new File(newPath));
    }

    /**
     * 移动文件、文件夹
     *
     * @param oldPath
     * @param targetPath
     */
    public static void moveFile(String oldPath, String targetPath) {
        File file = new File(oldPath);
        if (!file.exists()) {
            return;
        }
        String newPath = "";
        //TODO
        file.renameTo(new File(newPath));
    }

    public static void copyToOpenTempPath(String openPath, String filehash, long filesize) throws FileOperationException {
        if (TextUtils.isEmpty(filehash)) {
            throw new FileOperationException(FileOperationException.ERRORCODE_ERROR_PARAM);
        }
        File targetFile = new File(openPath);
        if (targetFile.exists() && targetFile.length() > 0) {
            if (targetFile.length() != filesize) {
                Util.deleteFile(openPath);
            }
        }

        String cacheFilePath = Config.getLocalFilePath(filehash);
        File cacheFile = new File(cacheFilePath);
        if (!cacheFile.exists()) {
            throw new FileOperationException(FileOperationException.ERRORCODE_FILE_NO_EXIST);
        }
        UtilFile.copyFile(cacheFile, targetFile);
    }

    public static void copyToClipbord(Context context, String content) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(content);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(context.getString(R.string.app_name), content);
            clipboard.setPrimaryClip(clip);
        }

    }

    /**
     * 对应filehash复制到缓存
     *
     * @param context
     * @param uri
     */
    public static boolean copyFile(Context context, Uri uri, String filehash) {
        boolean success = false;
        if (uri == null) {
            return success;
        }
        InputStream in = null;
        if (URLUtil.isFileUrl(uri.toString())) {
            File file = new File(URLDecoder.decode(uri.getEncodedPath()));
            if (file.exists()) {
                try {
                    in = new FileInputStream(file);
                } catch (Exception e) {

                }
            }
        } else {
            try {
                in = context.getContentResolver().openInputStream(uri);
            } catch (Exception e) {

            }
        }
        if (in != null) {
            String newPath = Config.getLocalFilePath(filehash);
            File file = new File(newPath);
            if (file.exists()) {
                return true;
            }
            success = UtilFile.copyFile(in, file);
        }

        return success;
    }


    /**
     * 删除文件或者文件夹
     *
     * @param path
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            if (file.isFile()) {
                file.delete();
            }
            return;
        }
        String[] tempList = file.list();
        String childFilePath = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                childFilePath = path + tempList[i];
            } else {
                childFilePath = path + File.separator + tempList[i];
            }
            File temp = new File(childFilePath);
            if (temp.isFile()) {
                temp.delete();
            } else if (temp.isDirectory()) {
                deleteFile(childFilePath);
            }
        }
        file.delete();
    }

    /**
     * 获得版本号
     *
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0xffffffff);
            return pInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }

    }

    static Method methodFormatterFileSize = null;

    private static void iniCupcakeInterface() {
        try {
            methodFormatterFileSize = Class.forName(
                    "android.text.format.Formatter").getMethod(
                    "formatFileSize", Context.class, long.class);
        } catch (Exception ex) {
            // This is not cupcake.
            return;
        }
    }

    /**
     * 格式化文件大小
     *
     * @param context
     * @param fileSize
     * @return
     */
    public static String formatFileSize(Context context, long fileSize) {
        if (methodFormatterFileSize == null) {
            iniCupcakeInterface();
        }
        String result = null;
        try {
            result = (String) methodFormatterFileSize.invoke(null, context,
                    fileSize);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result == null) {
                result = Long.toString(fileSize / 1024) + "KB";
            }
        }

        return result;
    }


    public static InputStream getSysInputStream(Context context, Uri uri) {
        Cursor c = context.getContentResolver().query(uri, null, null, null,
                null);

        if (c == null || !c.moveToFirst()) {
            if (c != null)
                c.close();

            return null;
        }

        int index = c.getColumnIndex("_data");
        if (index < 0) {
            c.close();
            return null;
        }

        String path = c.getString(index);
        c.close();

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendEmail(Context context, String email, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            context.startActivity(Intent.createChooser(emailIntent, context.getText(R.string.title_send_file)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_send_email,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendEmailWithHtml(Context context, String email, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(body)
        );
//        emailIntent.putExtra(
//                Intent.EXTRA_TEXT,
//                Html.fromHtml(new StringBuilder()
//                        .append("<p><b>Some Content</b></p>")
//                        .append("<a>http://www.google.com</a>")
//                        .append("<small><p>More content</p></small>")
//                        .toString())
//        );

        try {
            context.startActivity(Intent.createChooser(emailIntent, context.getText(R.string.title_send_file)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_send_email,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendSmsWithNumber(Context context, String phone) {
        Uri smsUri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
        intent.putExtra("sms_body", "");
        try {
            context.startActivity(Intent.createChooser(intent, context.getText(R.string.title_send_file)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, R.string.no_way_to_send_message,
                    Toast.LENGTH_SHORT).show();
        }
    }


    public static void sendSmsWithMessage(Context context, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("smsto:"));
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }
            try {
                context.startActivity(Intent.createChooser(intent, context.getText(R.string.title_send_file)));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(context, R.string.no_way_to_send_message,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri uri = Uri.parse("sms:");
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, uri);
            smsIntent.putExtra("sms_body", message);
            smsIntent.putExtra("address", "");
            smsIntent.setType("vnd.android-dir/mms-sms");
            try {
                context.startActivity(Intent.createChooser(smsIntent, context.getText(R.string.title_send_file)));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(context, R.string.no_way_to_send_message,
                        Toast.LENGTH_SHORT).show();
            }

        }
    }


    public static void sendCall(Context context, String phone) {
        Uri callUri = Uri.parse("tel:" + phone);
        context.startActivity(new Intent(Intent.ACTION_DIAL, callUri));
    }

    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static void shareWordsToWXFriends(Context context, String message, File file) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    public static void shareWordsToWXTimeLine(Context context, String message, File file) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(componentName);

        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("image/*");
        context.startActivity(intent);
    }



    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 将px换算为dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip换算为px
     *
     * @param context
     * @param dpValue
     * @return
     */

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将bitmap转化成drawable
     *
     * @param bitmap
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Drawable bitmapDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);

    }

    /**
     * 将drawable转化成bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableBitmap(Drawable drawable) {
        BitmapDrawable db = (BitmapDrawable) drawable;
        return db.getBitmap();
    }

    public static byte[] drawableToByte(Context context, int resId) {
        Drawable drawable = context.getResources().getDrawable(resId);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * 返回是否是邮箱地址
     *
     * @param emailString 邮箱地址
     * @return
     */
    public static boolean isEmail(String emailString) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(emailString);
        return m.matches();
    }

    public static boolean isContainSpecail(String folderName) {
        if (folderName.contains("\\")) {
            return true;

        } else if (folderName.contains(":")) {
            return true;

        } else if (folderName.contains("<")) {
            return true;

        } else if (folderName.contains(">")) {
            return true;

        } else if (folderName.contains("|")) {
            return true;

        } else if (folderName.contains("\"")) {
            return true;

        } else if (folderName.contains("/")) {
            return true;
        } else if (folderName.contains("*")) {
            return true;
        } else if (folderName.contains("?")) {
            return true;
        }
        return false;

    }


    public static boolean isInvaidName(String folderName) {
        if (folderName.startsWith(".") || folderName.endsWith(".")) {
            return true;
        }
        return false;
    }

    public static boolean isHttpUrl(String s) {
        String regEx = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        String regEx2 = "[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";

        Pattern p = Pattern.compile(regEx);
        Pattern p2 = Pattern.compile(regEx2);
        Matcher m = p.matcher(s);
        Matcher m2 = p2.matcher(s);
        return m.matches() || m2.matches();
    }

    public static boolean isContainExpression(CharSequence input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == 55356 || c == 55357 || c == 10060 || c == 9749 || c == 9917 || c == 10067 || c == 10024
                    || c == 11088 || c == 9889 || c == 9729 || c == 11093 || c == 9924) {
                return true;
            }
        }
        return false;
    }


    public static boolean isSpecialChar(CharSequence c) {
        String regEx = "[/\\:*?<>|\"]";

        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(c);
        return m.matches();
    }

    public static boolean isCodeFormat(CharSequence c) {
        String regEx = "^[A-Za-z0-9]+$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(c);
        return m.matches();
    }

    public static boolean isCloudLibNameForm(CharSequence c) {
        String regEx = "^[\u4e00-\u9fa5_a-zA-Z0-9]+$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(c);
        return m.matches();
    }

    public static String getTrimDirName(String dirName) {
        String name = trimMutiSpaceToOne(dirName);
        return trim(name, " ");
    }

    public static String trimMutiSpaceToOne(String str) {
        String s = str.replaceAll(" +", " ");
        return s;
    }

    public static String trimEnd(String input, String charsToTrim) {
        return input.replaceAll("[" + charsToTrim + "]+$", "");
    }

    public static String trimStart(String input, String charsToTrim) {
        return input.replaceAll("^[" + charsToTrim + "]+", "");
    }

    public static String trim(String input, String charsToTrim) {
        return input.replaceAll("^[" + charsToTrim + "]+|[" + charsToTrim + "]+$", "");
    }

    /**
     * 过滤特殊邮箱地址
     *
     * @param originalEmail
     * @return
     */

    public static boolean isSpecialEmail(String originalEmail) {
        String strPattern = ".+?@gk\\.oauth\\.[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]$";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(originalEmail);
        return m.matches();
    }


    /**
     * 获得圆形的drawable
     *
     * @param drawable
     * @param context
     * @param withBorder
     * @return
     */
    public static Bitmap makeRoundDrawable(Drawable drawable, Context context, boolean withBorder) {
        Bitmap bitmap = drawableBitmap(drawable);
        return makeRoundBitmap(bitmap, context, withBorder);
    }

    public static Bitmap getRoundedCornerBitmap(Drawable drawable, int pixels) {
        Bitmap bitmap = drawableBitmap(drawable);
        return getRoundedCornerBitmap(bitmap, pixels);
    }

    /**
     * 获取圆角bitmap
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获得圆形头像
     *
     * @return
     */
    public static Bitmap makeRoundBitmap(Bitmap bitmap, Context context,
                                         boolean withBorder) {
        if (bitmap == null) return null;
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
                TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);

        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        if (withBorder) {
            Paint strokePaint = new Paint();
            strokePaint.setAntiAlias(true);
            strokePaint.setColor(0xffffffff);
            strokePaint.setStrokeWidth(Util.dip2px(context, 2));
            strokePaint.setStyle(Paint.Style.STROKE);
            c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2 - Util.dip2px(context, 1),
                    strokePaint);
        }
        return circleBitmap;
    }


//    /**
//     * 生成二维码(webview访问生成)
//     *
//     * @param context
//     * @param fullpath
//     */
//    public static void createFileQrCode(Context context, String fullpath) {
//        //FIXME
//        Intent intent = new Intent(context, FunctionExtendWebViewActivity.class);
//        intent.putExtra(FunctionExtendWebViewActivity.EXTRA_WEBVIEW_TYPE,
//                FunctionExtendWebViewActivity.WEBVIEW_TYPE_SHOWSHAREQR);
//        intent.putExtra(FunctionExtendWebViewActivity.EXTRA_WEBVIEW_FULLPATH, fullpath);
//        context.startActivity(intent);
//
//    }

    /**
     * 0时区时间
     *
     * @return
     */
    public static long getUnixDateline() {
        Calendar ca = Calendar.getInstance(Locale.US);
        return ca.getTimeInMillis() / 1000;
    }

    /**
     * 将bitmap转化为file
     *
     * @param bitmap
     * @param uri
     */
    public static void bitmapToFile(Bitmap bitmap, String uri) {

        try {
            File file = new File(uri);
            if (file.exists()) {
                file.delete();
            } else {
                if (!file.getParentFile().isDirectory()) {
                    file.getParentFile().mkdirs();
                }
            }
            FileOutputStream out;
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

    }


    public static Bitmap getPreview(Uri uri, int thumbSize) {
        File image = new File(uri.getPath());

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbSize;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    public static Bitmap decodeSampledBitmapFromFile(File file) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);

        int width_tmp = options.outWidth, height_tmp = options.outHeight;
        int REQUIRED_SIZE = 2048;
        if (width_tmp / height_tmp < 3 || height_tmp / width_tmp < 3) {
            REQUIRED_SIZE = 2048;
        }

        final float reqwidth = width_tmp > height_tmp ? REQUIRED_SIZE
                : (width_tmp * REQUIRED_SIZE / height_tmp);
        final float reqheight = height_tmp > width_tmp ? REQUIRED_SIZE
                : (height_tmp * REQUIRED_SIZE / width_tmp);

        final int minSideLength = Math.min((int) reqwidth, (int) reqheight);
        options.inSampleSize = computeSampleSize(options, minSideLength,
                (int) reqwidth * (int) reqheight);
        options.inJustDecodeBounds = false;
        options.inInputShareable = true;
        options.inPurgeable = true;

        Bitmap b = null;
        try {
            b = BitmapFactory.decodeFile(file.getPath(), options);
        } catch (OutOfMemoryError error) {
            UtilDialog.showCrossThreadToast(R.string.out_of_memory);
        }
        return b;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static String getThumbPathFromImagePath(String path) {
        String imagePath = null;
        if (path != null) {
            String[] columns_to_return = {MediaStore.Images.Media._ID};
            String where = MediaStore.Images.Media.DATA + " LIKE ?";
            long reterievedImageId = -1;
            String valuesAre[] = {"%" + path};
            Cursor cursor = GKApplication.getInstance().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns_to_return, where, valuesAre, null);
            if (cursor != null) {
                int imageIdInImages = cursor.getColumnIndex(MediaStore.Images.Media._ID);

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    reterievedImageId = Long.parseLong(cursor.getString(imageIdInImages));
                }
                if (reterievedImageId != -1) {
                    String[] columnsReturn = {MediaStore.Images.Thumbnails.DATA};
                    String whereimageId = MediaStore.Images.Thumbnails.IMAGE_ID + " LIKE ?";
                    String valuesIs[] = {"%" + reterievedImageId};
                    Cursor mCursor = GKApplication.getInstance().getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, columnsReturn, whereimageId, valuesIs, null);
                    int rawDataPath = mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                        imagePath = mCursor.getString(rawDataPath);
                    }
                    mCursor.close();
                }
            }
            cursor.close();
        }
        return imagePath;
    }

    public static String getThumbPathFromVideoPath(String path) {
        String videoPath = null;
        if (path != null) {
            String[] columns_to_return = {MediaStore.Video.Media._ID};
            String where = MediaStore.Video.Media.DATA + " LIKE ?";
            long reterievedImageId = -1;
            String valuesAre[] = {"%" + path};
            Cursor cursor = GKApplication.getInstance().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns_to_return, where, valuesAre, null);
            if (cursor != null) {
                int imageIdInImages = cursor.getColumnIndex(MediaStore.Video.Media._ID);

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    reterievedImageId = Long.parseLong(cursor.getString(imageIdInImages));
                }
                if (reterievedImageId != -1) {
                    String[] columnsReturn = {MediaStore.Video.Thumbnails.DATA};
                    String whereimageId = MediaStore.Video.Thumbnails.VIDEO_ID + " LIKE ?";
                    String valuesIs[] = {"%" + reterievedImageId};
                    Cursor mCursor = GKApplication.getInstance().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, columnsReturn, whereimageId, valuesIs, null);
                    int rawDataPath = mCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
                    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                        videoPath = mCursor.getString(rawDataPath);
                    }
                    mCursor.close();
                }
            }
            cursor.close();
        }
        return videoPath;

    }

    public static String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = GKApplication.getInstance().getContentResolver().query(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String realPath = cursor.getString(column_index);
        cursor.close();
        return realPath;
    }

    /**
     * 将InputStream转换成某种字符编码的String
     *
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String inputstream2String(InputStream in, String encoding) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        while ((count = in.read(data, 0, 4096)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), encoding);
    }

    public static void setFinalStatic(Class clazz, String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiers = field.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        field.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);

    }


    /**
     * gzip压缩
     *
     * @param string
     * @return
     * @throws java.io.IOException
     */
    public static byte[] gZipCompress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    /**
     * Gzip解压缩
     *
     * @param compressed
     * @return
     * @throws java.io.IOException
     */
    public static String gZipDecompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }


    /**
     * 给textview的文字，部分加粗或者改变颜色
     *
     * @param text
     * @param color    颜色值 rgb
     * @param typeface Typeface 中的常量，例如Typeface
     * @param start    开始位置
     * @param end      末尾位置
     * @return
     */
    public static SpannableStringBuilder getSepecialfyString(String text, int color, int typeface, int start, int end) {
        final SpannableStringBuilder sb = new SpannableStringBuilder(text);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(color);

        // Span to set text color to some RGB value
        final StyleSpan bss = new StyleSpan(typeface);

        // Span to make text color
        sb.setSpan(fcs, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // Set the text bold
        sb.setSpan(bss, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    public static byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 将流转化成byte数组
     *
     * @param infile
     * @return
     */
    public static byte[] stream2Byte(String infile) {
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(infile));
            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] content = out.toByteArray();
        return content;
    }

    public static void stringToFile(String str, String filename) {
        try {
            BufferedReader in = new BufferedReader(new StringReader(str));

            PrintWriter out = new PrintWriter(new FileWriter(filename));

            String s;

            while ((s = in.readLine()) != null) {
                out.println(s);
            }

            out.close();

        } catch (IOException e4) {
        }

    }


    public static boolean comparison(String srcPath, String decodePath) {

        FileInputStream File1 = null;
        FileInputStream File2 = null;
        byte buffer1[];
        byte buffer2[];
        try {
            File1 = new FileInputStream(srcPath);
            File2 = new FileInputStream(decodePath);
            buffer1 = new byte[1024];
            buffer2 = new byte[1024];
            // 判断两个文件是否相等
            try {
                if (File1.available() != File2.available()) {
                    return false;
                } else {

                    while (File1.read(buffer1) != -1
                            && File2.read(buffer2) != -1) {
                        if (File1.read(buffer2) != File2.read(buffer2)) {
                            return false;
                        }
                    }

                }
            } catch (IOException e) {
                return false;
            }
        } catch (FileNotFoundException e) {
            return false;
        } finally {

            try {
                File1.close();
                File2.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return true;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            file = new File(filePath);
            File parent = file.getParentFile();
            if (!parent.exists()) {//判断文件目录是否存在
                parent.mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 检验程序是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPackageAvaliable(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public static boolean isSafeIntent(Context context, Intent intent, int requestCode) {
        PackageManager packageManager = context.getPackageManager();

        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

        boolean isIntentSafe = activities.size() > 0;
        return isIntentSafe;
    }


    public static byte[] shortToByteArray(short s) {
        byte[] shortBuf = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = i * 8;
            shortBuf[i] = (byte) ((s >>> offset) & 0xff);
        }
        return shortBuf;
    }

    /**
     * 将32位整数转换成长度为4的byte数组
     *
     * @param s int
     * @return byte[]
     */
    public static byte[] intToByteArray(int s) {
        byte[] targets = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = i * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * long to byte[]
     *
     * @param s long
     * @return byte[]
     */
    public static byte[] longToByteArray(long s) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = i * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    public static void zip(String[] files, String zipFile) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[4096];

            for (int i = 0; i < files.length; i++) {

                File file = new File(files[i]);
                if (file.isFile()) {
                    FileInputStream fi = new FileInputStream(files[i]);
                    origin = new BufferedInputStream(fi, 4096);
                    try {
                        ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, 4096)) != -1) {
                            out.write(data, 0, count);
                        }
                    } finally {
                        origin.close();
                    }
                } else {
                    String fileList[] = file.list();

                    //如果没有子文件, 则添加进去即可
                    if (!file.exists() || fileList.length <= 0) {
                        ZipEntry zipEntry =
                                new ZipEntry(file.getName() + File.separator);
                        out.putNextEntry(zipEntry);
                        out.closeEntry();
                    } else {

                        zip(fileList, zipFile);
                    }
                }
            }
        } finally {
            out.close();
        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
        File f = new File(location);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        try {
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                String path = location + ze.getName();

                if (ze.isDirectory()) {
                    File unzipFile = new File(path);
                    if (!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                } else {
                    FileOutputStream fout = new FileOutputStream(path, false);
                    try {
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                        zin.closeEntry();
                    } finally {
                        fout.close();
                    }
                }
            }
        } finally {
            zin.close();
        }
    }

    public static String getAppNameFromPackageName(String packageName) {
        final PackageManager pm = GKApplication.getInstance().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    public static Drawable getAppIconFromPackageName(String packageName) {
        Drawable d = null;
        try {
            d = GKApplication.getInstance().getPackageManager().getApplicationIcon(packageName);
        } catch (NameNotFoundException e) {
        }
        return d;
    }

    public static long getFolderSize(File directory) {
        long length = 0;
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.exists()) {
                    if (file.isFile())
                        length += file.length();
                    else
                        length += getFolderSize(file);
                }
            }
        }
        return length;
    }

    public static String convertToThumbnailPath(String fileName) {
        return Config.DCIM_PATH + File.separator + ".thumbnails/" + fileName;
    }


}
