package com.gokuai.yunkuandroidsdk.dialog;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.FileDataManager;
import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.adapter.FolderListAdapter;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.util.UtilDialog;
import com.yunkuent.sdk.utils.Util;

import java.util.ArrayList;

/**
 * No used now
 */
public class FolderSelectDialog extends DialogFragment implements View.OnClickListener, FileDataManager.FileDataListener, AdapterView.OnItemClickListener {

    private TextView mTv_PathTitle;
    private String mOriginPath = "";
    private String mFullPaths;

    private String mCloudPath;

    private ListView mLv_FileList;
    private String mRedirectPath;

    private FolderListAdapter mFolderAdapter;//云库文件列表

    private TextView mTV_CloudEmpty;//列表空界面
    private View mView_fileReturnView;
    private int mType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        mType = args.getInt(Constants.FILESELECT_TYPE);
        String title = "";
        switch (mType) {
            case Constants.FILESELECT_TYPE_COPY:
                title = getString(R.string.copy_to);
                break;
            case Constants.FILESELECT_TYPE_MOVE:
                title = getString(R.string.move_to);
                break;
        }
        View v = inflater.inflate(R.layout.action_bar_dialog, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_cancel) {
                    dismiss();
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.folder_select_menu);
        toolbar.setTitle(title);

        setupViews(v);

        openFolder("");
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void openFolder(final String fullPath) {
        changeShowView(true);

        mTV_CloudEmpty.setText(R.string.tip_is_loading);

        final boolean isRoot = TextUtils.isEmpty(fullPath);

        if (!isRoot) {
            setPathTitle(Util.getNameFromPath(fullPath).replace("/", ""));
            mView_fileReturnView.setVisibility(View.VISIBLE);

        } else {
            setPathTitle(Config.ORG_ROOT_TITLE);
        }

        mCloudPath = fullPath;
        //先从本地获取数据

        FileDataManager.getInstance().cancelFileTask();
        //FIXME 选择文件夹需要一个新的方式
//        FileDataManager.getInstance().getFileList(fullPath, this, 0);

    }

    private void setupViews(View v) {
        Button okBtn = (Button) v.findViewById(R.id.folder_ok_btn);
        okBtn.setOnClickListener(this);
        mTv_PathTitle = (TextView) v.findViewById(R.id.folder_path_title_tv);

        mLv_FileList = (ListView) v.findViewById(R.id.list);
        View empty = v.findViewById(R.id.empty_ll);
//        mLv_FileList.setEmptyView(empty);
        mView_fileReturnView = v.findViewById(R.id.file_list_return);
        mView_fileReturnView.setOnClickListener(this);
        mView_fileReturnView.setVisibility(View.GONE);

        mTV_CloudEmpty = (TextView) empty.findViewById(R.id.empty);
        mTV_CloudEmpty.setText(R.string.tip_is_loading);
    }

    private void setPathTitle(String title) {
        mTv_PathTitle.setText(title);
    }

    /**
     * 库界面与文件列表切换
     *
     * @param isFile
     */
    private void changeShowView(boolean isFile) {
        if (mLv_FileList != null) {
            mLv_FileList.setVisibility(isFile ? View.VISIBLE : View.GONE);
        }

    }

