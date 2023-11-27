package com.cs407.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button searchButton;
    private ListView listViewResults;
    private ArrayAdapter<String> adapter;

    private String api = "curl -H \"Authorization: Token token=405f8fbc02dd4b7eb560ce722c7be74a\" https://api.madgrades.com/v1/courses?query=";
    private String out;

    private String fetchURL(String urlString){
        try {
            java.net.URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                // Set the Authorization header
                urlConnection.setRequestProperty("Authorization", "Token token=405f8fbc02dd4b7eb560ce722c7be74a");

                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e("HTTP Request", "Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextSearch = findViewById(R.id.editTextSearch);
        listViewResults = findViewById(R.id.listViewResults);
        searchButton = findViewById(R.id.Search);

        // Sample data
        String[] data = {"Apple", "Banana", "Orange", "Mango", "Grapes"};

        // Set up the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        listViewResults.setAdapter(adapter);

        // Set up the search functionality
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
                listViewResults.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchURL(editTextSearch.getText().toString());
                    }
                }
        );
    }
}