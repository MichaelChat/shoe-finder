package com.example.shoefinder;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DisplayInformationFromInternet extends AsyncTask<String, Void, Void> {

    private static final int DESCRIPTION_ENDING_LENGTH = 20;
    private static final int DESCRIPTION_BEGINNING = 0;
    private static final String SRC = "src";
    private static final String BRANDLOGO = "brandlogo";
    private static final String PRODUCT_IMG = "product_img";
    private static final String SCHUHCENTER_DE = "https://www.schuhcenter.de/";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0";
    public static final String ZOOM_0 = "zoom-0";
    public static final String DATA_SRC = "data-src";
    public static final String PROPERTY = "property";
    public static final String META = "meta";

    private final TextView _textView;
    private final ImageView _imageView;
    private final ImageView _logoView;

    private Document _document_complete;
    private Document _document_shortened;
    private String _imgSrc = "";
    private String _logoSrc = "";

    public DisplayInformationFromInternet(TextView textView, ImageView imageView, ImageView logoView) {
        _textView = textView;
        _imageView = imageView;
        _logoView = logoView;
    }

    protected Void doInBackground(String... urls) {
        try {
            String id = urls[0];
            String url_complete = SCHUHCENTER_DE + id;
            _document_complete = Jsoup.connect(url_complete).userAgent(USER_AGENT).ignoreHttpErrors(true).get();

            int length = id.length();
            if (length == 13) {
                String url_shortened = url_complete.substring(0, url_complete.length() - 2);
                _document_shortened = Jsoup.connect(url_shortened).userAgent(USER_AGENT).ignoreHttpErrors(true).get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void v) {
        boolean wasSuccessful = exec(_document_complete);
        if (!wasSuccessful && _document_shortened != null) {
            exec(_document_shortened);
        }
    }

    private boolean exec(Document document) {
        boolean containsImage = getImageSrc(document);
        boolean containsLogo = getLogoSrc(document);
        new DownloadImageFromInternet(_imageView).execute(_imgSrc);
        new DownloadImageFromInternet(_logoView).execute(_logoSrc);
        setDescription(document);
        return containsImage && containsLogo;
    }


    private boolean getImageSrc(Document result) {
        Elements metaTags = result.getElementsByTag(META);

        for (Element metaTag : metaTags) {
            String property = metaTag.attr(PROPERTY);

            if("og:image".equals(property)) {
                _imgSrc = metaTag.attr("content");
                return true;
            }
        }
        return false;
    }

    private boolean getLogoSrc(Document result) {
        Element brandLogoElementDiv = result.getElementsByClass(BRANDLOGO).first();
        if (brandLogoElementDiv != null) {
            for (Element e : brandLogoElementDiv.children()) {
                if (e.hasAttr(SRC)) {
                    _logoSrc = e.attr(SRC);
                    return true;
                }
            }
        }
        return false;
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
