package com.gokuai.yunkuandroidsdk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.util.ArrayList;

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
        if (mContext instanceof BaseActivity) {
            return true;
        }
        DebugFlag.log("need extend BaseActivity in this Activity");

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

    public void startToInitData(ImageFetcher imageFetcher) {
        mImageFetcher = imageFetcher;
        openFolder("");
    }

    private int mShowPopMenuPosition;

    @Override
    public void onItemClick(FileListAdapter adapter, int position, View view) {
        final FileData data = (FileData) adapter.getItem(position);
        if (data.isHeader()) {
            onBackEvent();
            return;
        }

        if (view.getId() == R.id.file_item_view_ll) {
            if (data.getDir() == FileData.DIRIS) {
                mPositionPopStack.add(mFirstPosition);
                openFolder(data.getFullpath());

            } else {
                FileOpenManager.getInstance().handle(mContext, data);
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
        return TextUtils.isEmpty(mPath);
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
    public void onReceiveCacheData(final ArrayList<FileData> list) {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bindListView(list);
                if (!TextUtils.isEmpty(mRedirectPath)) {
                    redirectAndHighLight(list);
                }
            }
        });
    }

    private void refresh() {
        if (mFileListAdapter != null) {
            isRefreshAction = true;
            openFolder(mPath);
        }

        if (!isRefreshAction) {
            mLV_FileList.setSelection(0);
        } else {
            isRefreshAction = false;
        }
    }


    @Override
    public void onReceiveHttpData(final ArrayList<FileData> list, final String parentPath) {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (parentPath.equals(mPath)) {
                    bindListView(list);

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
    private void bindListView(ArrayList<FileData> list) {
        if (!isRoot()) {
            if (list.size() > 0) {
                list.add(0, FileData.createHeadData());
            }
        }

        if (mFileListAdapter == null) {
            mFileListAdapter = new FileListAdapter(mContext, list, mImageFetcher, this);
            mLV_FileList.setAdapter(mFileListAdapter);
        } else {
            mFileListAdapter.setList(list);
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
    public void onNetUnable() {
        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilDialog.dismissLoadingDialog(mContext);
                onRefreshComplete();
                UtilDialog.showNormalToast(R.string.network_not_available);
            }
        });
    }

    public void onCreateOptionsMenu(MenuInflater menuInflater, Menu menu) {
        if (mOption != null) {
            if (!mOption.canUpload) {
                return;
            }
        }
        menuInflater.inflate(R.menu.main_view_menu, menu);
    }

    public void onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_folders) {
            newFolderDialog();
        } else if (item.getItemId() == R.id.menu_files) {
            filesChooseDialog();
        } else if (item.getItemId() == R.id.menu_notes) {

        } else if (item.getItemId() == R.id.menu_records) {

        } else if (item.getItemId() == R.id.menu_take_photos) {

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

                ((BaseActivity) mContext).startActivityForResult(intent, Constants.REQUEST_CODE_UPLOAD_SUCCESS);

            }
        }).show();

    }

    private AsyncTask mDeleteTask;

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
     * @param redirectPath
     */
    private void setRedirectPath(String redirectPath) {
        mRedirectPath = redirectPath;
    }


    public void setOption(Option option) {
        mOption = option;
    }

    public void redirectToFile(String fullPath){
        setRedirectPath(fullPath);
        refresh();
    }


}
