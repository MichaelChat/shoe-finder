package com.example.shoefinder;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
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

        // TODO sysout keyCode
        editText.setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_SEARCH);

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchButton, editText, textView, imageView, logoView);
                return true;
            }
            return false;
        });

        searchButton.setOnClickListener(x -> performSearch(searchButton, editText, textView, imageView, logoView));

        searchButton.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchButton.setError(null);
            }
        });
    }

    private void performSearch(Button searchButton, ClearableEditText editText, TextView textView, ImageView imageView, ImageView logoView) {
        String articleID = editText.getText().toString();
        int length = articleID.length();
        if (length != 13 && length != 11) {
            searchButton.setError("Article ID needs to be either 11 or 13 digits long.");
        } else {
            searchButton.setError(null);
            new DisplayInformationFromInternet(textView, imageView, logoView).execute(articleID);
        }
    }

}