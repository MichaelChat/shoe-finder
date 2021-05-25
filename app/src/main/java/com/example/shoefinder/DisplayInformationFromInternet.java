package com.example.shoefinder;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class DisplayInformationFromInternet extends AsyncTask<String, Void, Document> {

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

    public DisplayInformationFromInternet(TextView textView, ImageView imageView, ImageView logoView) {
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
            new DownloadImageFromInternet(_logoView).execute(logoSrc);
            setDescription(result);
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
        Element brandLogoElementDiv = result.getElementsByClass(BRANDLOGO).first();
        if (brandLogoElementDiv != null) {
            for (Element e : brandLogoElementDiv.children()) {
                if (e.hasAttr(SRC)) {
                    return e.attr(SRC);
                }
            }
        }
        return EMPTY_STRING;
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
