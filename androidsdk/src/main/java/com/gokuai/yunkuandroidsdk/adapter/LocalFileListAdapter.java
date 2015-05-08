package com.gokuai.yunkuandroidsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.data.LocalFileData;
import com.gokuai.yunkuandroidsdk.imageutils.ImageFetcher;
import com.gokuai.yunkuandroidsdk.util.Util;
import com.gokuai.yunkuandroidsdk.util.UtilFile;
import com.yunkuent.sdk.utils.Constants;

import java.util.ArrayList;


public class LocalFileListAdapter extends BaseImageAdapter {

    private LayoutInflater mInflater;
    private ArrayList<LocalFileData> mList;
    private ListView mListView;
    private ImageFetcher mImageFetcher;
    private Context mContext;

    public LocalFileListAdapter(Context context, ArrayList<LocalFileData> list, ListView listView, ImageFetcher imageFetcher) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = list;
        mListView = listView;
        mContext = context;
        mImageFetcher = imageFetcher;
        mImageFetcher.setLoadingImage(R.drawable.ic_img);
        setOnscrollListner(mListView, mImageFetcher);

    }

    public ArrayList<LocalFileData> getList() {
        return mList;
    }

    public void setList(ArrayList<LocalFileData> list, ListView listView) {
        mList = list;
        mListView = listView;
    }

    public void addHeader(LocalFileData data) {
        if (mList == null) {
            mList = new ArrayList<LocalFileData>();
        }
        data.setHeader(true);
        mList.add(0, data);
    }

    public void updateSelect() {
        if (mList != null) {
            notifyDataSetChanged();
        }
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.localfile_item, null);
            holder = new ViewHolder();
            holder.filename = (TextView) convertView.findViewById(R.id.file_item_name);
            holder.filesize = (TextView) convertView.findViewById(R.id.file_item_size);
            holder.pic = (ImageView) convertView.findViewById(R.id.file_item_pic);
            holder.select = (ImageView) convertView.findViewById(R.id.file_item_selected);
            holder.time = (TextView) convertView.findViewById(R.id.file_item_time);
            holder.divider = convertView.findViewById(R.id.item_divider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LocalFileData data = mList.get(position);

        setDescribeVisibility(holder.filename, holder.filesize, holder.time, data.getHeader() || data.getDir() ? View.GONE : View.VISIBLE);
        holder.select.setVisibility(!data.getHeader() && !data.getDir() ? View.VISIBLE : View.GONE);
        if (data.getHeader()) {
            holder.filename.setText(R.string.tip_file_list_header);
            holder.filename.setTextColor(mContext.getResources().getColor(R.color.color_0));
            holder.pic.setImageResource(R.drawable.ic_back);
        } else {
            holder.filename.setText(data.getFilename());
            if (data.getDir()) {
                holder.pic.setImageResource(R.drawable.ic_dir);
            } else {
                holder.pic.setImageResource(UtilFile.getExtensionIcon(mContext, data.getFilename()));

                final String fullPath = data.getFullpath();
                if (UtilFile.isImageFile(data.getFilename())) {
                    mImageFetcher.loadImage(fullPath, holder.pic, false);
                }

                holder.filesize.setText(Util.formatFileSize(mContext, data.getFilesize()));
                holder.time.setText(Util.formateTime(data.getFiledate()));

                holder.select.setImageResource(data.getSelected() ? R.drawable.checkbox_checked
                        : R.drawable.checkbox_normal);

            }

        }

        if (position == mList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView filename;
        private TextView filesize;
        private TextView time;
        private ImageView pic;
        private ImageView select;
        private View divider;
    }

    private void setDescribeVisibility(TextView name, TextView size, TextView time, int visibility) {
        name.setPadding(0, 0, 0, visibility == View.VISIBLE ? mContext.getResources().getDimensionPixelOffset(R.dimen.list_view_offset_for_center_in_parent) : 0);
        size.setVisibility(visibility);
        time.setVisibility(visibility);
    }


}
