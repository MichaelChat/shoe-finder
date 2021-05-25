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
            new DisplayInformationFromInternet(textView, imageView, logoView).execute(articleID);
        });
    }
}