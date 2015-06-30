package com.gokuai.yunkuandroidsdk.data;

import org.json.JSONObject;

/**
 * Created by Brandon on 14-2-8.
 */
public class ServerData {
    private String host;
    private String port;
    private boolean httpsSupport;

    public boolean getHttpsSupport() {
        return httpsSupport;
    }

    public void setHttpsSupport(boolean httpsSupport) {
        this.httpsSupport = httpsSupport;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    public static ServerData create(JSONObject json) {
        ServerData data = new ServerData();
        data.setHost(json.optString("host"));
        data.setPort(json.optString("port"));
        data.setHttpsSupport(json.optString("https").equals("1"));

        return data;
    }
}
