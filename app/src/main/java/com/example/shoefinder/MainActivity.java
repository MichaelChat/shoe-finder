package com.example.shoefinder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


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
            new DisplayInformationFromInternet(textView, imageView, logoView).execute(articleID);
        });
    }
}