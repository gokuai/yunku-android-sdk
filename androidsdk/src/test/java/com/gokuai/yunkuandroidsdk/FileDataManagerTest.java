package com.gokuai.yunkuandroidsdk;

import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.ServerListData;
import com.yunkuent.sdk.upload.UploadCallBack;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by qp on 2017/5/11.
 */
public class FileDataManagerTest {

    @Ignore
    @Test
    public void rename() throws Exception {


        FileDataManager.getInstance().rename("aa", "bb", new FileDataManager.DataListener() {
            @Override
            public void onReceiveHttpResponse(int actionId) {
                Assert.assertEquals(5, actionId);
            }

            @Override
            public void onError(String errorMsg) {
                Assert.fail(errorMsg);
            }

            @Override
            public void onHookError(HookCallback.HookType type) {
                Assert.fail("Hook：此操作不被允许");

            }

            @Override
            public void onNetUnable() {
                Assert.fail("Network is not available");
            }
        });
    }

    @Test
    public void getFileInfoSync() throws Exception {

        FileData f = FileDataManager.getInstance().getFileInfoSync("test");

        Assert.assertEquals(200, f.getCode());
    }

    @Ignore
    @Test
    public void addFile() throws Exception {

        FileDataManager.getInstance().addFile("", "", new UploadCallBack() {
            @Override
            public void onSuccess(long threadId, String jsonInfo) {

            }

            @Override
            public void onFail(long threadId, String errorMsg) {

            }

            @Override
            public void onProgress(long threadId, float percent) {

            }
        });
    }

    @Ignore
    @Test
    public void addDir() throws Exception {

        FileDataManager.getInstance().addDir("", new FileDataManager.DataListener() {
            @Override
            public void onReceiveHttpResponse(int actionId) {

            }

            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onHookError(HookCallback.HookType type) {

            }

            @Override
            public void onNetUnable() {

            }
        });
    }

    @Test
    public void fileExistInCache() throws Exception {

        boolean b = FileDataManager.getInstance().fileExistInCache("aa.jpg");
        Assert.assertEquals(true, b);
    }

    @Ignore
    @Test
    public void getFileList() throws Exception {

        FileDataManager.getInstance().getFileList("", new FileDataManager.FileDataListener() {
            @Override
            public void onReceiveCacheData(int start, ArrayList<FileData> list) {

            }

            @Override
            public void onReceiveHttpData(ArrayList<FileData> list, int start, String parentPath) {

            }

            @Override
            public void onReceiveHttpResponse(int actionId) {

            }

            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onHookError(HookCallback.HookType type) {

            }

            @Override
            public void onNetUnable() {

            }
        }, 0);

    }

    @Test
    public void getPreviewServerSite() throws Exception {

        ServerListData serverSite = FileDataManager.getInstance().getPreviewServerSite();
        Assert.assertEquals(200, serverSite.getCode());
    }

    @Test
    public void getCountOfList() throws Exception {

        int i = FileDataManager.getInstance().getCountOfList("");
        Assert.assertEquals(true, i > -1);
    }

    @Test
    public void isRootPath() throws Exception {

        boolean b = FileDataManager.getInstance().isRootPath("");
        Assert.assertEquals(true, b);
    }

}