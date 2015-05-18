package com.gokuai.yunkuandroidsdk.adapter;

import android.content.Context;
import android.os.Handler;
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
import com.gokuai.yunkuandroidsdk.YKMainView;
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

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            mListener.onItemClick(this, position, v);
        }
    }

    public void setHighlightItemString(String highlightItemString) {
        mHighlightItemStr = highlightItemString;
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
        sortList(mSortType);
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
            holder.lastmembername = (TextView) convertView.findViewById(R.id.file_item_lastmembername);
            holder.filesize = (TextView) convertView.findViewById(R.id.file_item_size);
            holder.img = (ImageView) convertView.findViewById(R.id.file_item_pic);
            holder.dropdownbtn = (Button) convertView.findViewById(R.id.file_item_dropdown_btn);
            holder.dropdownbtn.setOnClickListener(this);
            holder.itemll = convertView.findViewById(R.id.file_item_view_ll);
            holder.itemll.setOnClickListener(this);
            holder.descriptionll = convertView.findViewById(R.id.file_item_description_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileData data = mList.get(position);
        holder.name.setText(data.getFilename());
        holder.dateline.setText(Util.formateTime(data.getDateline()));
        holder.lastmembername.setText(data.getLastMemberName());
        holder.dropdownbtn.setTag(position);
        holder.itemll.setTag(position);
        holder.img.setBackgroundDrawable(null);
        if (data.isHeader()) {
            holder.name.setText(R.string.file_list_header);
            holder.img.setImageResource(R.drawable.ic_back);
            holder.dropdownbtn.setVisibility(View.GONE);
            holder.descriptionll.setVisibility(View.GONE);
        } else {
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
            boolean hasRemoved = false;
            if (mList.get(0).isHeader()) {
                hasRemoved = true;
                mList.remove(0);
            }
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
            if (hasRemoved) {
                mList.add(0, FileData.createHeadData());
            }

        }

    }

    private class ViewHolder {
        private TextView name;
        private TextView dateline;
        private TextView lastmembername;
        private TextView filesize;
        private View descriptionll;
        private ImageView img;
        private Button dropdownbtn;
        private View itemll;
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


}
