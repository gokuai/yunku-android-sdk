package com.gokuai.yunkuandroidsdk;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.adapter.LocalFileListAdapter;
import com.gokuai.yunkuandroidsdk.callback.CallBack;
import com.gokuai.yunkuandroidsdk.data.LocalFileData;
import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FileUploadActivity extends BaseActivity {

    public final static String EXTRA_NAME_PATH = "path";
    public final static String EXTRA_SDCARD_PATH = "sdcardPath";
    public final static String EXTRA_UPLOAD_TYPE = "uploadType";

    private ListView mLV;
    private ArrayList<LocalFileData> mLocalFileDataList;
    private LocalFileListAdapter mLocalFileListAdapter;
    private ArrayList<LocalFileData> headerList = new ArrayList<LocalFileData>();
    private TextView mTV_empty;

    private String mPath;
    private String mSDCardPath;

    public final static int UPLOAD_TYPE_IMAGEFILE = 1;
    public final static int UPLOAD_TYPE_VIDEOFILE = 2;
    public final static int UPLOAD_TYPE_AUDIOFILE = 3;
    public final static int UPLOAD_TYPE_DOCFILE = 4;
    public final static int UPLOAD_TYPE_OTHERFILE = 5;
    private int mUploadType;
    private String mUploadTypeStrs[];

    private ArrayList<LocalFileData> mFileList;

    private ImageFetcher mImageFetcher;

    private LocalFileData mSelectedData;

    private final static int MSG_UPDATE_FIND_FILE_COUNT = 1;

    private static class MyHandler extends Handler {
        private final WeakReference<FileUploadActivity> mManager;

        public MyHandler(FileUploadActivity manager) {
            mManager = new WeakReference<>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            final FileUploadActivity manager = mManager.get();
            if (manager != null) {
                switch (msg.what) {
                    case MSG_UPDATE_FIND_FILE_COUNT:
                        TextView tv = (TextView) manager.findViewById(R.id.loading_view_progress_tv);
                        if (tv != null) {
                            tv.setVisibility(View.VISIBLE);
                            tv.setText(String.format(manager.getResources().getString(R.string.format_find_many_in_total), manager.mFileList.size()));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private Handler mHandler = new MyHandler(this);

    private AsyncTask mFindFileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //FIXME 就代码逻辑太混乱 需要优化 可以优化成异步
    private void setUpView() {
        mImageFetcher = getNewImageFetcher();
        switch (mUploadType) {
            case UPLOAD_TYPE_OTHERFILE:
                mLocalFileDataList = getFilelist(TextUtils.isEmpty(mSDCardPath) ? Config.SD_CARD_PATH : mSDCardPath);
                getSupportActionBar().setTitle(Config.CACHE_PATH);
                bindView();
                mTV_empty.setText(R.string.tip_is_loading);
                break;
            case UPLOAD_TYPE_AUDIOFILE:
                mLocalFileDataList = getAudioList();
                getSupportActionBar().setTitle(mUploadTypeStrs[mUploadType - 1]);
                bindView();
                mTV_empty.setText(R.string.tip_audio_empty);
                break;
            case UPLOAD_TYPE_IMAGEFILE:
                mLocalFileDataList = getImageList();
                getSupportActionBar().setTitle(mUploadTypeStrs[mUploadType - 1]);
                bindView();
                mTV_empty.setText(R.string.tip_img_empty);
                break;
            case UPLOAD_TYPE_VIDEOFILE:
                mLocalFileDataList = getVideoList();
                getSupportActionBar().setTitle(mUploadTypeStrs[mUploadType - 1]);
                bindView();
                mTV_empty.setText(R.string.tip_video_empty);
                break;
            case UPLOAD_TYPE_DOCFILE:
                mFileList = new ArrayList<LocalFileData>();
                setContentView(R.layout.loading_view);
                mFindFileTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        listFiles(new File(Config.SD_CARD_PATH));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (!FileUploadActivity.this.isFinishing()) {
                            bindView();
                            mTV_empty.setText(R.string.tip_doc_empty);
                        }
                    }
                }.execute();
                mLocalFileDataList = mFileList;
                getSupportActionBar().setTitle(mUploadTypeStrs[mUploadType - 1]);
                break;
            default:
                break;
        }

    }

    private void initData() {
        mUploadTypeStrs = getResources().getStringArray(R.array.upload_file_type);
        Bundle bundle = this.getIntent().getExtras();
        mPath = bundle.getString(EXTRA_NAME_PATH);
        mSDCardPath = bundle.getString(EXTRA_SDCARD_PATH);
        mUploadType = bundle.getInt(EXTRA_UPLOAD_TYPE);

    }

    private void bindView() {
        setContentView(R.layout.listview_layout_commen);
        mLV = (ListView) findViewById(R.id.list);
        mLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                LocalFileData data = (LocalFileData) arg0.getItemAtPosition(arg2);
                if (arg2 == 0 && data.getHeader()) {
                    back(data);
                    return;
                }
                openFileList(data);
            }
        });
        mLV.setEmptyView(findViewById(R.id.empty_ll));
        mTV_empty = (TextView) findViewById(R.id.empty);
        mLocalFileListAdapter = new LocalFileListAdapter(FileUploadActivity.this, mLocalFileDataList, mLV, mImageFetcher);
        mLV.setAdapter(mLocalFileListAdapter);

    }

    public void openFromPath(String path) {
        mLocalFileDataList = getFilelist(path);
        if (mLocalFileListAdapter == null) {
            mLocalFileListAdapter = new LocalFileListAdapter(FileUploadActivity.this, mLocalFileDataList, mLV, mImageFetcher);
            mLV.setAdapter(mLocalFileListAdapter);
        } else {
            mLocalFileListAdapter.setList(mLocalFileDataList, mLV);
            if (!headerList.isEmpty()) {
                mLocalFileListAdapter.addHeader(headerList.get(headerList.size() - 1));
            }
            mLV.setAdapter(mLocalFileListAdapter);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            onFinish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 返回上一层
     *
     * @param data
     */
    private void back(LocalFileData data) {
        String path = Util.getParentPath(data.getFullpath());
        if (!headerList.isEmpty()) {
            headerList.remove(headerList.size() - 1);
        }
        if (path.equals(Config.CACHE_PATH)) {
            getSupportActionBar().setTitle(path);
        } else {
            getSupportActionBar().setTitle(Util.getNameFromPath(path).substring(1));
        }
        openFromPath(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        initData();
        setUpView();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onFinish();
        } else if (item.getItemId() == R.id.btn_menu_ok) {
            if (mLocalFileDataList == null) {
                UtilDialog.showNormalToast(R.string.tip_upload_selected_file_list_error);
                return false;
            }

            if (mLocalFileDataList.size() == 0) {
                boolean isSelected = false;
                for (LocalFileData data : mLocalFileDataList) {
                    if (data.getSelected()) {
                        isSelected = true;
                        break;
                    }
                }
                if (!isSelected) {
                    UtilDialog.showNormalToast(R.string.tip_upload_selected_file_list_error);
                }
                return false;
            }

            for (LocalFileData data : mLocalFileDataList) {
                if (data.getSelected()) {
                    if (FileDataManager.getInstance().fileExistInCache(mPath + data.getFilename())) {
                        UtilDialog.showDialogSameFileExist(FileUploadActivity.this, new CallBack() {
                            @Override
                            public void call() {
                                uploadSelectedFileList();
                            }
                        });
                        return false;

                    }
                }
            }

            uploadSelectedFileList();
        }


        return false;
    }

    public void onFinish() {
        if (FileUploadManager.getInstance().isSuccess()) {
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_ACTION_ID, Constants.ACTION_ID_REFRESH);
            setResult(RESULT_OK, intent);

            FileUploadManager.getInstance().resetSuccessStatus();
        }
        finish();
    }

    /**
     * 点击文件列表的操作
     *
     * @param data
     */
    private void openFileList(LocalFileData data) {
        if (data.getDir()) {
            headerList.add(data);
            getSupportActionBar().setTitle(data.getFilename());
            openFromPath(data.getFullpath());
        } else {
            if (mLocalFileListAdapter != null) {
                mLocalFileListAdapter.clearSelects();
                data.setSelected(!data.getSelected());
                mSelectedData = data;
                mLocalFileListAdapter.updateSelect();
            }
        }
    }

    /**
     * 获取文件列表
     *
     * @param path
     * @return
     */
    private ArrayList<LocalFileData> getFilelist(String path) {
        File file = new File(path);
        if (file.list() == null) {
            return null;
        }
        ArrayList<LocalFileData> list = new ArrayList<LocalFileData>();
        LocalFileData mLocalFileData = null;
        int count = file.list().length;
        for (int i = 0; i < count; i++) {
            File childFile = new File(path, file.list()[i]);
            mLocalFileData = new LocalFileData(childFile.getName(), childFile.length(), childFile.getPath(), childFile.isDirectory(), childFile.lastModified());
            list.add(mLocalFileData);
        }
        FileComparator comparator = new FileComparator();
        Collections.sort(list, comparator);
        return list;
    }

    private ArrayList<LocalFileData> getImageList() {
        String[] imagePathColumn = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imagePathColumn, null, null, MediaStore.Images.Media._ID);
        ArrayList<LocalFileData> list = new ArrayList<LocalFileData>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File file = new File(data);
                    list.add(new LocalFileData(file.getName(), file.length(), file.getPath(), file.isDirectory(), file.lastModified()));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        return list;
    }

    private ArrayList<LocalFileData> getVideoList() {
        String[] videoPathColumn = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA};

        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoPathColumn, null, null, MediaStore.Video.Media._ID);
