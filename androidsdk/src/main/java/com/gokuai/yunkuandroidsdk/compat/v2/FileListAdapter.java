package com.gokuai.yunkuandroidsdk.compat.v2;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.Constants;
import com.gokuai.yunkuandroidsdk.Option;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Brandon on 15/4/15.
 */
public class FileListAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater mInflater;
    private Context mContext;
    private ImageFetcher mImageFetcher;
    private FileItemClickListener mListener;
    private int mSortType;
    private boolean isOperationEnable;
    private String mHighlightItemStr;
    private Handler mHandler;
    private boolean isLoadingMore;

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            if (isLoadingMore && ((FileData) getItem(position)).isFooter()) {
                //正在加载的状态不予以处理
                return;
            }
            mListener.onItemClick(this, position, v);
        }
    }

    public void setHighlightItemString(String highlightItemString) {
        mHighlightItemStr = highlightItemString;
    }

    public void setIsLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    public interface FileItemClickListener {
        void onItemClick(FileListAdapter adapter, int position, View view);
    }

    public FileListAdapter(Context context, ArrayList<FileData> list, ImageFetcher imageFetcher, FileItemClickListener listener) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);

        mImageFetcher = imageFetcher;
        mImageFetcher.setLoadingImage(R.drawable.ic_img);
        mListener = listener;
        mHandler = new Handler();

        Option option = ((YKMainView) listener).getOption();
        if (option != null) {
            isOperationEnable = option.canDel || option.canRename;
        }

        mSortType = Config.getListSortType(context);
        sortList(mSortType);
    }


    private ArrayList<FileData> mList;

    private SparseArray<ArrayList<FileData>> mPareArr;

    private SparseArray<ArrayList<FileData>> getSparseArr() {
        if (mPareArr == null) {
            mPareArr = new SparseArray<>();
        }
        return mPareArr;
    }


    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    public void setList(ArrayList<FileData> list) {
        mList = list;
        mPareArr = null;//每次置空 避免不同路径下的列表
        boolean hasFoot = removeFooter(list);
        getSparseArr().put(0, list); //start == 0
        sortList(mSortType);
        if (hasFoot) {
            mList.add(FileData.createFootData());
        }
    }

    public void addList(ArrayList<FileData> list, int start) {
        removeFooter(mList);//删除之前最后一项footer
        boolean hasFoot = removeFooter(list);
        getSparseArr().put(start, list);
        mList = generateList(getSparseArr());
        sortList(mSortType);
        if (hasFoot) {
            mList.add(FileData.createFootData());
        }
    }

    //FIXME 如果是超长的列表，这里会有内存大开销的隐患
    private ArrayList<FileData> generateList(SparseArray<ArrayList<FileData>> sparseArray) {

        ArrayList<FileData> list = new ArrayList<>();
        for (int i = 0; i < sparseArray.size(); i++) {
            int key = sparseArray.keyAt(i);
            ArrayList<FileData> tempList = sparseArray.get(key);
            if (tempList != null) {
                list.addAll(sparseArray.get(key));
            }
        }

        return list;
    }

    public void setSortType(int sortType) {
        mSortType = sortType;
        sortList(sortType);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.file_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.file_item_name);
            holder.dateline = (TextView) convertView.findViewById(R.id.file_item_dateline);
