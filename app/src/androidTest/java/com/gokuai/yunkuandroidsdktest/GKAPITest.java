package com.gokuai.yunkuandroidsdktest;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.gokuai.yunkuandroidsdk.FileDataManager;
import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.data.ServerListData;
import com.yunkuent.sdk.upload.UploadCallBack;

import org.junit.Assert;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Created by qp on 2017/5/27.
 * <p>
 * API接口测试
 */
public class GKAPITest extends AndroidTestCase {

    private final static String TAG = "GKAPITest";

    public void testRename() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        FileDataManager.getInstance().rename("test(100).jpg", "a3.jpg", new FileDataManager.DataListener() {
            @Override
            public void onReceiveHttpResponse(int actionId) {
                latch.countDown();
                Log.i(TAG, "addDir-->" + actionId + "");
                Assert.assertEquals(5, actionId);
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
                Assert.fail();
            }

            @Override
            public void onHookError(HookCallback.HookType type) {
                Log.e(TAG, "Hook：此操作不被允许");
                Assert.fail();
            }

            @Override
            public void onNetUnable() {
                Log.e(TAG, "Network is not available");
                Assert.fail();
            }
        });
        latch.await();
    }

    public void testGetFileInfoSync() throws Exception {

        FileData f = FileDataManager.getInstance().getFileInfoSync("test");

        Log.i(TAG, f.getFilename());

        Assert.assertEquals(200, f.getCode());


    }

    public void testGetFileInfoAsync() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        FileDataManager.getInstance().getFileInfoAsync("test", new FileDataManager.FileInfoListener() {
            @Override
            public void onReceiveData(Object data) {
                latch.countDown();
                FileData f = (FileData) data;
                Log.i(TAG, f.getFilename());
                Assert.assertEquals(200, f.getCode());
            }

            @Override
            public void onReceiveHttpResponse(int actionId) {
                Log.i(TAG, "addDir-->" + actionId + "");
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
                Assert.fail();
            }

            @Override
            public void onHookError(HookCallback.HookType type) {
                Log.e(TAG, "Hook：此操作不被允许");
                Assert.fail();
            }

            @Override
            public void onNetUnable() {
                Log.e(TAG, "Network is not available");
                Assert.fail();
            }
        });
        latch.await();
    }

    public void testAddFile() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        String path = Environment.getExternalStorageDirectory().getPath() + "/" + "test/test.jpg";

        Assert.assertEquals(true, new File(path).exists());

        FileDataManager.getInstance().addFile("addFile1.jpg", path, new UploadCallBack() {
            @Override
            public void onSuccess(long threadId, String jsonInfo) {
                latch.countDown();
                Log.i(TAG, "testAddFile-->" + jsonInfo);
                Assert.assertEquals(1, threadId);
            }

            @Override
            public void onFail(long threadId, String errorMsg) {
                Log.e(TAG, errorMsg);
                Assert.fail();
            }

            @Override
            public void onProgress(long threadId, float percent) {
                Log.i(TAG, "onProgress:" + threadId + " onProgress:" + percent * 100);
            }
        });
        latch.await();

    }

    public void testAddDir() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        FileDataManager.getInstance().addDir("addDir2", new FileDataManager.DataListener() {
            @Override
            public void onReceiveHttpResponse(int actionId) {
                latch.countDown();
                Log.i(TAG, "addDir-->" + actionId + "");
                Assert.assertEquals(1, actionId);
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
                Assert.fail();
            }

            @Override
            public void onHookError(HookCallback.HookType type) {
                Log.e(TAG, "Hook：此操作不被允许");
                Assert.fail();
            }

            @Override
            public void onNetUnable() {
                Log.e(TAG, "Network is not available");
                Assert.fail();
            }
        });
        latch.await();

    }

    public void testDel() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        FileDataManager.getInstance().del("test.jpg", new FileDataManager.DataListener() {
            @Override
            public void onReceiveHttpResponse(int actionId) {
                latch.countDown();
                Log.i(TAG, actionId + "-->" + "del");
                Assert.assertEquals(2, actionId);
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
                Assert.fail();
            }

            @Override
            public void onHookError(HookCallback.HookType type) {
                Log.e(TAG, "Hook：此操作不被允许");
                Assert.fail();
            }

            @Override
            public void onNetUnable() {
                Log.e(TAG, "Network is not available");
                Assert.fail();
            }
        });
        latch.await();

    }

    public void testFileExistInCache() throws Exception {

        boolean b = FileDataManager.getInstance().fileExistInCache("test/qq.jpg");

        Assert.assertEquals(false, b);
    }

    public void testGetPreviewServerSite() throws Exception {

        ServerListData serverSite = FileDataManager.getInstance().getPreviewServerSite();

        Assert.assertEquals(200, serverSite.getCode());
    }

    public void testGetCountOfList() throws Exception {

        int i = FileDataManager.getInstance().getCountOfList("test");

        Assert.assertEquals(true, i > -1);
    }

    public void testIsRootPath() throws Exception {

        boolean b = FileDataManager.getInstance().isRootPath("");

        Assert.assertEquals(true, b);
    }

}