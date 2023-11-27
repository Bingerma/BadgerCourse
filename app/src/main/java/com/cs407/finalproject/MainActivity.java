package com.cs407.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private ListView listViewResults;
    private ArrayAdapter<String> adapter;

    private String api = "curl -H \"Authorization: Token token=405f8fbc02dd4b7eb560ce722c7be74a\" https://api.madgrades.com/v1/courses?query=";
    private String out;

    private class FetchRunnable implements Runnable{
        @Override
        public void run() {

            String urlString = editTextSearch.getText().toString();
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

                    out = response.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("HTTP Request", "Error: " + e.getMessage());
                return;
            }


        }
    }

    private void startSearch(View view){
        FetchRunnable run = new FetchRunnable();
        new Thread(run).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextSearch = findViewById(R.id.editTextSearch);
        listViewResults = findViewById(R.id.listViewResults);

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
    }
}