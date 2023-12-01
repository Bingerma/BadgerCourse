package com.cs407.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfessorSelect extends AppCompatActivity {
    // Currently Empty - Will get passed the URL for a mad grades website

    // See fetchURL in MainActivity of guidance on how to parse

    // Example:
    // URL: https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923

    //{"uuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","number":407,"name":"Foundations of Mobile Systems and Applications",
    //"names":["Found of Mobl Systms\u0026Applctns"],
    // "subjects":[{"name":"Computer Sciences","abbreviation":"COMP SCI","code":"266"}],
    // "url":"https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923",
    // "gradesUrl":"https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923/grades",
    // "courseOfferings":[{"uuid":"297e78e9-cfc3-3e26-9740-36c63e3be950","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923",
    // "termCode":1222,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/297e78e9-cfc3-3e26-9740-36c63e3be950"},
    // {"uuid":"6930688a-4bbf-3212-adbb-b99b5cdc86db","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","termCode":1204,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/6930688a-4bbf-3212-adbb-b99b5cdc86db"},
    // {"uuid":"5be924d0-6308-3062-98c4-136b16944a66","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","termCode":1174,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/5be924d0-6308-3062-98c4-136b16944a66"},
    // {"uuid":"089fc737-6209-30dc-b8eb-3d3141ac89e9","courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923","termCode":1164,"name":"Found of Mobl Systms\u0026Applctns","url":"https://api.madgrades.com/v1/course_offerings/089fc737-6209-30dc-b8eb-3d3141ac89e9"}]}

    // Of these, gradesUrl is probably the most important to us
    // Following that link will give us something like this:

    // {"courseUuid":"3b14a2ff-b999-338f-b2a8-65b95ce52923",
    // "cumulative":{"total":882,"aCount":400,"abCount":262,"bCount":141,"bcCount":43,
    // "cCount":7,"dCount":10,"fCount":2,"sCount":15,"uCount":2,"crCount":0,"nCount":0,"pCount":0,
    // "iCount":0,"nwCount":0,"nrCount":0,"otherCount":0},
    // "courseOfferings":[{"termCode":1222,"cumulative":{"total":196,"aCount":52,"abCount":62,"bCount":47,"bcCount":33,"cCount":0,"dCount":2,
    // "fCount":0,"sCount":0,"uCount":0,"crCount":0,"nCount":0,"pCount":0,"iCount":0,"nwCount":0,"nrCount":0,"otherCount":0},
    // "sections":[{"sectionNumber":1,"instructors":[{"id":6294901,"name":"X / KEATON LEPPANEN"},{"id":6248778,"name":"X / MOHIT LOGANATHAN"},
    // {"id":3661702,"name":"SUMAN BANERJEE"},{"id":6249338,"name":"X / ISHA PADMANABAN"},{"id":6375575,"name":"X / MUHAMMAD HARIS NOOR"}],
    // "total":196,"aCount":52,"abCount":62,"bCount":47,"bcCount":33,"cCount":0,"dCount":2,"fCount":0,"sCount":0,"uCount":0,"crCount":0,"nCount":0,"pCount":0,"iCount":0,"nwCount":0,"nrCount":0,"otherCount":0}]},



    // Note: If it seems easier to parse the default webpage, we can switch to the normal webpage
    // by removing the api. and the v1/
    // https://api.madgrades.com/v1/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923
    // ->
    // https://madgrades.com/courses/3b14a2ff-b999-338f-b2a8-65b95ce52923

    private Button backButton;
    private Map<Integer, String> professorMap = new HashMap<>();
    private ListView profList;
    private ArrayAdapter<String> adapter;
    private List<String> displayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_select);

        Intent intent = getIntent();
        String message = intent.getStringExtra("courseUrl");

        profList = findViewById(R.id.professorList);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorSelect.this, MainActivity.class);
                startActivity(intent);
            }
        });


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);

        profList.setAdapter(adapter);

        // Set item click listener to open a new activity with the selected course URL
        profList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO: Open a new activity and pass the selected professor information
                Intent intent = new Intent(ProfessorSelect.this, ProfessorSelect.class);
                //intent.putExtra("profName", courseList.get(position).getUrl());
                startActivity(intent);
            }
        });

        if (message != null) {
            // Use the received message as needed
            Log.d("ReceiverActivity", "Received message: " + message + "/grades");
            new ProfessorSelect.FetchDataTask(ProfessorSelect.this).execute(message + "/grades");

        } else {
            Log.e("ReceiverActivity", "No message received");
        }

    }



    private static class FetchDataTask extends AsyncTask<String, Void, List<Course>> {

        private WeakReference<ProfessorSelect> activityReference;

        public FetchDataTask(ProfessorSelect activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected List<Course> doInBackground(String... params) {
            fetchURL(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(List<Course> result) {
            ProfessorSelect activity = activityReference.get();
            Map<Integer, String> pMap = activity.professorMap;
            if (pMap != null) {
                activity.adapter.clear();
                for (Map.Entry<Integer, String> entry : pMap.entrySet()) {
                    String professorName = entry.getValue();

                    activity.adapter.add(professorName);
                }
            }
        }

        private void fetchURL(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Log.e("HTTP Request", "Connected");

                try {
                    // Set the Authorization header
                    urlConnection.setRequestProperty("Authorization", "Token token=405f8fbc02dd4b7eb560ce722c7be74a");

                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        Log.e("HTTP Request", line);
                        response.append(line);
                    }

                    parseJson(response.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("HTTP Request", "Error: " + e.getMessage());
            }
        }

        private void parseJson(String json) {
            ProfessorSelect activity = activityReference.get();


            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray courseOfferingsArray = jsonObject.getJSONArray("courseOfferings");

                for (int i = 0; i < courseOfferingsArray.length(); i++) {
                    JSONObject courseOfferingObject = courseOfferingsArray.getJSONObject(i);
                    JSONArray sectionsArray = courseOfferingObject.getJSONArray("sections");

                    for (int j = 0; j < sectionsArray.length(); j++) {
                        JSONObject sectionObject = sectionsArray.getJSONObject(j);
                        JSONArray instructorsArray = sectionObject.getJSONArray("instructors");

                        for (int k = 0; k < instructorsArray.length(); k++) {
                            JSONObject instructorObject = instructorsArray.getJSONObject(k);
                            String professorName = instructorObject.getString("name");

                            if (professorName != "null"){
                                int instructorId = instructorObject.getInt("id");
                                activity.professorMap.put(instructorId, professorName);
                                Log.e("Found Professor", professorName);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("JSON Parsing", "Error: " + e.getMessage());
            }

        }

    }

}