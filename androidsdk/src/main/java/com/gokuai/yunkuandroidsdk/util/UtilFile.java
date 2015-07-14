package com.gokuai.yunkuandroidsdk.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.provider.OpenableColumns;
import android.webkit.URLUtil;

import com.gokuai.yunkuandroidsdk.DebugFlag;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.MessageDigest;

//import com.gokuai.library.net.NotifyManager;

public class UtilFile {
    static final String LOG_TAG = "UtilFile";
    public static final String DEFAUT_CHARSET_ENCODING_FOR_TXT = "GBK";
    public static final String DEFAUT_CHARSET_ENCODING_FOR_SAVE_DATA = "UTF-8";
    public static final String IMG[] = {"png", "gif", "jpeg", "jpg", "bmp","psd","ai"};
    static final String PROGRAM[] = {"ipa", "exe", "pxl", "apk", "bat", "com"};
    static final String COMPRESSFILE[] = {"iso", "tar", "rar", "gz", "cab",
            "zip"};
    public static final String VIDEO[] = {"3gp", "asf", "avi", "m4v", "mpg", "flv",
            "mkv", "mov", "mp4", "mpeg", "mpg", "rm", "rmvb", "ts", "wmv",
            "3gp", "avi"};
    static final String MUSIC[] = {"flac", "m4a", "mp3", "ogg", "aac", "ape",
            "wma", "wav"};
    static final String WORDSFILE[] = {"odt", "txt"};
    static final String PREVIEW_FILES_TYPE[] = {"doc", "docm", "docx", "dot", "dotm", "dotx", "odt", "ods", "xls",
            "xlsb", "xlsm", "xlsx", "odp", "pot", "potm", "potx", "pps", "ppsm", "ppsx", "ppt", "pptm", "pptx", "pdf"};


//	static final String OFFICEDOC[] = { "ppt", "pptx", "doc", "docx", "xls",
//			"xlsx" };
//	static final int FILE_TYPE_IMAGE = 0;
//	static final int FILE_TYPE_PROGRAM = 1;
//	static final int FILE_TYPE_COMPRESS_FILE = 2;
//	static final int FILE_TYPE_VIDEO = 3;
//	static final int FILE_TYPE_MUISC = 4;
//	static final int FILE_TYPE_WORDS_FILE = 5;

    /**
     * Whether the filename is a video file.
     *
     * @param filename
     * @return
     */
    /*
     * public static boolean isVideo(String filename) { String mimeType =
	 * getMimeType(filename); if (mimeType != null &&
	 * mimeType.startsWith("video/")) { return true; } else { return false; } }
	 */

    /**
     * Whether the URI is a local one.
     *
     * @param uri
     * @return
     */
    public static boolean isLocal(String uri) {
        if (uri != null && !uri.startsWith("http://")) {
            return true;
        }
        return false;
    }


    public static String getExtension(String uri) {
        return getExtensionWithDot(uri).replace(".", "");
    }

    /**
     * Get the extension of a file name, like "png" or "jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    public static String getExtensionWithDot(String uri) {
        if (uri == null) {
            return null;
        }
        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot).toLowerCase();
        } else {
            return "";
        }
    }

    public static boolean isImageFile(String fileName) {
        String ext = getExtension(fileName).toLowerCase();
        for (String str : IMG) {
            if (str.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPreviewFile(String fileName) {
        String ext = getExtension(fileName).toLowerCase();
        for (String str : PREVIEW_FILES_TYPE) {
            if (str.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoFile(String fileName) {
        String ext = getExtension(fileName).toLowerCase();
        for (String str : VIDEO) {
            if (str.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 大小不敏感处理
     *
     * @param source
     * @param oldStr
     * @param newStr
     * @return
     */
    public static String replaceString(String source, String oldStr,
                                       String newStr) {
        String result = source.replaceAll("(?i)" + oldStr, newStr); // 大小写不敏感
        return result;
    }

