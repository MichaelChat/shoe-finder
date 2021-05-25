package com.example.shoefinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
    ImageView _imageView;

    public DownloadImageFromInternet(ImageView imageView) {
        _imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        InputStream in = null;
        try {
            in = new URL(imageURL).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(in);
    }

    protected void onPostExecute(Bitmap result) {
        _imageView.setImageBitmap(result);
    }
}
