package com.gokuai.yunkuandroidsdk.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.gokuai.yunkuandroidsdk.HookCallback;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.util.Util;

/**
 * Created by Brandon on 15/5/8.
 */
public class NewFolderDialogManager extends DialogManger{

    private AsyncTask mNewFolderTask;

    public NewFolderDialogManager(Context context) {
        super(context);
    }


    @Override
    public void showDialog(final String parentFullPath, final DialogActionListener listener) {
        final View editView = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_edit_with_check, null);
        final EditText editText = (EditText) editView.findViewById(R.id.dialog_edit);
        final TextView textView = (TextView) editView.findViewById(R.id.dialog_check);
        final TextView doingTextView = (TextView) editView.findViewById(R.id.dialog_doing);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setView(editView)
                .setTitle(R.string.new_folder)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mNewFolderTask != null) {
                            mNewFolderTask.cancel(true);
                            mNewFolderTask = null;
                        }
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
                mOKBtn.setEnabled(false);
                mOKBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        doingTextView.setText(R.string.tip_is_creating);
                        doingTextView.setVisibility(View.VISIBLE);

                        //判断是否有同名文件夹
                        final String dirName = Util.getTrimDirName(editText.getText().toString());
                        final String fullPath = parentFullPath + dirName + "/";

                        boolean hasSame = FileDataManager.getInstance().fileExistInCache(fullPath);
                        if (hasSame) {
                            doingTextView.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(R.string.tip_same_file_name_exist);
                            return;
                        }


                        mOKBtn.setEnabled(false);
                        //网络发送添加文件夹的请求
                        mNewFolderTask = FileDataManager.getInstance().addDir(fullPath, new FileDataManager.DataListener() {
                                    @Override
                                    public void onReceiveHttpResponse(int actionId) {
                                        dialog.dismiss();
                                        listener.onDone(fullPath);
                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        mOKBtn.setEnabled(true);
                                        doingTextView.setText(errorMsg);
                                    }

                                    @Override
                                    public void onHookError(HookCallback.HookType type) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNetUnable() {
                                        mOKBtn.setEnabled(true);
                                        doingTextView.setText(R.string.tip_net_is_not_available);
                                    }
                                }
                        );
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
                //验证文件名的可用性
                boolean isContainSpecial = Util.isContainSpecail(s.toString());
                boolean isContainExpression = Util.isContainExpression(s);

                boolean isValid = Util.isInvaidName(s.toString());
                if (isContainSpecial || isContainExpression) {
                    textView.setText(R.string.tip_name_contain_special_char);
                } else if (isValid) {
                    textView.setText(R.string.tip_name_invalid_folder_name);
                }
                textView.setVisibility(isContainSpecial || isValid || isContainExpression ? View.VISIBLE : View.GONE);
                mOKBtn.setEnabled(!isContainSpecial && !isContainExpression
                        && !TextUtils.isEmpty(s.toString().trim()) && !isValid && s.length() > 0);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        dialog.show();
        Util.showSoftKeyBoard(mContext, editText);

    }


}
