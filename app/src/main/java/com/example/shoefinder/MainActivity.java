package com.example.shoefinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Connection.Response;
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

        searchButton.setOnClickListener(x -> {
            Editable textID = editText.getText();
            if (textID.length() == 0) {
                printHint("Please enter the item number.");
            } else {
                new GetImageURLFromInternet().execute(textID.toString());
            }
        });
    }

    private void printHint(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView _imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            _imageView = imageView;
            printHint("Please wait, it may take a few minute...");
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
            if (result == null) {
                printHint("Could not download image.");
            } else {
                _imageView.setImageBitmap(result);
            }

        }
    }

    private class GetImageURLFromInternet extends AsyncTask<String, Void, Document> {

        private static final int STATUS_OK = 200;

        protected Document doInBackground(String... urls) {
            try {
                Response response = Jsoup.connect("https://www.schuhcenter.de/" + urls[0]).ignoreHttpErrors(true).execute();
                int statusCode = response.statusCode();
                if (statusCode == STATUS_OK) {
                    return response.parse();
                } else {
                    printHint("Could not find the article corresponding to this number.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Document result) {
            if (result == null) {
                printHint("Could not parse the website that was requested.");
            } else {
                String imgSrc = getImageSrc(result);
                String logoSrc = getLogoSrc(result);

                new DownloadImageFromInternet(findViewById(R.id.imageView)).execute(imgSrc);
                setDescription(result);
                new DownloadImageFromInternet(findViewById(R.id.logoView)).execute(logoSrc);
            }
        }

        private String getImageSrc(Document result) {
            Element productImg = result.getElementById("product_img");
            if (productImg == null) {
                printHint("Could not find the image that was requested");
                return "";
            }
            return productImg.attr("data-cfsrc");
        }

        private String getLogoSrc(Document result) {
            String logoSrc = "";
            Element brandLogoElementDiv = result.getElementsByClass("brandlogo").first();
            if (brandLogoElementDiv == null) {
                printHint("Could not find the brand logo that was requested");
                return logoSrc;
            }
            for (Element e : brandLogoElementDiv.children()) {
                if (e.hasAttr("data-cfsrc")) {
                    logoSrc = e.attr("data-cfsrc");
                }
            }
            return logoSrc;
        }

        private void setDescription(Document result) {
            String title = result.title();
            title = title.substring(0, title.length() - 20);
            TextView textView = findViewById(R.id.descriptionTextView);
            textView.setText(title);
        }
    }


}