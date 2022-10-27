package com.ttcreator.mycoloring;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.ttcreator.mycoloring.utill.ImageLoaderUtill;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.senab.photoview.ColourImageView;

public class LoadAsyncBitmap implements Runnable {

    String urls;
    Context context;
    Bitmap image;
    ColourImageView imageView;

    LoadAsyncBitmap(Context context, String urls) {
        this.urls = urls;
        this.context = context;
    }

    public LoadAsyncBitmap(ColourImageView colourImageView, String urls) {
        this.imageView = colourImageView;
        this.urls = urls;
    }

    @Override
    public void run() {
        getBitmap(urls);
    }

    public void getBitmap(String... url) {
        String stringUrl = url[0];
        image = null;
        InputStream inputStream;
        try {
            inputStream = new java.net.URL(stringUrl).openStream();
            image = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBtmImage() {
        return image;
    }


    public Bitmap loadImage(String url) {
        Bitmap bitmapThread = null;
        try {
            URL myUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bitmapThread = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
        }
        return bitmapThread;
    }

    public static void showLagreImageAsynWithNoCacheOpen(ImageView imageView, String url, ImageLoadingListener listener) {
        ImageLoaderUtill.getInstance().displayImage(url, imageView,
                ImageLoaderUtill.DetailImageOptionsNoCache(),
                listener);
    }
    
}
