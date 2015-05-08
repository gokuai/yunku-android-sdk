package com.gokuai.yunkuandroidsdk.callback;

/**
 * Created by Brandon on 15/4/8.
 */
public interface HookCalback {

    enum HookType{
        HOOK_TYPE_FILE_LIST,
        HOOK_TYPE_FILE_VIEW,
        HOOK_TYPE_DOWNLOAD,
        HOOK_TYPE_UPLOAD,
        HOOK_TYPE_RENAME,
        HOOK_TYPE_MOVE,
        HOOK_TYPE_COPY,
    }

    public boolean hookInvoke(HookType type, String... params);



}
