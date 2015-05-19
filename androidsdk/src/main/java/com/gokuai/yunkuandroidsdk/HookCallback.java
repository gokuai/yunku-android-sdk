package com.gokuai.yunkuandroidsdk;

/**
 * Created by Brandon on 15/4/8.
 */
public interface HookCallback {

    enum HookType{
        HOOK_TYPE_FILE_LIST,
        HOOK_TYPE_DOWNLOAD,
        HOOK_TYPE_UPLOAD,
        HOOK_TYPE_CREATE_DIR,
        HOOK_TYPE_RENAME,
//        HOOK_TYPE_MOVE,
//        HOOK_TYPE_COPY,
        HOOK_TYPE_DELETE,
    }

    boolean hookInvoke(HookType type, String fullPath);



}