    /**
     * Get the extension icon of a file name.
     *
     * @param context
     * @param uri
     * @return
     */
    public static int getExtensionIcon(Context context, String uri) {

        String ext = getExtension(uri);
        // image file
        if (ext.length() > 0) {
            int id = 0;
            for (String img : IMG) {
                if (ext.equals(img)) {
                    id = context.getResources().getIdentifier("ic_img",
                            "drawable", context.getPackageName());

                    return id;
                }

            }

            for (String music : MUSIC) {
                if (ext.equals(music)) {
                    id = context.getResources().getIdentifier("ic_music",
                            "drawable", context.getPackageName());

                    return id;
                }

            }
            // video file

            for (String video : VIDEO) {
                if (ext.equals(video)) {
                    id = context.getResources().getIdentifier("ic_video",
                            "drawable", context.getPackageName());

                    return id;
                }

            }

            // words file

            for (String words : WORDSFILE) {
                if (ext.equals(words)) {
                    id = context.getResources().getIdentifier(
                            "ic_words_file", "drawable",
                            context.getPackageName());

                    return id;
                }

            }

            // compress file

            for (String compressfile : COMPRESSFILE) {
                if (ext.equals(compressfile)) {
                    id = context.getResources().getIdentifier(
                            "ic_compress_file", "drawable",
                            context.getPackageName());

                    return id;
                }

            }
            // program file


            for (String program : PROGRAM) {
                if (ext.equals(program)) {
                    id = context.getResources().getIdentifier("ic_program",
                            "drawable", context.getPackageName());

                    return id;
                }

            }

            if (ext.equals("ppt") || ext.equals("pptx")) {
                id = context.getResources().getIdentifier("ic_ppt",
                        "drawable", context.getPackageName());
                return id;

            } else if (ext.equals("doc")
                    || ext.equals("docx")) {
                id = context.getResources().getIdentifier("ic_doc",
                        "drawable", context.getPackageName());
                return id;

            } else if (ext.equals("xls")
                    || ext.equals("xlsx")) {
                id = context.getResources().getIdentifier("ic_xls",
                        "drawable", context.getPackageName());
                return id;

            }


            id = context.getResources().getIdentifier("ic_" + ext, "drawable", context.getPackageName());
            if (id > 0) {
                return id;
            }
        }
        return context.getResources().getIdentifier("ic_other", "drawable", context.getPackageName());
    }

