package com.gokuai.yunkuandroidsdk.data;

/**
 * Created by Brandon on 15/6/28.
 */
public class FileDataKey {

    public FileDataKey( int start,String fullPath) {
        this.fullPath = fullPath;
        this.start = start;
    }

    String fullPath;
    int start;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileDataKey)) return false;

        FileDataKey that = (FileDataKey) o;

        if (start != that.start) return false;
        return !(fullPath != null ? !fullPath.equals(that.fullPath) : that.fullPath != null);

    }

    @Override
    public int hashCode() {
        int result = fullPath != null ? fullPath.hashCode() : 0;
        result = 31 * result + start;
        return result;
    }
}
