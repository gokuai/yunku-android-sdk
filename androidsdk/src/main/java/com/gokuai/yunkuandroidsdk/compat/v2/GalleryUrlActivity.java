package com.gokuai.yunkuandroidsdk.compat.v2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.WindowCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.BaseActivity;
import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.gallery.touchwedgit.GalleryViewPager;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;
import com.gokuai.yunkuandroidsdk.util.UtilFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 图片类型预览
 */
public class GalleryUrlActivity extends BaseActivity implements View.OnClickListener, FileDataManager.FileDataListener {

    public final static String EXTRA_LOCAL_FILE_PATH = "localFilePath";

    private TextView mTv_Desc;
    private GalleryViewPager mViewPager;
    public YKMainView mYKMainView;
    private int mLocalIndex = -1;
    private ArrayList<FileData> mImageFileList;

    private int mGalleryMode;
    private UrlPagerAdapter mPagerAdapter;
    private boolean hasDetail = true;

    private AsyncTask mDeleteTask;

    private int mPathFileCount;


    public static final int GALLERY_PAGE_SIZE = 100;//默认列表文件数量

    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gallery, menu);
        setUpView();
        initData();
        menu.findItem(R.id.btn_send_another_app).setVisible(true);
        menu.findItem(R.id.btn_add_cellphone_photo).setVisible(true);
        menu.findItem(R.id.btn_delete).setVisible(true);
        menu.findItem(R.id.btn_menu_function).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final FileData data = mImageFileList.get(mPagerAdapter.getCurrentPosition());

        int i = item.getItemId();
        if (i == R.id.btn_delete) {
            mDeleteTask = FileDataManager.getInstance().del(data.getFullpath(), GalleryUrlActivity.this);
        } else if (i == R.id.btn_send_another_app) {
            FileOpenManager.getInstance().handle(GalleryUrlActivity.this, data);
        } else if (i == R.id.btn_add_cellphone_photo) {

            new AsyncTask<Void, Void, Boolean>() {

                boolean isSaved = true;

                @Override
                protected Boolean doInBackground(Void... voids) {

                    final String imagePath = Config.getBigThumbPath(data.getFilehash());
                    File file = new File(imagePath);

                    if (file.exists()) {
                        Bitmap bitmap = Util.decodeSampledBitmapFromFile(file);
                        if (bitmap != null) {
                            isSaved = true;
                            String savepath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, data.getFilename(), null);
                            Uri uri = Uri.parse(savepath);
                            String bitmappath = getFilePathByContentResolver(GalleryUrlActivity.this, uri);

                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(bitmappath))));


                            return isSaved;
                        } else {
                            isSaved = false;
                            file.delete();
                            return isSaved;
                        }
                    } else {
                        if (!file.getParentFile().isDirectory()) {
                            file.getParentFile().mkdirs();
                            isSaved = false;
                            return isSaved;
                        }
                    }
                    return isSaved;
                }


                @Override
                protected void onPostExecute(Boolean save) {
                    super.onPostExecute(save);

                    if (save) {
                        UtilDialog.showNormalToast(R.string.toast_add_cellphone_photo_success);
                    } else {
                        UtilDialog.showNormalToast(R.string.toast_add_cellphone_photo_fail);
                    }
                }
            }.execute();


        } else if (i == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /*
  根据Uri获取加入到手机相册的图片名字，该名字有手机系统自动分配
   */
    private String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }


    /*
      点击触发状态栏的显示和隐藏
     */
    @Override
    public void onClick(View view) {
        if (getSupportActionBar().isShowing()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            getSupportActionBar().hide();
            onTimerEventChange(false);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            getSupportActionBar().show();
            onTimerEventChange(true);
        }

    }

    protected void onTimerEventChange(boolean show) {
        setDescriptionVisible(show);
    }

    public void setDescriptionVisible(boolean visible) {
        if (mTv_Desc != null) {
            mTv_Desc.setVisibility(visible && hasDetail ? View.VISIBLE : View.GONE);
        }
    }


    /*
    在改变屏幕方向等配置改变，执行该方法，不重新创建activity
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
                || newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mViewPager != null) {
                mPagerAdapter.notifyDataSetChanged();
                UrlTouchImageView urlImageView = (UrlTouchImageView) mViewPager
                        .findViewWithTag("iv" + mPagerAdapter.getCurrentPosition());
                int index = mPagerAdapter.getCurrentPosition();
                urlImageView.setUrl(mImageFileList.get(index));
            }
        }
    }

    private void setUpView() {
        setContentView(R.layout.gallery_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.gallery_title_view);
        getSupportActionBar().show();
        getSupportActionBar().setSplitBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_f)));
        getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_f)));
        mTv_Desc = (TextView) findViewById(R.id.gallery_lasttime_desc_tv);
    }


    private void initData() {

        Intent intent = getIntent();
        mYKMainView = new YKMainView(this);
        String initCurrentFilePath = intent.getStringExtra(EXTRA_LOCAL_FILE_PATH);
        //int mountId = intent.getIntExtra(Constants.EXTRA_MOUNT_ID, -1);

        mGalleryMode = intent.getIntExtra(Constants.EXTRA_GALLERY_MODE, 0);
        //mMountPropertyData = intent.getParcelableExtra(Constants.EXTRA_KEY_MOUNT_PROPERTY_DATA);
        //mEntId = intent.getIntExtra(Constants.EXTRA_ENT_ID, 0);

        ArrayList<String> imgUrlList = new ArrayList<String>();

        if (mGalleryMode == Constants.EXTRA_GALLERY_MODE_LIST) {

            ArrayList<FileData> imgFileList = new ArrayList<FileData>();
            String parentPath = Util.getParentPath(initCurrentFilePath);
            if (!parentPath.equals("") && !parentPath.endsWith("/")) {
                parentPath = parentPath + "/";
            }

            mPathFileCount = FileDataManager.getInstance().getCountOfList(parentPath);
            HashMap<Integer, ArrayList<FileData>> mListMap = new HashMap<Integer, ArrayList<FileData>>();
            //ArrayList<Object> mList = null;
            for (int i = 0, j = 0; i <= mPathFileCount; i += GALLERY_PAGE_SIZE, j++) {
                ArrayList<FileData> list = FileDataManager.getInstance().getFilesFromPath(i, parentPath);
                if (list.size() == 0) {
                    //UtilDialog.showNormalToast(R.string.tip_file_not_exist);
                    break;
                } else {
                    //mList.add(list);
                    mListMap.put(j, list);
                }
            }

            for (int i = 0; i < mListMap.size(); i++) {
                ArrayList<FileData> mList;
                mList = mListMap.get(i);
                if (mList.size() == 0) {
                    UtilDialog.showNormalToast(R.string.tip_file_not_exist);
                    finish();
                    return;
                } else {
                    for (FileData fileData : mList) {
                        if (fileData.getDir() != FileData.DIRIS) {
                            if (UtilFile.isImageFile(fileData.getFilename())) {
                                imgUrlList.add(fileData.getFullpath());
                                imgFileList.add(fileData);
                            }
                        }
                    }

                    mLocalIndex = imgUrlList.indexOf(initCurrentFilePath);
                    mImageFileList = imgFileList;
                }
            }


        }

        if (mImageFileList.size() > 0) {
            bindView();
        } else {
            UtilDialog.showNormalToast(R.string.tip_file_not_exist);
        }

    }


    private void bindView() {

        mTv_Desc.setVisibility(hasDetail ? View.VISIBLE : View.GONE);

        mPagerAdapter = new UrlPagerAdapter(this, mImageFileList, mLocalIndex, mGalleryMode, this);
        mPagerAdapter.setOnItemChangeListener(new BasePagerAdapter.OnItemChangeListener() {

            @Override
            public void onItemChange(int currentPosition) {
                mLocalIndex = currentPosition;
                refreshTitleAndIndex(currentPosition);
            }
        });

        mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(
                R.dimen.view_pager_margin));
        mViewPager.setCurrentItem(mLocalIndex, false);

    }


    private void refreshTitleAndIndex(int currentPosition) {
        View view = getSupportActionBar().getCustomView();
        String index = (currentPosition + 1) + " of " + mImageFileList.size();
        String title = mImageFileList.get(currentPosition).getFilename();
        FileData data = mImageFileList.get(currentPosition);

        ((TextView) view.findViewById(R.id.gallery_indexer_tv))
                .setText(index);
        ((TextView) view.findViewById(R.id.gallery_title_tv))
                .setText(title);
        mTv_Desc.setText(String.format(getString(R.string.format_the_lasttime_fixed_desc),
                data.getLastMemberName(), Util.formateTime(data.getDateline() * 1000, Util.TIMEFORMAT_YEAR_MONTH_DAY, this)));

    }


    @Override
    public void onReceiveHttpResponse(int actionId) {

        if (actionId == FileDataManager.ACTION_ID_DELETE) {
            FileData fileData = mImageFileList.get(mLocalIndex);

            ArrayList<FileData> imageFileList = mImageFileList;
            for (int i = 0; i < imageFileList.size(); i++) {
                if (imageFileList.get(i).getFullpath().equals(fileData.getFullpath())) {
                    imageFileList.remove(i);
                    break;
                }
            }

            //删除页面上的数据
            if (mImageFileList.size() == 0) {
                finish();
            } else {
                if (mLocalIndex >= mImageFileList.size()) {
                    mLocalIndex--;
                }
                bindView();
            }

            //刷新根目录
            mYKMainView.refresh();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDeleteTask != null) {
            mDeleteTask.cancel(true);
        }
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

    @Override
    public void onReceiveCacheData(int start, ArrayList<FileData> list) {

    }

    @Override
    public void onReceiveHttpData(ArrayList<FileData> list, int start, String parentPath) {

    }
}
