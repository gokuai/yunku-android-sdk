package com.gokuai.yunkuandroidsdk.data;

import com.yunkuent.sdk.data.ReturnResult;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 *
 */
public class BaseData {

    protected final static String KEY_ERRORCODE = "error_code";
    protected final static String KEY_ERRORMSG = "error_msg";

    protected int code;
    private String key;
    private Object obj;
    protected int error_code;
    protected String error_msg;


    public static   BaseData create(String result) {
        BaseData data=new BaseData();
        ReturnResult returnResult = ReturnResult.create(result);
        int code = returnResult.getStatusCode();
        data.setCode(code);

        if (code == HttpStatus.SC_OK) {

        } else {
            JSONObject json;
            try {
                json = new JSONObject(returnResult.getResult());
            } catch (Exception e) {
                json = null;
            }
            if (json != null) {
                data.setErrorCode(json.optInt(KEY_ERRORCODE));
                data.setErrorMsg(json.optString(KEY_ERRORMSG));
            }
        }
        return data;
    }


    public static  BaseData create(String result, String key) {
        BaseData data=new BaseData();
        ReturnResult returnResult = ReturnResult.create(result);
        int code = returnResult.getStatusCode();
        data.setCode(code);
        JSONObject json;
        try {
            json = new JSONObject(returnResult.getResult());
        } catch (Exception e) {
            json = null;
            //兼容bug处理
            if (code == HttpStatus.SC_OK) {
            }
        }
        if (json != null) {
            if (code == HttpStatus.SC_OK) {
                data.setKey(json.optString(key));
            } else {
                data.setErrorCode(json.optInt(KEY_ERRORCODE));
                data.setErrorMsg(json.optString(KEY_ERRORMSG));
            }
        }
        return data;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getErrorCode() {
        return error_code;
    }

    public void setErrorCode(int errno_code) {
        this.error_code = errno_code;
    }

    public String getErrorMsg() {
        return error_msg;
    }

    public void setErrorMsg(String errno_msg) {
        this.error_msg = errno_msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
