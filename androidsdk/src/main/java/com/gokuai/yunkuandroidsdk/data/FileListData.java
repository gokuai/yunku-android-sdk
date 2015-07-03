package com.gokuai.yunkuandroidsdk.data;

import com.yunkuent.sdk.data.ReturnResult;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Brandon on 15/4/20.
 */
public class FileListData extends BaseData {

    private final static String KEY_LIST = "list";
    private final static String KEY_COUNT = "count";

    private ArrayList<FileData> list;
    private int count;

    public static FileListData create(String result) {

        FileListData fileListData = new FileListData();

        ReturnResult returnResult = ReturnResult.create(result);
        int code = returnResult.getStatusCode();
        fileListData.setCode(code);
        JSONObject json;
        try {
            json = new JSONObject(returnResult.getResult());
        } catch (Exception e) {
            json = null;
        }

        if (json != null) {
            fileListData.count = json.optInt(KEY_COUNT);
            ArrayList<FileData> list = new ArrayList<>();
            if (code == HttpStatus.SC_OK) {
                JSONArray array = json.optJSONArray(KEY_LIST);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        FileData data = null;
                        try {
                            data = FileData.create(array.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list.add(data);
                    }
                }
                fileListData.setList(list);
            } else {
                fileListData.setErrorCode(json.optInt(KEY_ERRORCODE));
                fileListData.setErrorMsg(json.optString(KEY_ERRORMSG));
            }

        }
        return fileListData;
    }

    public ArrayList<FileData> getList() {
        return list;
    }

    public void setList(ArrayList<FileData> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
