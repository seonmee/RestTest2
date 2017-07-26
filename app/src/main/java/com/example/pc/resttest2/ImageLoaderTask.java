package com.example.pc.resttest2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by samsung on 2017-07-26.
 */

public class ImageLoaderTask extends android.os.AsyncTask<String,Void,Bitmap> {
    private ImageView dispImageView;

    public ImageLoaderTask(ImageView dispImgView){
        this.dispImageView=dispImgView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imgUrl=params[0];
        Bitmap bmp=null;
        try{
            bmp= BitmapFactory.decodeStream((InputStream)new URL(imgUrl).getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap!=null){
            dispImageView.setImageBitmap(bitmap);
        }
    }
}
