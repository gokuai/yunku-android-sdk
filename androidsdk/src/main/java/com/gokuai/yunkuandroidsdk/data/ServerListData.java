package com.gokuai.yunkuandroidsdk.data;

import android.os.Bundle;

import com.yunkuent.sdk.data.ReturnResult;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Brandon on 14-2-8.
 */
public class ServerListData extends BaseData {

    private ArrayList<ServerData> serverList;

    public static ServerListData create(String b) {
        ServerListData data = new ServerListData();
        ReturnResult returnResult = ReturnResult.create(b);
        int code = returnResult.getStatusCode();
        data.setCode(code);
        if (data.getCode() == HttpStatus.SC_OK) {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(returnResult.getResult());
            } catch (Exception e) {
                jsonArray = null;
            }

            if (jsonArray == null) {
                return null;
            }
            if (jsonArray != null) {
                ArrayList<ServerData> items = new ArrayList<ServerData>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonItem = jsonArray.optJSONObject(i);
                    ServerData item = ServerData.create(jsonItem);
                    if (item != null) {
                        items.add(item);
                    }
                }
                data.setServerList(items);
            } else {
                data.setServerList(new ArrayList<ServerData>());
            }

        } else {


        }
        return data;
    }

    public ArrayList<ServerData> getServerList() {
        return serverList;
    }

    public void setServerList(ArrayList<ServerData> serverList) {
        this.serverList = serverList;
    }
}
