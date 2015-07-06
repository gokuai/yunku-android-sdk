package com.gokuai.yunkuandroidsdk;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.adapter.FileListAdapter;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.dialog.NewFolderDialogManager;
import com.gokuai.yunkuandroidsdk.dialog.RenameDialogManager;
import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;
import com.gokuai.yunkuandroidsdk.imageutils.Utils;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;
import com.gokuai.yunkuandroidsdk.util.UtilFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Brandon on 15/4/10.
 */
public class YKMainView extends LinearLayout implements FileListAdapter.FileItemClickListener, AbsListView.OnScrollListener, View.OnClickListener, FileDataManager.FileDataListener, SwipeRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener {

    private Context mContext;
    private ListView mLV_FileList;
    private TextView mTV_CloudEmpty;
    private FileListAdapter mFileListAdapter;
    private ImageFetcher mImageFetcher;
    private String mPath = "";
    private View mReturnViewInEmpty;
    private String mCloudName = Config.ORG_ROOT_TITLE;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isRefreshAction;
    private Option mOption;

    private final ArrayList<Integer> mPositionPopStack = new ArrayList<>();
    private String mRedirectPath;


    public YKMainView(Context context) {
        super(context);
        setUpView(context);
    }

    public YKMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpView(context);
    }

    public YKMainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView(context);

    }

    private boolean checkIfExtendBaseActivity() {
        if (mContext instanceof MainViewBaseActivity) {
            return true;
        }
        DebugFlag.log("need extend MainViewBaseActivity in this Activity");

        return false;
    }

    private void setUpView(Context context) {
        mContext = context;
        checkIfExtendBaseActivity();

        inflate(context, R.layout.widget_main_view, this);
        mLV_FileList = (ListView) findViewById(android.R.id.list);
        mLV_FileList.setOnScrollListener(this);

        View emptyView = findViewById(R.id.empty_rl);
        mTV_CloudEmpty = (TextView) emptyView.findViewById(R.id.empty);
        mLV_FileList.setEmptyView(emptyView);
        //空列表显示字符
        mReturnViewInEmpty = emptyView.findViewById(R.id.file_list_return);

        mReturnViewInEmpty.setOnClickListener(this);
        mReturnViewInEmpty.setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        this.setId(R.id.yk_main_view);
    }

    public void initData() {
        openFolder(Config.ORG_ROOT_PATH);
    }

    public void setImageFetcher(ImageFetcher imageFetcher) {
        mImageFetcher = imageFetcher;
    }

    private int mShowPopMenuPosition;

    @Override
    public void onItemClick(FileListAdapter adapter, int position, View view) {
        final FileData data = (FileData) adapter.getItem(position);
        if (data.isHeader()) {
            onBackEvent();
            return;
        }

        if (data.isFooter()) {
            onLoadMoreData();
            return;
        }

        if (view.getId() == R.id.file_item_view_ll) {
            if (data.getDir() == FileData.DIRIS) {
                mPositionPopStack.add(mFirstPosition);
                openFolder(data.getFullpath());

            } else {
                if (UtilFile.isImageFile(data.getFilename())) {
                    Intent intent = new Intent(mContext, GalleryUrlActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(GalleryUrlActivity.EXTRA_LOCAL_FILE_PATH, data.getFullpath());
                    intent.putExtra(Constants.EXTRA_GALLERY_MODE, Constants.EXTRA_GALLERY_MODE_LIST);
                    //intent.putExtra(Constants.EXTRA_KEY_MOUNT_PROPERTY_DATA, mMountPropertyData);
                    intent.putExtra(Constants.EXTRA_MOUNT_ID, data.getMountId());
                    //intent.putExtra(Constants.EXTRA_ENT_ID, mEntId);
                    GKApplication.getInstance().startActivity(intent);
                } else if (UtilFile.isPreviewFile(data.getFilename())) {
                    Intent intent = new Intent(mContext, PreviewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.EXTRA_FILEDATA, data);
                    //intent.putExtra(Constants.EXTRA_KEY_FILE_READ, access);
                    GKApplication.getInstance().startActivity(intent);
                } else {
                    FileOpenManager.getInstance().handle(mContext, data);
                }

            }
        } else if (view.getId() == R.id.file_item_dropdown_btn) {
            //文件列表单项下啦

            PopupMenu popupMenu = new PopupMenu(mContext, view);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.file_operation_menu);
            if (mOption != null) {
                popupMenu.getMenu().getItem(0).setVisible(mOption.canRename);
                popupMenu.getMenu().getItem(1).setVisible(mOption.canDel);
            }
            popupMenu.show();
            mShowPopMenuPosition = position;
        }
    }

    public void onBackEvent() {
        //pop out position
        if (mPositionPopStack.size() > 0) {
            int index = mPositionPopStack.size() - 1;
            int position = mPositionPopStack.get(index);
            mLV_FileList.setSelection(position);
            mPositionPopStack.remove(index);
        }

        String fullPath = mPath;
        String parentPath = Util.getParentPath(fullPath);
        parentPath += (TextUtils.isEmpty(parentPath) || parentPath.endsWith("/")) ? "" : "/";

        setRedirectPath(fullPath);
        openFolder(parentPath);
    }

    private void onLoadMoreData() {
        mFileListAdapter.setIsLoadingMore(true);
        FileDataManager.getInstance().getListMore(this);
    }

    private void openFolder(final String fullPath) {
        mTV_CloudEmpty.setText(R.string.tip_is_loading);
        mPath = fullPath;

        //set title
        String folderName = Util.getNameFromPath(fullPath).replace("/", "");
        setActionTitle(isRoot() ? mCloudName : folderName);

        //set return view
        mReturnViewInEmpty.setVisibility(isRoot() ? View.GONE : View.VISIBLE);

        FileDataManager.getInstance().getFileList(fullPath, this, 0);

    }


    private void setActionTitle(String title) {
        ((Activity) mContext).setTitle(title);
    }

    private int mFirstPosition;

    public boolean isRoot() {
        return FileDataManager.getInstance().isRootPath(mPath);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                if (Utils.hasHoneycomb()) {
                    mImageFetcher.setPauseWork(true);
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mImageFetcher.setPauseWork(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mFirstPosition != firstVisibleItem) {
            mFirstPosition = firstVisibleItem;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.file_list_return) {
            onBackEvent();
        }
    }

    @Override
    public void onReceiveCacheData(final int start, final ArrayList<FileData> list) {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bindListView(start, list);
                if (!TextUtils.isEmpty(mRedirectPath)) {
                    redirectAndHighLight(list);
                } else {
                    //进入一个新的文件夹需要滑动到第一项目，如果是加载更多则不需要，start>0是加载更多
                    if (!isRefreshAction && start == 0) {
                        mLV_FileList.setSelection(0);
                    } else {
                        isRefreshAction = false;
                    }
                }
            }
        });
    }

    public void refresh() {
        if (mFileListAdapter != null) {
            isRefreshAction = true;
            openFolder(mPath);
        }

//        if (!isRefreshAction) {
//            mLV_FileList.setSelection(0);
//        } else {
//            isRefreshAction = false;
//        }
    }


    @Override
    public void onReceiveHttpData(final ArrayList<FileData> list, final int start, final String parentPath) {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (parentPath.equals(mPath)) {

                    mFileListAdapter.setIsLoadingMore(false);

                    bindListView(start, list);

                    if (!TextUtils.isEmpty(mRedirectPath)) {
                        redirectAndHighLight(list);
                    }
                    mTV_CloudEmpty.setText(R.string.file_list_empty);
                }
                onRefreshComplete();
            }
        });


    }

    /**
     * 在list 高亮显示
     *
     * @param list
     */
    private void redirectAndHighLight(ArrayList<FileData> list) {

        for (int i = 0; i < list.size(); i++) {
            FileData fileData = list.get(i);
            if (fileData.getFullpath().equals(mRedirectPath)) {

                // 导航之后重置数据
                setRedirectPath("");
                if (mPositionPopStack.size() > 0) {
                    int index = mPositionPopStack.size() - 1;
                    int position = mPositionPopStack.get(index);
                    mLV_FileList.setSelection(position);
                    mPositionPopStack.remove(index);
                } else {
                    mLV_FileList.setSelection(i);
                }
                mFileListAdapter.setHighlightItemString(fileData.getFullpath());
                break;
            }

        }
    }

    //绑定filelist的数据
    private void bindListView(int start, ArrayList<FileData> list) {
        if (!isRoot() && start == 0) {
            if (list.size() > 0) {
                list.add(0, FileData.createHeadData());
            }

            //要计算返回数据这一列，所以要加一
            if (list.size() >= FileDataManager.PAGE_SIZE + 1) {
                list.add(FileData.createFootData());
            }
        } else {
            if (isRoot()) {
                if (list.size() >= FileDataManager.PAGE_SIZE) {
                    list.add(FileData.createFootData());
                }

            }
        }

        if (mFileListAdapter == null) {
            mFileListAdapter = new FileListAdapter(mContext, list, mImageFetcher, this);
            mLV_FileList.setAdapter(mFileListAdapter);
        } else {
            if (start == 0) {
                mFileListAdapter.setList(list);
            } else {
                mFileListAdapter.addList(list, start);
            }
            mFileListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onReceiveHttpResponse(final int actionId) {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilDialog.dismissLoadingDialog(mContext);
                if (actionId == FileDataManager.ACTION_ID_DELETE) {
                    refresh();
                }
            }
        });


    }

    @Override
    public void onError(final String errorMsg) {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilDialog.dismissLoadingDialog(mContext);
                onRefreshComplete();
                UtilDialog.showNormalToast(errorMsg);
            }
        });

    }

    @Override
    public void onHookError(HookCallback.HookType type) {

    }

    @Override
    public void onNetUnable() {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilDialog.dismissLoadingDialog(mContext);
                onRefreshComplete();
                UtilDialog.showNormalToast(R.string.tip_net_is_not_available);
            }
        });
    }

    public void onCreateOptionsMenu(MenuInflater menuInflater, Menu menu) {
        if (mOption == null || !mOption.canUpload) {
            return;
        }
        menuInflater.inflate(R.menu.main_view_menu, menu);
    }

    public void onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_folders) {
            newFolderDialog();
        } else if (item.getItemId() == R.id.menu_files) {
            filesChooseDialog();
        } else if (item.getItemId() == R.id.menu_notes) {
            ((AppCompatActivity) mContext).startActivityForResult(
                    new Intent(mContext, GKNoteEditorActivity.class), Constants.REQUEST_CODE_UPLOAD_SUCCESS);
        } else if (item.getItemId() == R.id.menu_records) {
            createAudio();
        } else if (item.getItemId() == R.id.menu_take_photos) {
            createPicture();
        }

    }

    /**
     * 新建文件夹
     */
    public void newFolderDialog() {

        new NewFolderDialogManager(mContext).showDialog(mPath, new NewFolderDialogManager.DialogActionListener() {
            @Override
            public void onDone(String fullPath) {
                setRedirectPath(fullPath);
                refresh();
            }
        });

    }

    /**
     * 文件选择
     */
    public void filesChooseDialog() {
        new AlertDialog.Builder(mContext).setTitle(R.string.upload_type).setItems(R.array.upload_file_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, FileUploadActivity.class);
                intent.putExtra(FileUploadActivity.EXTRA_NAME_PATH, mPath);
                switch (which) {
                    case 0:
                        intent.putExtra(FileUploadActivity.EXTRA_UPLOAD_TYPE, FileUploadActivity.UPLOAD_TYPE_IMAGEFILE);
                        break;
                    case 1:
                        intent.putExtra(FileUploadActivity.EXTRA_UPLOAD_TYPE, FileUploadActivity.UPLOAD_TYPE_VIDEOFILE);
                        break;
                    case 2:
                        intent.putExtra(FileUploadActivity.EXTRA_UPLOAD_TYPE, FileUploadActivity.UPLOAD_TYPE_AUDIOFILE);
                        break;
                    case 3:
                        intent.putExtra(FileUploadActivity.EXTRA_UPLOAD_TYPE, FileUploadActivity.UPLOAD_TYPE_DOCFILE);
                        break;
                    case 4:
                        intent.putExtra(FileUploadActivity.EXTRA_UPLOAD_TYPE, FileUploadActivity.UPLOAD_TYPE_OTHERFILE);
                        break;
                }

                ((MainViewBaseActivity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_UPLOAD_SUCCESS);

            }
        }).show();

    }

    private AsyncTask mDeleteTask;

    /**
     * 删除文件
     */
    private void deleteFileAtPopPosition() {
        FileData fileData = (FileData) mFileListAdapter.getItem(mShowPopMenuPosition);
        final String fullPath = fileData.getFullpath();
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_to_delete)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UtilDialog.showDialogLoading(mContext, mContext.getString(R.string.deleting), mDeleteTask);
                        mDeleteTask = FileDataManager.getInstance().del(fullPath, YKMainView.this);
                    }
                })
                .setNegativeButton(R.string.cancel, null).show();

    }

    /**
     * 重命名
     */
    private void renameFileAtPopPosition() {
        FileData fileData = (FileData) mFileListAdapter.getItem(mShowPopMenuPosition);
        final String fullPath = fileData.getFullpath();
        new RenameDialogManager(mContext).showDialog(fullPath, new RenameDialogManager.DialogActionListener() {
            @Override
            public void onDone(String fullPath) {
                setRedirectPath(fullPath);
                refresh();
            }
        });

    }


    @Override
    public void onRefresh() {
        refresh();
    }

    /**
     * 下载刷新完毕
     */
    private void onRefreshComplete() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item_delete) {
            deleteFileAtPopPosition();
        } else if (menuItem.getItemId() == R.id.item_rename) {
            renameFileAtPopPosition();
        }
        return false;
    }


    public Option getOption() {
        return mOption;
    }

    /**
     * 设置定位路径
     *
     * @param redirectPath
     */
    private void setRedirectPath(String redirectPath) {
        mRedirectPath = redirectPath;
    }


    public void setOption(Option option) {
        mOption = option;
    }

    /**
     * 文件定位（高亮显示）
     *
     * @param fullPath
     */
    public void redirectToFile(String fullPath) {
        setRedirectPath(fullPath);
        refresh();
    }

    /**
     * 录音上传
     */
    private void createAudio() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        try {
            ((AppCompatActivity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_AUDIO);
        } catch (Exception e) {
            UtilDialog.showNormalToast(R.string.tip_no_available_device_to_take_audio);
        }
    }

    private Uri mCameraImageUri;

    /**
     * 拍照上传
     */
    private void createPicture() {
        ContentValues values = new ContentValues();
        long dateTaken = System.currentTimeMillis();
        Date date = new Date(dateTaken);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                mContext.getString(R.string.image_file_name_format));
        String title = dateFormat.format(date);

        String filename = title + ".jpg";
        values.put(MediaStore.MediaColumns.TITLE, "my image");
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.ImageColumns.DESCRIPTION, "image captured by camera");

        mCameraImageUri = mContext.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
        try {
            ((AppCompatActivity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PIC);
        } catch (Exception e) {
            UtilDialog.showNormalToast(R.string.tip_no_available_device_to_take_camera);
        }
    }


    public String getCurrentPath() {
        return mPath;
    }

    public Uri getCameraUri() {
        return mCameraImageUri;
    }


}
