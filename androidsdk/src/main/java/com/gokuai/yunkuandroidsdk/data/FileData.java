package com.gokuai.yunkuandroidsdk.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.gokuai.base.ReturnResult;
import com.gokuai.yunkuandroidsdk.util.FirstLetterUtil;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilFile;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * 用来显示在文件列表中的每一条数据
 */
public class FileData extends BaseData implements Parcelable {

    public final static int DIRIS = 1;

    private final static String KEY_HASH = "hash";
    private final static String KEY_FILENAME = "filename";
    private final static String KEY_FILEHASH = "filehash";
    private final static String KEY_FILESIZE = "filesize";
    private final static String KEY_FULLPATH = "fullpath";
    private final static String KEY_DIR = "dir";
    private final static String KEY_CMD = "cmd";
    private final static String KEY_LASTDATELINE = "last_dateline";
    private final static String KEY_LASTMEMBERID = "last_member_id";
    private final static String KEY_LASTMEMBERNAME = "last_member_name";
    private final static String KEY_PREVIEW = "preview";


    // FileUpdateData KEY
    private final static String KEY_VERSION = "version";
    private final static String KEY_URI = "uri";
    private final static String KEY_URIS = "uris";
    private final static String KEY_CREATE_ID = "create_member_id";
    private final static String KEY_CREATE_TIME = "create_dateline";
    private final static String KEY_CREATE_NAME = "create_member_name";
    private final static String KEY_LOCK = "lock";
    private final static String KEY_THUMBNAIL = "thumbnail";

    private String upFullPath;
    private String filename = "";
    private String filehash = "";
    private long filesize = 0;
    private String fullpath = "";
    private int dir;
    private int cmd;// 只标识正在上传，和下载没有关系，
    private long dateline = 0;// 文件上传的时间
    private int lastMemberId;
    private boolean isHeader = false;// 在listview中是否为第一个view
    private boolean isFooter = false;
    private boolean selected = false;
    private String thumbSmall = "";
    private String thumbBig = "";
    private String version = "";
    private String uuidHash = "";
    private String createMemberName = "";
    private int createId;
    private long createTime;
    private String lastMemberName = "";
    private String[] uris;
    private String preview;

    private String uri;

    private String fileUri = "";

    private boolean isSync;//只有上传的时候会用到
    private int lock;//锁定

    private long photoDateline;
    private String property;

    private String firstLetter;

    private String collectionType;

    public FileData() {

    }


    public static FileData createHeadData() {
        FileData data = new FileData();
        data.isHeader = true;
        return data;
    }

    public static FileData createFootData() {
        FileData data = new FileData();
        data.isFooter = true;
        return data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(upFullPath);
        dest.writeString(filename);
        dest.writeString(filehash);
        dest.writeLong(filesize);
        dest.writeString(fullpath);
        dest.writeInt(dir);
        dest.writeInt(cmd);
        dest.writeLong(dateline);
        dest.writeInt(lastMemberId);
        dest.writeByte((byte) (isHeader ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (isSync ? 1 : 0));
        dest.writeString(thumbSmall);
        dest.writeString(version);
        dest.writeString(uuidHash);
        dest.writeString(createMemberName);
        dest.writeInt(createId);
        dest.writeLong(createTime);
        dest.writeString(lastMemberName);
        dest.writeString(uri);
        dest.writeString(fileUri);
        dest.writeLong(photoDateline);
        dest.writeString(firstLetter);
        dest.writeInt(ext);
        dest.writeInt(lock);
        dest.writeString(property);
        dest.writeString(collectionType);
        dest.writeString(preview);
    }

    public static final Creator<FileData> CREATOR = new Creator<FileData>() {

        @Override
        public FileData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            FileData cus = new FileData();
            cus.upFullPath = source.readString();
            cus.filename = source.readString();
            cus.filehash = source.readString();
            cus.filesize = source.readLong();
            cus.fullpath = source.readString();
            cus.dir = source.readInt();
            cus.cmd = source.readInt();
            cus.dateline = source.readLong();
            cus.lastMemberId = source.readInt();
            cus.isHeader = source.readByte() != 0;
            cus.selected = source.readByte() != 0;
            cus.isSync = source.readByte() != 0;
            cus.thumbSmall = source.readString();
            cus.version = source.readString();
            cus.uuidHash = source.readString();
            cus.createMemberName = source.readString();
            cus.createId = source.readInt();
            cus.createTime = source.readLong();
            cus.lastMemberName = source.readString();
            cus.uri = source.readString();
            cus.fileUri = source.readString();
            cus.photoDateline = source.readLong();
            cus.firstLetter = source.readString();
            cus.ext = source.readInt();
            cus.lock = source.readInt();
            cus.property = source.readString();
            cus.collectionType = source.readString();
            cus.preview = source.readString();
            return cus;
        }

        @Override
        public FileData[] newArray(int size) {

            // TODO Auto-generated method stub
            return new FileData[size];
        }

    };


    public static FileData create(JSONObject json) {
        FileData data = new FileData();
        data.setUuidHash(json.optString(KEY_HASH));
        data.setFilehash(json.optString(KEY_FILEHASH));
        data.setFilesize(json.optLong(KEY_FILESIZE));
        data.setFullpath(json.optString(KEY_FULLPATH));
        data.setFilename(json.optString(KEY_FILENAME));
        data.setDir(json.optInt(KEY_DIR));
        data.setCmd(json.optInt(KEY_CMD));
        data.setDateline(json.optLong(KEY_LASTDATELINE));
        data.setLastMemberId(json.optInt(KEY_LASTMEMBERID, -1));
        data.setLastMemberName(json.optString(KEY_LASTMEMBERNAME));
        data.setVersion(json.optString(KEY_VERSION));
        data.setCreateId(json.optInt(KEY_CREATE_ID));
        data.setCreateTime(json.optInt(KEY_CREATE_TIME));
        data.setCreateMemberName(json.optString(KEY_CREATE_NAME));
        data.setUpFullpath(Util.getParentPath(data.getFullpath()));
        data.setLock(json.optInt(KEY_LOCK));
        data.thumbSmall = json.optString(KEY_THUMBNAIL);
        data.preview = json.optString(KEY_PREVIEW);
        return data;
    }

