package com.gokuai.yunkuandroidsdk.compat.v2;

import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.ServerListData;
import com.yunkuent.sdk.upload.UploadCallBack;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by qp on 2017/5/11.
 */
public class FileDataManagerTest {

    //FIXME : IS FOR ANDROID
    @Ignore
    @Test
    public void rename() throws Exception {

        final CountDownLatch Latch = new CountDownLatch(1);
        FileDataManager.getInstance().rename("aa.jpg", "hh", new FileDataManager.DataListener() {
            @Override
            public void onReceiveHttpResponse(int actionId) {
                Assert.assertEquals(5, actionId);
                Latch.countDown();
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
        Latch.await();
    }

    @Ignore
    @Test
    public void addDir() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        FileDataManager.getInstance().addDir("addDir", new FileDataManager.DataListener() {
            @Override
            public void onReceiveHttpResponse(int actionId) {
                Assert.assertEquals(1,actionId);
                latch.countDown();
            }

            @Override
            public void onError(String errorMsg) {
                Assert.fail();
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
        latch.await(10, TimeUnit.SECONDS);
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
    public void getFileInfoSync() throws Exception {

        FileData f = FileDataManager.getInstance().getFileInfoSync("test");


        Assert.assertEquals(200, f.getCode());
    }

    @Test
    public void addFile() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        Assert.assertEquals(true,new File("/Users/qp/Desktop/test.xlsx").exists());
        FileDataManager.getInstance().addFile("addFile.xlsx", "/Users/qp/Desktop/test.xlsx", new UploadCallBack() {
            @Override
            public void onSuccess(long threadId, String jsonInfo) {
                latch.countDown();
                Assert.assertEquals(1,threadId);
                System.out.println("success:" + threadId);
            }

            @Override
            public void onFail(long threadId, String errorMsg) {
                Assert.fail();
                System.out.println("fail:" + threadId + " errorMsg:" + errorMsg);
            }

            @Override
            public void onProgress(long threadId, float percent) {
                System.out.println("onProgress:" + threadId + " onProgress:" + percent * 100);
            }
        });
        latch.await();
    }

    @Test
    public void fileExistInCache() throws Exception {

        boolean b = FileDataManager.getInstance().fileExistInCache("aa.jpg");
        Assert.assertEquals(false, b);
    }

    @Test
    public void getPreviewServerSite() throws Exception {

        ServerListData serverSite = FileDataManager.getInstance().getPreviewServerSite();
        Assert.assertEquals(200, serverSite.getCode());
    }

    @Test
    public void getCountOfList() throws Exception {

        int i = FileDataManager.getInstance().getCountOfList("test");
        Assert.assertEquals(true, i > -1);
    }

    @Test
    public void isRootPath() throws Exception {

        boolean b = FileDataManager.getInstance().isRootPath("");
        Assert.assertEquals(true, b);
    }

}