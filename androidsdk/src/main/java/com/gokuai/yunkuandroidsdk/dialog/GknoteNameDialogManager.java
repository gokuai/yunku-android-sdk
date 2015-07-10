package com.gokuai.yunkuandroidsdk.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.FileDataManager;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.util.Util;

/**
 * Created by Brandon on 15/5/18.
 */
public class GknoteNameDialogManager extends DialogManger {


    public GknoteNameDialogManager(Context context) {
        super(context);
    }

    private Button mOKBtn;

    @Override
    public void showDialog(final String parentPath, final DialogActionListener listener) {
        final View editView = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_edit_with_check, null);
        final EditText editText = (EditText) editView.findViewById(R.id.dialog_edit);

        String hintText = String.format(mContext.getString(R.string.gknote_name_format),
                Util.formateTime(System.currentTimeMillis(), "yyyyMMdd_hhmmss", mContext));

        editText.setHint(hintText);
        final TextView textView = (TextView) editView.findViewById(R.id.dialog_check);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setView(editView)
                .setTitle(R.string.new_gknote_name)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //prevent dialog auto dismiss should override this method here

                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                mOKBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                mOKBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String fileName;
                        String input = editText.getText().toString();
                        if (TextUtils.isEmpty(input)) {
                            fileName = editText.getHint().toString();
                        } else {
                            fileName = editText.getText().toString() + ".gknote";
                        }

                        String fullPath = parentPath + fileName;
                        if (FileDataManager.getInstance().fileExistInCache(parentPath + fileName)) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(R.string.tip_same_file_name_exist);
                        } else {

                            listener.onDone(fullPath);

                        }
                    }
                });
            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isContainSpecial = Util.isContainSpecail(s.toString());
                boolean isContainExpression = Util.isContainExpression(s);

                boolean isValid = Util.isInvaidName(s.toString());
                if (isContainSpecial || isContainExpression) {
                    textView.setText(R.string.tip_name_contain_special_char);
                } else if (isValid) {
                    textView.setText(R.string.tip_name_invalid_folder_name);
                }
                textView.setVisibility(isContainSpecial || isValid || isContainExpression ? View.VISIBLE : View.GONE);
                mOKBtn.setEnabled(!isContainSpecial && !isContainExpression && !isValid);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        dialog.show();
        Util.showSoftKeyBoard(mContext, editText);


    }
}