//            holder.lastmembername = (TextView) convertView.findViewById(R.id.file_item_lastmembername);
            holder.filesize = (TextView) convertView.findViewById(R.id.file_item_size);
            holder.img = (ImageView) convertView.findViewById(R.id.file_item_pic);
            holder.dropdownbtn = (Button) convertView.findViewById(R.id.file_item_dropdown_btn);
            holder.dropdownbtn.setOnClickListener(this);
            holder.itemll = convertView.findViewById(R.id.file_item_view_ll);
            holder.itemll.setOnClickListener(this);
            holder.loadingMoreText = (TextView) convertView.findViewById(R.id.file_item_loading_more_tv);
            holder.descriptionll = convertView.findViewById(R.id.file_item_description_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileData data = mList.get(position);
        holder.name.setText(data.getFilename());
        holder.dateline.setText(Util.formateTime(data.getDateline()));
//        holder.lastmembername.setText(data.getLastMemberName());
        holder.dropdownbtn.setTag(position);
        holder.itemll.setTag(position);

        holder.img.setBackgroundDrawable(null);
        if (data.isHeader()) {
            holder.name.setText(R.string.file_list_header);
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setImageResource(R.drawable.ic_back);
            holder.dropdownbtn.setVisibility(View.GONE);
            holder.descriptionll.setVisibility(View.GONE);
            holder.loadingMoreText.setVisibility(View.GONE);
        } else if (data.isFooter()) {
            holder.img.setVisibility(View.GONE);
            holder.name.setText("");
            holder.dropdownbtn.setVisibility(View.GONE);
            holder.descriptionll.setVisibility(View.GONE);
            holder.loadingMoreText.setVisibility(View.VISIBLE);
        } else {
            holder.img.setVisibility(View.VISIBLE);
            holder.loadingMoreText.setVisibility(View.GONE);
            holder.dropdownbtn.setVisibility(isOperationEnable ? View.VISIBLE : View.GONE);
            holder.descriptionll.setVisibility(View.VISIBLE);
            if (data.getDir() == FileData.DIRIS) {

                holder.img.setImageResource(R.drawable.ic_dir);
                holder.filesize.setText("");
            } else {

                holder.img.setImageResource(data.getExt(mContext));

                if (UtilFile.isImageFile(data.getFilename())) {
                    String thumbNailUrl = data.getThumbSmall();
                    mImageFetcher.loadImage(thumbNailUrl, holder.img, false);
                }
                holder.filesize.setText(Util.formatFileSize(mContext, data.getFilesize()) + ",");
            }

            if (mHighlightItemStr != null && mHighlightItemStr.equals(data.getFullpath())) {
                startAnimation(holder.itemll);
                mHighlightItemStr = null;
            } else {
                convertView.setBackgroundResource(R.drawable.listview_selector);
            }
        }

        return convertView;
    }


    private class DatelineComparator implements Comparator<Object> {

        @Override
        public int compare(Object object1, Object object2) {
            FileData data1 = (FileData) object1;
            FileData data2 = (FileData) object2;

            if (data1.getDateline() > data2.getDateline()) {
                return -1;
            } else if (data1.getDateline() < data2.getDateline()) {
                return 1;
            } else {
                if (data1.getFirstCharacterType() < data2.getFirstCharacterType()) {
                    return -1;
                } else if (data1.getFirstCharacterType() > data2.getFirstCharacterType()) {
                    return 1;
                } else {
                    return data1.getFirstLetters().compareTo(data2.getFirstLetters());
                }
            }
        }
    }

    private class FileSizeComparator implements Comparator<Object> {

        @Override
        public int compare(Object object1, Object object2) {
            FileData data1 = (FileData) object1;
            FileData data2 = (FileData) object2;

            if (data1.getDir() < data2.getDir()) {
                return -1;
            } else if (data1.getDir() > data2.getDir()) {
                return 1;
            } else {
                if (data1.getFilesize() < data2.getFilesize()) {
                    return 1;
                } else if (data1.getFilesize() > data2.getFilesize()) {
                    return -1;
                } else {
                    if (data1.getFirstCharacterType() < data2.getFirstCharacterType()) {
                        return -1;
                    } else if (data1.getFirstCharacterType() > data2.getFirstCharacterType()) {
                        return 1;
                    } else {
                        return data1.getFirstLetters().compareTo(data2.getFirstLetters());
                    }
                }
            }
        }
    }

    private class FileNameComparator implements Comparator<Object> {

        @Override
        public int compare(Object object1, Object object2) {
            FileData data1 = (FileData) object1;
            FileData data2 = (FileData) object2;
            if (data1.getDir() > data2.getDir()) {
                return -1;
            } else if (data1.getDir() < data2.getDir()) {
                return 1;
            } else {
                if (data1.getFirstCharacterType() < data2.getFirstCharacterType()) {
                    return -1;
                } else if (data1.getFirstCharacterType() > data2.getFirstCharacterType()) {
                    return 1;
                } else {
                    return data1.getFirstLetters().compareTo(data2.getFirstLetters());
                }
            }
        }
    }

    private void sortList(int sortType) {
        Config.saveListSortType(mContext, sortType);
        if (mList != null && mList.size() > 0) {
            boolean hasHeader = removeHeader(mList);

            switch (sortType) {
                case Constants.FILE_SORT_TYPE_FILENAME:
                    FileNameComparator fileNameComparator = new FileNameComparator();
                    Collections.sort(mList, fileNameComparator);
                    break;
                case Constants.FILE_SORT_TYPE_DATELINE:
                    DatelineComparator datelineComparator = new DatelineComparator();
                    Collections.sort(mList, datelineComparator);
                    break;
                case Constants.FILE_SORT_TYPE_FILESIZE:
                    FileSizeComparator fileSizeComparator = new FileSizeComparator();
                    Collections.sort(mList, fileSizeComparator);
                    break;
            }
            if (hasHeader) {
                mList.add(0, FileData.createHeadData());
            }

        }

    }

    private class ViewHolder {
        private TextView name;
        private TextView dateline;
        //        private TextView lastmembername;
        private TextView filesize;
        private View descriptionll;
        private ImageView img;
        private Button dropdownbtn;
        private View itemll;
        private TextView loadingMoreText;
    }

    private void startAnimation(final View counterView) {
        counterView.setBackgroundColor(counterView.getResources().getColor(R.color.list_selected));
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                counterView.setBackgroundResource(R.drawable.listview_selector);
            }
        }, 500);
    }

    private boolean removeFooter(ArrayList<FileData> list) {

        boolean hasFooter = false;
        if (list.size() > 0) {
            int index = list.size() - 1;

            if (list.get(index).isFooter()) {
                hasFooter = true;
                list.remove(index);

            }
        }
        return hasFooter;
    }

    private boolean removeHeader(ArrayList<FileData> list) {
        boolean hasHeader = false;
        if (list.get(0).isHeader()) {
            hasHeader = true;
            list.remove(0);
        }
        return hasHeader;

    }


}
