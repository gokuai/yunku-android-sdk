/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gokuai.yunkuandroidsdk.gallery.touchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gokuai.yunkuandroidsdk.Config;
import com.gokuai.yunkuandroidsdk.DebugFlag;
import com.gokuai.yunkuandroidsdk.FileDataManager;
import com.gokuai.yunkuandroidsdk.R;
import com.gokuai.yunkuandroidsdk.callback.ParamsCallBack;
import com.gokuai.yunkuandroidsdk.data.FileData;
import com.gokuai.yunkuandroidsdk.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlTouchImageView extends RelativeLayout {

    protected ProgressBar mProgressBar;
    protected TouchImageView mImageView;
    protected TextView mTextView;
    protected Button mButton;
    private ImageLoadTask mImageLoadTask;
    private FileData mFileData;
    private boolean isLocalImage;

    private String errorCode;
    private String errorMsg;

    private AsyncTask mImageUrlTask;


    protected Context mContext;
    private OnClickListener mListener;

    private static final String IMAGEVIEW_FORMAT_FAILED = "40017";
    private static final String IMAGEVIEW_SIZE_FAILED = "400171";
    private static final String IMAGEVIEW_PREVIEW_FAILED = "50302";

    public UrlTouchImageView(Context ctx, OnClickListener listener) {
        super(ctx);
        mContext = ctx;
        init();
        mListener = listener;

    }


    public TouchImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public Button getButton() {
        return mButton;
    }

    @SuppressWarnings("deprecation")
    protected void init() {

        mImageView = new TouchImageView(mContext);

        LayoutParams params = new LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT);
        mImageView.setLayoutParams(params);
        this.addView(mImageView);

        mImageView.setVisibility(GONE);

        mProgressBar = new ProgressBar(mContext, null,
                android.R.attr.progressBarStyleInverse);
        params = new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(params);
        mProgressBar.setIndeterminate(false);
        this.addView(mProgressBar);

        mTextView = new TextView(mContext);
        params = new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTextView.setLayoutParams(params);
        int left = getResources().getDimensionPixelSize(
                R.dimen.gallery_percent_tv_padding_left);
        int top = getResources().getDimensionPixelSize(
                R.dimen.gallery_percent_tv_padding_top);
        int right = getResources().getDimensionPixelSize(
                R.dimen.gallery_percent_tv_padding_right);
        int bottom = getResources().getDimensionPixelSize(
                R.dimen.gallery_percent_tv_padding_bottom);
        mTextView.setPadding(left, top, right, bottom);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setText(R.string.tip_is_preparing_for_data);
        mTextView.setVisibility(View.GONE);
        this.addView(mTextView);

        mButton = new Button(mContext);
        params = new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mButton.setLayoutParams(params);
        mButton.setGravity(Gravity.CENTER);
        mButton.setText(R.string.tip_image_button);
        mButton.setTextColor(Color.WHITE);
        mButton.setBackgroundResource(R.drawable.btn_check_image);
        mButton.setVisibility(View.GONE);
        this.addView(mButton);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageUrlTask = new AsyncTask<Void, Integer, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        if (mFileData != null) {
                            FileData fileData = FileDataManager.getInstance().getFileInfoSync(mFileData.getFullpath());

                            return getOriImage(fileData, new ParamsCallBack() {
                                @Override
                                public void callBack(Object obj) {
                                    publishProgress((int) obj);
                                }
                            });

                        } else {
                            return null;
                        }

                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        mButton.setVisibility(GONE);
                        mTextView.setVisibility(VISIBLE);
                        mProgressBar.setVisibility(VISIBLE);
                        if (values[0] == -1) {
                            mTextView.setText(mContext.getString(R.string.tip_is_loading));
                        } else {
                            mTextView.setText(values[0] + " %");
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);

                        if (mImageView != null && mProgressBar != null) {
                            if (bitmap != null) {
                                mButton.setVisibility(GONE);
                                mImageView.setScaleType(ScaleType.MATRIX);
                                mImageView.setImageBitmap(bitmap);

                                mTextView.setVisibility(View.GONE);
                                mImageView.setVisibility(VISIBLE);
                                mProgressBar.setVisibility(GONE);

                            } else {
                                mButton.setVisibility(GONE);
                                mTextView.setVisibility(VISIBLE);
                                mTextView.setText(R.string.tip_open_image_failed);
                                mProgressBar.setVisibility(View.VISIBLE);
                            }
                        }


                    }
                }.execute();
            }
        });

    }

    public void setUrl(FileData data) {
        mImageView.setOnClickListener(mListener);
        isLocalImage = false;
        if (mImageLoadTask == null) {
            mImageLoadTask = new ImageLoadTask();
            mFileData = data;
            mImageLoadTask.execute(data);
        }

    }

    public void setLocalUrl(String localPath) {
        isLocalImage = true;
        mImageView.setOnClickListener(mListener);
        if (mImageLoadTask == null) {
            mImageLoadTask = new ImageLoadTask();
            mImageLoadTask.execute(localPath);
        }
    }


    public class ImageLoadTask extends AsyncTask<Object, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(Object... objs) {
            if (isLocalImage) {
                String localPath = objs[0].toString();
                File file = new File(localPath);
                return Util.decodeSampledBitmapFromFile(file);
            } else {
                FileData data = (FileData) objs[0];
                String fileHash = data.getFilehash();
                String localPath = Config.getLocalFilePath(fileHash);
                //FIXME
                File file = new File(localPath);
                if (file.exists()) {
                    // from SD cache
                    Bitmap b = Util.decodeSampledBitmapFromFile(file);
                    if (b != null)
                        return b;
                    else {
                        return getFromInternet(data, new ParamsCallBack() {
                            @Override
                            public void callBack(Object obj) {
                                publishProgress((int) obj);
                            }
                        });
                    }

                } else {

                    return getFromInternet(data, new ParamsCallBack() {
                        @Override
                        public void callBack(Object obj) {
                            publishProgress((int) obj);
                        }
                    });
                }

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mTextView.setVisibility(VISIBLE);
            if (values[0] == -1) {
                mTextView.setText(mContext.getString(R.string.tip_is_loading));
            } else {
                mTextView.setText(values[0] + " %");
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mImageView != null && mProgressBar != null) {
                if (bitmap == null) {
                    if (errorCode.equals(IMAGEVIEW_FORMAT_FAILED)) {
                        mTextView.setVisibility(VISIBLE);
                        mTextView.setText(R.string.tip_image_format_nonsupport);
                        mProgressBar.setVisibility(View.GONE);
                        mButton.setVisibility(View.VISIBLE);

                    } else if (errorCode.equals(IMAGEVIEW_SIZE_FAILED)) {
                        mTextView.setVisibility(VISIBLE);
                        mTextView.setText(R.string.tip_image_loading_failed);
                        mProgressBar.setVisibility(View.GONE);
                        mButton.setVisibility(View.VISIBLE);

                    } else if (errorCode.equals(IMAGEVIEW_PREVIEW_FAILED)) {
                        mTextView.setVisibility(VISIBLE);
                        mTextView.setText(R.string.tip_image_loading_failed);
                        mProgressBar.setVisibility(View.GONE);
                        mButton.setVisibility(View.VISIBLE);

                    } else {
                        mTextView.setVisibility(VISIBLE);
                        mTextView.setText(R.string.tip_open_image_failed);
                        mProgressBar.setVisibility(View.GONE);
                        mButton.setVisibility(View.VISIBLE);
                    }

                } else {
                    mImageView.setScaleType(ScaleType.MATRIX);
                    mImageView.setImageBitmap(bitmap);

                    mTextView.setVisibility(View.GONE);
                    mImageView.setVisibility(VISIBLE);
                    mProgressBar.setVisibility(GONE);
                }

            }


        }

    }

    private static final int IO_BUFFER_SIZE = 8 * 1024;


    /*
    预览图片获取失败后，下载原始图片
     */
    private Bitmap getOriImage(FileData data, ParamsCallBack callBack) {

        String uri = data.getUri();
        String thumbBigPath = Config.getBigThumbPath(data.getFilehash());
        File file = new File(thumbBigPath);
        if (file.exists()) {
            Bitmap b = Util.decodeSampledBitmapFromFile(file);
            if (b != null) {
                return b;
            } else {
                file.delete();
            }
        } else {
            if (!file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }
        }


        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {

            callBack.callBack(-1);

            FileOutputStream fos = new FileOutputStream(new File(thumbBigPath));

            final URL url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            int totalLength = urlConnection.getContentLength();
            //mResponseCode = urlConnection.getResponseCode();
            if (totalLength == -1) {

                return null;
            }

            in = new BufferedInputStream(url.openStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(fos, IO_BUFFER_SIZE);

            byte[] buffer = new byte[4096];
            int length;
            long byteNow = 0;
            while ((length = in.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                byteNow += length;
                callBack.callBack((int) (((float) byteNow / (float) totalLength) * (float) 100));
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
            }
        }

        file = new File(thumbBigPath);

        return !file.exists() ? null : Util.decodeSampledBitmapFromFile(file);
    }




    private Bitmap getFromInternet(FileData data, ParamsCallBack callBack) {
        String urlString = data.getThumbBig();
        String thumbBigPath = Config.getBigThumbPath(data.getFilehash());

        File file = new File(thumbBigPath);
        if (file.exists()) {
            Bitmap b = Util.decodeSampledBitmapFromFile(file);
            if (b != null) {
                return b;
            } else {
                file.delete();
            }
        } else {
            if (!file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }
        }


        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {

            callBack.callBack(-1);

            FileOutputStream fos = new FileOutputStream(new File(thumbBigPath));

            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            int totalLength = urlConnection.getContentLength();
            if (totalLength == -1) {

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urlString);
                HttpResponse response = httpclient.execute(httpGet);
                String errorStr = EntityUtils.toString(response.getEntity(), "UTF-8");

                JSONObject jsonObject = new JSONObject(errorStr);
                errorCode = jsonObject.optString("error_code");
                errorMsg = jsonObject.optString("error_msg");

                DebugFlag.log("error_msg", errorCode);
                DebugFlag.log("error_msg", errorMsg);
                return null;
            }

            in = new BufferedInputStream(url.openStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(fos, IO_BUFFER_SIZE);

            byte[] buffer = new byte[4096];
            int length;
            long byteNow = 0;
            while ((length = in.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                byteNow += length;
                callBack.callBack((int) (((float) byteNow / (float) totalLength) * (float) 100));
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
            }
        }

        file = new File(thumbBigPath);

        return !file.exists() ? null : Util.decodeSampledBitmapFromFile(file);
    }
}