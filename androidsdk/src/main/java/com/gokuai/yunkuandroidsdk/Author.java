package com.gokuai.yunkuandroidsdk;

public class Author {
    public static String CLIENT_ID;
    public static  String CLIENT_SECRET;
    private String mAccount;
    private String mPassWord;
    private String mExchangeToken;
    private String mToken;


    public Author(String clientId, String clientSecret, String account, String password) {
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
        mAccount = account;
        mPassWord = password;
    }

    public Author(String clientId, String clientSecret,String exchangeToken){
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
        mExchangeToken=exchangeToken;
    }

    public void accessToken(){

    }

    public String getToken(){
        return mToken;
    }


}