//        mImageList=new ArrayList<GridItemData>();
        ArrayList<LocalFileData> list = new ArrayList<LocalFileData>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    File file = new File(data);
                    list.add(new LocalFileData(file.getName(), file.length(), file.getPath(), file.isDirectory(), file.lastModified()));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }

        return list;
    }


    private ArrayList<LocalFileData> getAudioList() {
        String[] videoPathColumn = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA};

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, videoPathColumn, null, null, MediaStore.Audio.Media._ID);
//        mImageList=new ArrayList<GridItemData>();
        ArrayList<LocalFileData> list = new ArrayList<LocalFileData>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    File file = new File(data);
                    list.add(new LocalFileData(file.getName(), file.length(), file.getPath(), file.isDirectory(), file.lastModified()));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        return list;
    }

    /**
     * @param sdcard
     * @return find 1 file
     */
    private void listFiles(File sdcard) {
        if (sdcard.isDirectory()) {
            File[] files = sdcard.listFiles();
            try {
                for (File f : files) {
                    if (FileUploadActivity.this.isFinishing()) {
                        break;
                    }
                    if (!f.isDirectory()) {
                        if (f.getName().endsWith(".doc") || f.getName().endsWith(".txt")
                                || f.getName().endsWith(".docx") || f.getName().endsWith(".rtf") || f.getName().endsWith(".xls")
                                || f.getName().endsWith(".xlsx")) {
                            File file = new File(f.getAbsolutePath());
                            mFileList.add(new LocalFileData(file.getName(), file.length(), file.getPath(), file.isDirectory(), file.lastModified()));
                            mHandler.removeMessages(MSG_UPDATE_FIND_FILE_COUNT);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FIND_FILE_COUNT);
                        }
                    } else {
                        this.listFiles(f);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFindFileTask != null) {
            mFindFileTask.cancel(true);
        }
    }

    /**
     * 文件比较
     */
    public static class FileComparator implements Comparator<Object> {

        @Override
        public int compare(Object object1, Object object2) {
            LocalFileData file1 = (LocalFileData) object1;
            LocalFileData file2 = (LocalFileData) object2;
            if (file1.getDir() != file2.getDir()) {
                return file1.getDir() ? -1 : 1;
            } else {
                return file1.getFilename().compareTo(file2.getFilename());
            }
        }
    }


    /**
     * 上传选中的文件
     */
    private void uploadSelectedFileList() {
        FileUploadManager.getInstance().upload(this, mPath, mSelectedData);
    }


}
