package com.gokuai.yunkuandroidsdk.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.R;

/**
 * Created by Brandon on 15/5/8.
 */
public class FileSelectDialog extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString("title");
        View v = inflater.inflate(R.layout.action_bar_dialog, container, false);
        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText("This is an instance of ActionBarDialog");
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.file_operation_menu);
        toolbar.setTitle(title);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
