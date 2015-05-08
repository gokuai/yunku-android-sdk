package com.gokuai.yunkuandroidsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.data.FileData;

import java.util.ArrayList;

/**
 * Created by Brandon on 15/5/8.
 */
public class FolderListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<FileData> mList;

    private String mHighlightItemStr;

    private Context mContext;

    public FolderListAdapter(Context context, ArrayList<FileData> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
            convertView = mInflater.inflate(R.layout.folder_item, null);
            holder = new ViewHolder();
            holder.filename = (TextView) convertView.findViewById(R.id.folder_item_name);
            holder.image = (ImageView) convertView.findViewById(R.id.folder_item_pic);
            holder.item_ll = (LinearLayout) convertView.findViewById(R.id.folder_item_ll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileData data = mList.get(position);
        if (data.isHeader()) {
            holder.filename.setText(R.string.tip_file_list_header);
            holder.image.setImageResource(R.drawable.ic_back);
        } else {
            holder.filename.setText(data.getFilename());
            if (data.getDir() == FileData.DIRIS) {
                holder.image.setImageResource(R.drawable.ic_dir);
            } else {
                holder.image.setImageResource(data.getExt(mContext));
            }

        }
        holder.item_ll.setTag(position);

        if (mHighlightItemStr != null && mHighlightItemStr.equals(data.getFullpath())) {
            startAnimation(holder.item_ll);
            mHighlightItemStr = null;
        } else {
            holder.item_ll.setBackgroundResource(R.drawable.listview_selector);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView filename;
        ImageView image;
        LinearLayout item_ll;

    }

    public void startAnimation(final View view) {
        view.setBackgroundResource(R.color.list_selected);
        view.postDelayed(new Runnable() {

            @Override
            public void run() {
                view.setBackgroundResource(R.drawable.listview_selector);
            }
        }, 500);
    }

    public void clearList() {
        mList = null;
        notifyDataSetChanged();
    }

    public void setHighlightItemString(String str) {
        mHighlightItemStr = str;
    }

    public void setList(ArrayList<FileData> list) {
        mList = list;
    }

}