    public static FileData create(String result) {
        FileData data = new FileData();

        ReturnResult returnResult = ReturnResult.create(result);
        int code = returnResult.getStatusCode();
        data.setCode(code);
        JSONObject json;
        try {
            json = new JSONObject(returnResult.getResult());
        } catch (Exception e) {
            json = null;
        }


        if (json != null) {
            if (code == HttpStatus.SC_OK) {
                data.setUuidHash(json.optString(KEY_HASH));
                data.setFilehash(json.optString(KEY_FILEHASH));
                data.setFilesize(json.optLong(KEY_FILESIZE));
                data.setFullpath(json.optString(KEY_FULLPATH));
                data.setFilename(json.optString(KEY_FILENAME));
                data.setDir(json.optInt(KEY_DIR));
                data.setCmd(json.optInt(KEY_CMD));
                data.setDateline(json.optLong(KEY_LASTDATELINE));
                data.setLastMemberId(json.optInt(KEY_LASTMEMBERID, -1));
                data.setLastMemberName(json.optString(KEY_LASTMEMBERNAME));
                data.setVersion(json.optString(KEY_VERSION));
                data.setCreateId(json.optInt(KEY_CREATE_ID));
                data.setCreateTime(json.optInt(KEY_CREATE_TIME));
                data.setCreateMemberName(json.optString(KEY_CREATE_NAME));
                data.setUpFullpath(Util.getParentPath(data.getFullpath()));
                data.setLock(json.optInt(KEY_LOCK));
                data.setUri(json.optString(KEY_URI));
                data.thumbSmall = json.optString(KEY_THUMBNAIL);
                data.preview = json.optString(KEY_PREVIEW);
            } else {
                String errorMsg = json.optString(KEY_ERRORMSG);
                int errorCode = json.optInt(KEY_ERRORCODE);
                data.setErrorMsg(errorMsg);
                data.setErrorCode(errorCode);
            }

        }


        return data;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilehash() {
        return filehash;
    }

    public void setFilehash(String filehash) {
        this.filehash = filehash;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public String getFullpath() {
        if (dir == 1) {
            return fullpath + (fullpath.endsWith("/") ? "" : "/");
        }
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public long getDateline() {
        return dateline;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }

    public int getLastMemberId() {
        return lastMemberId;
    }

    public void setLastMemberId(int lastMemberId) {
        this.lastMemberId = lastMemberId;
    }

    public String getLastMemberName() {
        return lastMemberName;
    }

    public void setLastMemberName(String lastMemberName) {
        this.lastMemberName = lastMemberName;
    }


    public boolean isHeader() {
        return isHeader;
    }

    public void setSelected(boolean Selected) {
        this.selected = Selected;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public boolean getSelected() {
        return selected;
    }

    public String getUpFullpath() {
        //在fullpath末尾加斜杆，如果有为空或者为null则不加
        if (upFullPath == null) {
            if (fullpath != null) {
                upFullPath = Util.getParentPath(fullpath);
            }
        }
        return !TextUtils.isEmpty(upFullPath) && !upFullPath.endsWith("/") ? upFullPath + "/" : upFullPath;
    }

    public void setUpFullpath(String upFullpath) {
        this.upFullPath = upFullpath;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(uuidHash))
            return "".hashCode();

        return uuidHash.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileData))
            return false;

        if (TextUtils.isEmpty(uuidHash))
            return false;

        if (uuidHash.equals(((FileData) o).getUuidHash()))
            return true;
        return false;
    }

    //上传的文件，需要刷新列表才能获取到
    public String getThumbSmall() {
        return thumbSmall;
    }

    public String getThumbBig() {
        if (TextUtils.isEmpty(thumbBig)) {
            thumbBig = getThumbSmall() + "&big=1";
        }
        return thumbBig;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public String getUuidHash() {
        return uuidHash;
    }

    public void setUuidHash(String uuidHash) {
        this.uuidHash = uuidHash;
    }

    public String[] getUris() {
        return uris;
    }

    public void setUris(String[] uris) {
        this.uris = uris;
    }

    public String getCreateMemberName() {
        return createMemberName;
    }

    public void setCreateMemberName(String createMemberName) {
        this.createMemberName = createMemberName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getCreateId() {
        return createId;
    }

    public void setCreateId(int createId) {
        this.createId = createId;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean isSync) {
        this.isSync = isSync;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public long getPhotoDateline() {
        return photoDateline;
    }

    public void setPhotoDateline(long photoDateline) {
        this.photoDateline = photoDateline;
    }

    private int ext;

    public int getExt(Context context) {
        if (ext == 0 && filename != null) {
            ext = UtilFile.getExtensionIcon(context, filename);
        }
        return ext;
    }

    public String getFirstLetters() {
        if (TextUtils.isEmpty(firstLetter)) {
            firstLetter = FirstLetterUtil.getFirstLetter(filename);
        }
        return firstLetter;
    }

    public int getFirstCharacterType() {
        return Character.getType(filename.charAt(0)) == Character.OTHER_LETTER ? 1 : -1;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public boolean isFooter() {
        return isFooter;
    }
}
