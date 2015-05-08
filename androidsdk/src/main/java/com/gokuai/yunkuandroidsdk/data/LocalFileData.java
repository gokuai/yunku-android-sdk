package com.gokuai.yunkuandroidsdk.data;

import android.text.TextUtils;

/**
 * 用来显示在本地文件列表中的每一条数据
 */
public class LocalFileData {
    public final static int DirIs = 1;
    public final static int DirNot = 0;

    private String filename = "";
    private long filesize = 0;
    private String fullpath = "";
    private boolean dir;
    private long filedate;
    private boolean isHeader = false;
    private boolean selected = false;
    private boolean isFavourate = false;

    /**
     * @param filename
     * @param filesize
     * @param fullpath
     * @param dir
     * @param filedate
     */
    public LocalFileData(String filename, long filesize, String fullpath, boolean dir, long filedate) {
        this.filename = filename;
        this.filesize = filesize;
        this.fullpath = fullpath;
        this.dir = dir;
        this.filedate = filedate / 1000;//seconds
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public String getFullpath() {
        if (dir) {
            return fullpath + "/";
        }
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public boolean getDir() {
        return dir;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public long getFiledate() {
        return filedate;
    }

    public void setDateline(long dateline) {
        this.filedate = dateline;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public boolean getHeader() {
        return isHeader;
    }

    public void setSelected(boolean Selected) {
        this.selected = Selected;
    }

    public boolean getSelected() {
        return selected;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(fullpath))
            return "".hashCode();

        return fullpath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LocalFileData))
            return false;

        if (TextUtils.isEmpty(fullpath))
            return false;

        if (fullpath.equals(((LocalFileData) o).getFullpath()))
            return true;
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("filename is:" + filename + "\n");
        sb.append("filesize is:" + filesize + "\n");
        sb.append("fullpath is:" + fullpath + "\n");
        sb.append("filedate is:" + filedate + "\n");
        sb.append("dir is:" + dir + "\n");
        return sb.toString();
    }

    public boolean isFavourate() {
        return isFavourate;
    }

    public void setFavourate(boolean isFavourate) {
        this.isFavourate = isFavourate;
    }
}
