package com.gokuai.yunkuandroidsdk.callback;

/**
 * Hook回调类
 */
public interface HookCallback {

    enum HookType {
        HOOK_TYPE_FILE_LIST,//列表显示
        HOOK_TYPE_DOWNLOAD,//文件下载
        HOOK_TYPE_UPLOAD,//文件上传
        HOOK_TYPE_CREATE_DIR,//文件夹创建
        HOOK_TYPE_RENAME,//文件重命名
        //        HOOK_TYPE_MOVE,
//        HOOK_TYPE_COPY,
        HOOK_TYPE_DELETE,//文件删除
    }

    /**
     * hook回调
     *
     * @param type
     * @param fullPath 执行操作的路径
     * @return 是否允许操作
     */
    boolean hookInvoke(HookType type, String fullPath);


}
