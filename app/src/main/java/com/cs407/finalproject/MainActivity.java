package com.cs407.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button searchButton;


    private String api = "https://api.madgrades.com/v1/courses?query=";

    private ListView listViewResults;
    private ArrayAdapter<String> adapter;
    private List<String> displayList = new ArrayList<>();
    private ArrayList<Course> courseList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        listViewResults = findViewById(R.id.listViewResults);
        searchButton = findViewById(R.id.Search);

        // Set up the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);

        /*adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, R.id.textViewItem, displayList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Use the correct inflater
                View view = super.getView(position, convertView, parent);

                // Customize the view if needed

                return view;
            }
        }; */

        listViewResults.setAdapter(adapter);

        // Set item click listener to open a new activity with the selected course URL
        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String selectedCourse = adapter.getItem(position);
                if (position >= 0 && position < 5) {
                    // Open a new activity and pass the selected course URL
                    Intent intent = new Intent(MainActivity.this, ProfessorSelect.class);
                    intent.putExtra("courseUrl", courseList.get(position).getUrl());
                    startActivity(intent);
                }
            }
        });



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchDataTask(MainActivity.this, adapter, courseList).execute(api + editTextSearch.getText().toString().replaceAll("\\s", ""));
            }
        });
    }



    private static class FetchDataTask extends AsyncTask<String, Void, List<Course>> {

        private WeakReference<MainActivity> activityReference;
        private ArrayAdapter<String> adapter;
        private ArrayList<Course> courses;

        public FetchDataTask(MainActivity activity, ArrayAdapter<String> adapter, ArrayList<Course> courses) {
            this.activityReference = new WeakReference<>(activity);
            this.adapter = adapter;
            this.courses = courses;
        }

        @Override
        protected List<Course> doInBackground(String... params) {
            return fetchURL(params[0]);
        }

        @Override
        protected void onPostExecute(List<Course> result) {
            MainActivity activity = activityReference.get();
            if (activity != null) {
                activity.adapter.clear();
                for (Course course : result) {
                    Log.d("Class Info", "Class Name: " + course.getName() + ", URL: " + course.getUrl());
                    activity.adapter.add(course.getName());
                }
                activity.courseList.clear();
                activity.courseList.addAll(result);

            }
        }

        private List<Course> fetchURL(String urlString) {
            try {
                URL url = new URL(urlString);
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

                    return parseJson(response.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("HTTP Request", "Error: " + e.getMessage());
                return null;
            }
        }

        private List<Course> parseJson(String json) {
            List<Course> courses = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray resultsArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < Math.min(5, resultsArray.length()); i++) {
                    JSONObject courseObject = resultsArray.getJSONObject(i);
                    String className = courseObject.getString("name");
                    String classUrl = courseObject.getString("url");

                    courses.add(new Course(className, classUrl));
                }
            } catch (JSONException e) {
                Log.e("JSON Parsing", "Error: " + e.getMessage());
            }

            return courses;
        }
    }
}