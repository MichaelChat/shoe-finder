package com.example.shoefinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = findViewById(R.id.searchButton);
        EditText editText = findViewById(R.id.editTextNumber);
        TextView textView = findViewById(R.id.descriptionTextView);
        ImageView imageView = findViewById(R.id.imageView);
        ImageView logoView = findViewById(R.id.logoView);

        searchButton.setOnClickListener(x -> {
            String articleID = editText.getText().toString();
            new GetImageURLFromInternet(textView, imageView, logoView).execute(articleID);
        });
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView _imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            _imageView = imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            InputStream in;
            try {
                in = new URL(imageURL).openStream();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return BitmapFactory.decodeStream(in);
        }

        protected void onPostExecute(Bitmap result) {
                _imageView.setImageBitmap(result);
        }
    }

    private class GetImageURLFromInternet extends AsyncTask<String, Void, Document> {

        private static final int DESCRIPTION_ENDING_LENGTH = 20;
        private static final int DESCRIPTION_BEGINNING = 0;
        private static final String SRC = "src";
        private static final String BRANDLOGO = "brandlogo";
        private static final String EMPTY_STRING = "";
        private static final String PRODUCT_IMG = "product_img";
        private static final String SCHUHCENTER_DE = "https://www.schuhcenter.de/";
        private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0";

        private final TextView _textView;
        private final ImageView _imageView;
        private final ImageView _logoView;

        private GetImageURLFromInternet(TextView textView, ImageView imageView, ImageView logoView) {
            _textView = textView;
            _imageView = imageView;
            _logoView = logoView;
        }

        protected Document doInBackground(String... urls) {
            try {
                return Jsoup.connect(SCHUHCENTER_DE + urls[0]).userAgent(USER_AGENT).ignoreHttpErrors(true).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Document result) {
            if (result != null) {
                String imgSrc = getImageSrc(result);
                String logoSrc = getLogoSrc(result);

                new DownloadImageFromInternet(_imageView).execute(imgSrc);
                setDescription(result);
                new DownloadImageFromInternet(_logoView).execute(logoSrc);
            }
        }

        private String getImageSrc(Document result) {
            Element productImg = result.getElementById(PRODUCT_IMG);
            if (productImg == null) {
                return EMPTY_STRING;
            }
            return productImg.attr(SRC);
        }

        private String getLogoSrc(Document result) {
            String logoSrc = EMPTY_STRING;
            Element brandLogoElementDiv = result.getElementsByClass(BRANDLOGO).first();
            if (brandLogoElementDiv == null) {
                return logoSrc;
            }
            for (Element e : brandLogoElementDiv.children()) {
                if (e.hasAttr(SRC)) {
                    logoSrc = e.attr(SRC);
                }
            }
            return logoSrc;
        }

        private void setDescription(Document result) {
            String title = result.title();
            int titleLength = title.length();
            if (titleLength > DESCRIPTION_ENDING_LENGTH) {
                title = title.substring(DESCRIPTION_BEGINNING, titleLength - DESCRIPTION_ENDING_LENGTH);
            }
            _textView.setText(title);
        }
    }
}