    private AsyncTask mHttpRequestTask;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.folder_ok_btn) {
            String filePath = mCloudPath;
            if (mOriginPath != null && (mOriginPath.equals(filePath))) {
                UtilDialog.showNormalToast(R.string.tip_target_path_is_same_with_orginpath);
            } else {
                String messageString = mType == Constants.FILESELECT_TYPE_MOVE ? getString(R.string.tip_is_copying) : getString(R.string.tip_is_moving);
                UtilDialog.showDialogLoading(getActivity(), messageString, mHttpRequestTask);
                switch (mType) {
                    case Constants.FILESELECT_TYPE_COPY:
                        mHttpRequestTask = FileDataManager.getInstance().copy(mFullPaths, mCloudPath, this);
                        break;
                    case Constants.FILESELECT_TYPE_MOVE:
                        mHttpRequestTask = FileDataManager.getInstance().move(mFullPaths, mCloudPath, this);
                        break;
                }

            }

        } else if (v.getId() == R.id.file_list_return) {
            onBackEvent();
        }

    }

    private void onBackEvent() {
        setRedirectPath(mCloudPath);

        String parentPath = com.gokuai.yunkuandroidsdk.util.Util.getParentPath(mCloudPath);
        if (!TextUtils.isEmpty(parentPath)) {
            parentPath = parentPath + "/";
        }

        openFolder(parentPath);
    }

    @Override
    public void onReceiveCacheData(int start, final ArrayList<FileData> list) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (shouldShowHead()) {
                    if (list.size() > 0) {
                        list.add(0, FileData.createHeadData());
                    }
                }
                if (mFolderAdapter == null) {//初始化
                    initCloudFileList(list);
                } else {

                    mFolderAdapter.setList(list);
                    mFolderAdapter.notifyDataSetChanged();

                    if (!TextUtils.isEmpty(getRedirectPath())) {
                        for (int i = 0; i < list.size(); i++) {
                            FileData fileData = list.get(i);
                            if (fileData.getFullpath().equals(getRedirectPath())) {
                                // 导航之后重置数据
                                setRedirectPath("");
                                mLv_FileList.setSelection(i);
                                mFolderAdapter.setHighlightItemString(fileData.getFullpath());
                                break;
                            }

                        }
                    }
                }
            }
        });

    }

    @Override
    public void onReceiveHttpData(final ArrayList<FileData> list, int start, final String parentPath) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (parentPath.equals(mCloudPath)) {
                    if (shouldShowHead()) {
                        if (list.size() > 0) {
                            list.add(0, FileData.createHeadData());
                        }
                    }

                    mFolderAdapter.setList(list);
                    mFolderAdapter.notifyDataSetChanged();
                }
                mTV_CloudEmpty.setText(R.string.file_list_empty);

            }
        });
    }

    @Override
    public void onReceiveHttpResponse(int actionId) {

    }

    @Override
    public void onError(final String errorMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilDialog.showNormalToast(errorMsg);
                mTV_CloudEmpty.setText(errorMsg);
            }
        });
    }

    @Override
    public void onHookError(HookCallback.HookType type) {

    }

    @Override
    public void onNetUnable() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTV_CloudEmpty.setText(getString(R.string.tip_net_is_not_available));

            }
        });

    }

    public String getRedirectPath() {
        return mRedirectPath;
    }

    /**
     * 设置定位路径
     *
     * @param redirectPath
     */
    public void setRedirectPath(String redirectPath) {
        this.mRedirectPath = redirectPath;
    }

    public boolean shouldShowHead() {
        return !TextUtils.isEmpty(mCloudPath.replace("/", ""));
    }

    /**
     * 初始化云库文件
     */
    public void initCloudFileList(ArrayList<FileData> folderList) {
        mFolderAdapter = new FolderListAdapter(getActivity(), folderList);
        mLv_FileList.setAdapter(mFolderAdapter);
        mLv_FileList.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        if (view.getId() == R.id.folder_item_ll) {
            if (obj instanceof FileData) {

                FileData data = (FileData) obj;
                if (data.isHeader()) {
                    onBackEvent();
                } else {
                    if (data.getDir() == FileData.DIRIS) {

                        //文件复制和移动的时候不能移动到子目录下
                        if (mType == Constants.FILESELECT_TYPE_MOVE || mType == Constants.FILESELECT_TYPE_COPY) {

                            if (mFullPaths.equals(data.getFullpath())) {
                                if (mType == Constants.FILESELECT_TYPE_MOVE) {
                                    UtilDialog.showNormalToast(R.string.tip_cant_copy_to_son_folder);
                                } else if (mType == Constants.FILESELECT_TYPE_COPY) {
                                    UtilDialog.showNormalToast(R.string.tip_cant_move_to_son_folder);
                                }
                                return;
                            }
                        }
                        openFolder(data.getFullpath());
                    }
                }
            }
        }
    }
}
