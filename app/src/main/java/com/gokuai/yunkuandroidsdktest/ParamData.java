package com.gokuai.yunkuandroidsdktest;

import java.io.Serializable;

/**
 * Created by Brandon on 15/5/19.
 */
public class ParamData implements Serializable {

    public boolean funcDelete;
    public boolean funcRename;
    public boolean funcUpload;

    public String hookPath;
    public boolean hookList;
    public boolean hookDownload;
    public boolean hookUpload;
    public boolean hookCreateDir;
    public boolean hookRename;
    public boolean hookDelete;

    public String clientId;
    public String clientSecret;
    public String rootPath;
    public String rootTitle;



}