    /**
     * Returns true if uri is a media uri.
     *
     * @param uri
     * @return
     */
    public static boolean isMediaUri(String uri) {
        if (uri.startsWith(Audio.Media.INTERNAL_CONTENT_URI.toString())
                || uri.startsWith(Audio.Media.EXTERNAL_CONTENT_URI.toString())
                || uri.startsWith(Video.Media.INTERNAL_CONTENT_URI.toString())
                || uri.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Convert Uri into File.
     *
     * @param uri
     * @return file
     */
    public static File getFile(Uri uri) {
        if (uri != null) {
            String filepath = uri.getPath();
            if (filepath != null) {
                return new File(filepath);
            }
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0,
                            pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * Constructs a file from a path and file name.
     *
     * @param curdir
     * @param file
     * @return
     */
    public static File getFile(String curdir, String file) {
        String separator = "/";
        if (curdir.endsWith("/")) {
            separator = "";
        }
        File clickedFile = new File(curdir + separator + file);
        return clickedFile;
    }

    public static File getFile(File curdir, String file) {
        return getFile(curdir.getAbsolutePath(), file);
    }

    public static final class FileInfo {
        public String filename = "";
        public String filehash = "";
        public long filesize = 0;
    }

    private static final String[] ATTACHMENT_META_COLUMNS = {
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};

    public static String getFileName(Context context, Uri uri) {
        if (uri == null) {
            return "";
        }
        String filename = "";
        if (URLUtil.isFileUrl(uri.toString())) {
            filename = uri.getLastPathSegment();
        } else {
            Cursor c = context.getContentResolver().query(uri,
                    ATTACHMENT_META_COLUMNS, null, null, null);
            if (c != null && c.moveToFirst()) {
                filename = c.getString(0);
            }
            if (c != null) {
                c.close();
            }

            if (URLUtil.isFileUrl(filename)) {
                filename = Uri.parse(filename).getLastPathSegment();
            }

        }
        return filename;
    }

    /**
     * 获取文件的名字、大小、hash
     *
     * @param context
     * @param uri
     * @return
     */
    @SuppressWarnings("deprecation")
    public static final FileInfo getFileInfo(Context context, Uri uri) {
        if (uri == null) {
            return new FileInfo();
        }
        FileInfo info = new FileInfo();
        if (URLUtil.isFileUrl(uri.toString())) {
            info.filename = uri.getLastPathSegment();
            File file = new File(URLDecoder.decode(uri.getEncodedPath()));
            if (file.exists()) {
                info.filesize = file.length();
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    info.filehash = getFileSha1(in);
                } catch (Exception e) {

                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Cursor c = context.getContentResolver().query(uri,
                    ATTACHMENT_META_COLUMNS, null, null, null);
            if (c != null && c.moveToFirst()) {
                info.filename = c.getString(0);
                InputStream in = null;
                try {
                    in = context.getContentResolver().openInputStream(uri);
                    info.filesize = in.available();
                    info.filehash = getFileSha1(in);
                } catch (Exception e) {

                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (c != null) {
                c.close();
            }

            if (URLUtil.isFileUrl(info.filename)) {
                info.filename = Uri.parse(info.filename).getLastPathSegment();
            }
        }
        return info;
    }

    /**
     * 获取文件的filehash
     */
    @SuppressWarnings("deprecation")
    public static String getFileSha1(String path) {
        String filehash = "";
        path = URLDecoder.decode(path.replace("file://", ""));
        File file = new File(path);
        if (file.exists()) {
            FileInputStream in = null;
            MessageDigest messagedigest;
            try {
                try {
                    in = new FileInputStream(file);
                    messagedigest = MessageDigest.getInstance("SHA-1");

                    byte[] buffer = new byte[1024 * 1024 * 10];
                    int len = 0;

                    while ((len = in.read(buffer)) > 0) {
                        messagedigest.update(buffer, 0, len);
                    }

                    filehash = toHexString(messagedigest.digest());
                } catch (OutOfMemoryError e) {
                    DebugFlag.log("out of memory error");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filehash;
    }

    /**
     * 获取文件的filehash
     */
    public static String getFileSha1(InputStream in) {
        String filehash = "";
        MessageDigest messagedigest;
        try {
            messagedigest = MessageDigest.getInstance("SHA-1");

            try {
                byte[] buffer = new byte[1024 * 1024 * 10];
                int len = 0;

                while ((len = in.read(buffer)) > 0) {
                    messagedigest.update(buffer, 0, len);
                }
                filehash = toHexString(messagedigest.digest());
            } catch (OutOfMemoryError e) {
                DebugFlag.log(LOG_TAG + " out of memory error");
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filehash;
    }

    private static char hexChar[] = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String toHexString(byte b[]) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0xf]);
        }
        return sb.toString();
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
     * @param context
     * @param fileLength
     * @return
     */
    public static String getFormatFileSize(Context context, long fileLength) {
        if (methodFormatterFileSize == null) {
            iniCupcakeInterface();
        }
        String result = null;
        try {
            result = (String) methodFormatterFileSize.invoke(null, context,
                    fileLength);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result == null) {
                result = Long.toString(fileLength / 1024) + "KB";
            }
        }
        return result;
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null || files.length <= 0)
                return path.delete();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());

    }

    public static long computeFileSize(File path) {
        long bytes = 0;
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null || files.length <= 0)
                return 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    bytes += computeFileSize(files[i]);
                } else {
                    bytes += files[i].length();
                }
            }
        }
        return bytes;
    }

    /**
     * 复制文件
     *
     * @param src
     * @param tar
     * @return
     */
    public static boolean copyFile(File src, File tar) {

        if (!tar.getParentFile().exists()) {
            tar.getParentFile().mkdirs();
        }

        if (!tar.exists()) {
            try {
                tar.createNewFile();
            } catch (IOException e) {

            }
        }

        try {
            if (src.isFile()) {
                InputStream is = new FileInputStream(src);
                OutputStream op = new FileOutputStream(tar);
                BufferedInputStream bis = new BufferedInputStream(is);
                BufferedOutputStream bos = new BufferedOutputStream(op);
                byte[] bt = new byte[8192];
                int len = bis.read(bt);
                while (len != -1) {
                    bos.write(bt, 0, len);
                    len = bis.read(bt);
                }
                bis.close();
                bos.close();
            }
            if (src.isDirectory()) {
                File[] f = src.listFiles();
                tar.mkdir();
                for (int i = 0; i < f.length; i++) {
                    copyFile(f[i].getAbsoluteFile(),
                            new File(tar.getAbsoluteFile() + File.separator
                                    + f[i].getName())
                    );
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 复制文件
     *
     * @param isSrc
     * @param tar
     * @return
     */
    public static boolean copyFile(InputStream isSrc, File tar) {
        try {
            if (!tar.getParentFile().isDirectory()) {
                tar.getParentFile().mkdirs();
            }

            if (!tar.exists()) {
                try {
                    tar.createNewFile();
                } catch (IOException e) {

                }
            }
            OutputStream op = new FileOutputStream(tar);
            BufferedInputStream bis = new BufferedInputStream(isSrc);
            BufferedOutputStream bos = new BufferedOutputStream(op);
            byte[] bt = new byte[8192];
            int len = bis.read(bt);
            while (len != -1) {
                bos.write(bt, 0, len);
                len = bis.read(bt);
            }
            isSrc.close();
            bis.close();
            bos.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 写文件
     *
     * @param fileName
     * @param content
     * @param codeType
     * @return isException
     */
    public static boolean writeFileData(String fileName, String content, String codeType) {
        try {
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    DebugFlag.log(UtilFile.class.getClass().getSimpleName() + ":" + e.toString());
                }
            }
            FileOutputStream fot = new FileOutputStream(fileName);
            byte[] bytes = content.getBytes(codeType);
            fot.write(bytes);
            fot.close();
        } catch (Exception e) {
            DebugFlag.log(UtilFile.class.getClass().getSimpleName() + ":" + e.toString());
            return false;
        }
        return true;
    }


    /**
     * 读文件
     *
     * @param fileName
     * @param codeType
     * @return
     */
    public static String readFileData(String fileName, String codeType) {
        String res = "";
//        String codeType = "";
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return "";
            }
//            String code = getFileEncode(fileName);
//            if (code == null) {
//                code = DEFAUT_CHARSET_ENCODING_FOR_TXT;
//            } else if (code != "GBK" && !code.contains("UTF")&&!code.contains("Unicode")) {//
//                code = DEFAUT_CHARSET_ENCODING_FOR_TXT;
//            }
//            codeType = code;
            FileInputStream fis = new FileInputStream(fileName);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            res = EncodingUtils.getString(buffer, codeType);
            fis.close();
        } catch (Exception e) {
        }
        return res;
    }


}
