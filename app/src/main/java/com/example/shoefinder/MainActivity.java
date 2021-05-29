package com.example.shoefinder;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.nodes.Document;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = findViewById(R.id.searchButton);
        ClearableEditText editText = findViewById(R.id.editTextNumber);
        TextView textView = findViewById(R.id.descriptionTextView);
        ImageView imageView = findViewById(R.id.imageView);
        ImageView logoView = findViewById(R.id.logoView);

        searchButton.setOnClickListener(x -> {
            String articleID = editText.getText().toString();
            int length = articleID.length();
            if (length != 13 && length != 11) {
                searchButton.setError("Article ID needs to be either 11 or 13 digits long.");
            } else {
                new DisplayInformationFromInternet(textView, imageView, logoView).execute(articleID);
            }
        });

        searchButton.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchButton.setError(null);
            }
        });
    }

}