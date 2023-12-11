package com.cs407.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class GradeCounts {
    int aCount, abCount, bCount, bcCount, cCount, dCount, fCount, sCount, uCount, crCount, nCount, pCount, iCount, nwCount, nrCount, otherCount;

    // Constructor
    public GradeCounts(int aCount, int abCount, int bCount, int bcCount, int cCount, int dCount, int fCount) {
        this.aCount = aCount;
        this.abCount = abCount;
        this.bCount = bCount;
        this.bcCount = bcCount;
        this.cCount = cCount;
        this.dCount = dCount;
        this.fCount = fCount;
    }

    public void updateCounts(GradeCounts other) {
        this.aCount += other.aCount;
        this.abCount += other.abCount;
        this.bCount += other.bCount;
        this.cCount += other.cCount;
        this.bcCount += other.bcCount;
        this.dCount += other.dCount;
        this.fCount += other.fCount;
    }
}

public class ProfessorDetails extends AppCompatActivity {
    BarChart barChart;
    private Button backButton;
    private Map<String, String> redditMap = new HashMap<>();
    private ListView redditList;
    private ArrayAdapter<String> adapter;
    private List<String> displayList = new ArrayList<>();

    String redditURL = "https://www.reddit.com/r/UWMadison/search/?q=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Map<String, GradeCounts> instructorGradeMap = new HashMap<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_detail);
        Intent intent = getIntent();
        String apiUrl = intent.getStringExtra("courseUrl");
        String selectedProfessor = intent.getStringExtra("selectedProfessorName");

        redditList = findViewById(R.id.redditList);
        backButton = findViewById(R.id.backDetails);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest getGradeUrl = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String gradeUrl = jsonObject.getString("gradesUrl");
                            Log.d("MyAppLog", gradeUrl);

                            StringRequest getGrades = new StringRequest(Request.Method.GET, gradeUrl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try{
                                                JSONObject jsonObject1 = new JSONObject(response);
                                                JSONArray courseOfferings = jsonObject1.getJSONArray("courseOfferings");
                                                for (int i = 0; i < courseOfferings.length(); i++) {
                                                    JSONObject courseOffering = courseOfferings.getJSONObject(i);
                                                    JSONArray sections = courseOffering.getJSONArray("sections");
                                                    for (int j = 0; j < sections.length(); j++) {
                                                        JSONObject section = sections.getJSONObject(j);
                                                        JSONArray instructors = section.getJSONArray("instructors");
                                                        int aCount = section.getInt("aCount");
                                                        int abCount = section.getInt("abCount");
                                                        int bCount = section.getInt("bCount");
                                                        int bcCount = section.getInt("bcCount");
                                                        int cCount = section.getInt("cCount");
                                                        int dCount = section.getInt("dCount");
                                                        int fCount = section.getInt("fCount");

                                                        for (int k = 0; k < instructors.length(); k++) {
                                                            JSONObject instructor = instructors.getJSONObject(k);
                                                            String instructorName = instructor.getString("name");
                                                            GradeCounts gradeCounts = new GradeCounts(aCount, abCount, bCount, bcCount, cCount, dCount, fCount);
                                                            instructorGradeMap.computeIfAbsent(instructorName, val -> new GradeCounts(0, 0, 0, 0, 0, 0, 0)).updateCounts(gradeCounts);
                                                        }
                                                    }
                                                }
//                                                instructorGradeMap.forEach((name, grades) -> Log.d("MyApp", name+grades.aCount + grades.abCount + grades.bCount + grades.bcCount + grades.cCount));
                                                if (instructorGradeMap.containsKey(selectedProfessor)) {
                                                    GradeCounts selectedProfGradeCounts = instructorGradeMap.get(selectedProfessor);
                                                    Log.d("MyApp", "Grades for " + selectedProfessor + ": " +
                                                                    "A Count: " + selectedProfGradeCounts.aCount + ", " +
                                                                    "AB Count: " + selectedProfGradeCounts.abCount + ", "
                                                    );
                                                    TextView textView = findViewById(R.id.textView);
                                                    String displayText = "Grades for " + selectedProfessor + ": " +
                                                            "A Count: " + selectedProfGradeCounts.aCount + ", " +
                                                            "AB Count: " + selectedProfGradeCounts.abCount + ", " +
                                                            "B Count: " + selectedProfGradeCounts.bCount + ", " +
                                                            "BC Cont: " + selectedProfGradeCounts.bcCount + ", " +
                                                            "C Cont: " + selectedProfGradeCounts.cCount + ", " +
                                                            "D Cont: " + selectedProfGradeCounts.dCount + ", " +
                                                            "F Cont: " + selectedProfGradeCounts.fCount + ", "
                                                            ;
                                                    textView.setText(displayText);
                                                    barChart = findViewById(R.id.barChart);
                                                    ArrayList<BarEntry> entries = new ArrayList<>();
                                                    entries.add(new BarEntry(0f, selectedProfGradeCounts.aCount));
                                                    entries.add(new BarEntry(1f, selectedProfGradeCounts.abCount));
                                                    entries.add(new BarEntry(2f, selectedProfGradeCounts.bCount));
                                                    entries.add(new BarEntry(3f, selectedProfGradeCounts.bcCount));
                                                    entries.add(new BarEntry(4f, selectedProfGradeCounts.cCount));
                                                    entries.add(new BarEntry(5f, selectedProfGradeCounts.dCount));
                                                    entries.add(new BarEntry(6f, selectedProfGradeCounts.fCount));
                                                    BarDataSet barDataSet = new BarDataSet(entries, "Label");
                                                    BarData barData = new BarData(barDataSet);
                                                    barChart.setData(barData);

                                                    String[] labels = new String[] {"A", "AB", "B", "BC", "C", "D", "F"};
                                                    XAxis xAxis = barChart.getXAxis();
                                                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                                                    xAxis.setGranularity(1f);
                                                    xAxis.setGranularityEnabled(true);
                                                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Labels at the bottom
                                                    xAxis.setDrawGridLines(false);

                                                    barChart.invalidate();
                                                }
                                            }catch (JSONException error){
                                                throw new RuntimeException(error);
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    throw new RuntimeException(error);
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Content-Type", "application/json");
                                    headers.put("Authorization", "Token token=405f8fbc02dd4b7eb560ce722c7be74a");
                                    return headers;
                                }
                            };

                            queue.add(getGrades);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                throw new RuntimeException(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token token=405f8fbc02dd4b7eb560ce722c7be74a");
                return headers;
            }
        };

        queue.add(getGradeUrl);


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);

        redditList.setAdapter(adapter);

        // Set item click listener to open a new activity with the selected course URL
        redditList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // TODO: Open reddit url in default browser
                String redditLink = redditMap.get(displayList.get(position));
                // Create an Intent to open the URL in the default browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redditLink));
                startActivity(browserIntent);
            }
        });

        if (selectedProfessor != null) {
            // Use the received message as needed
            Log.d("ReceiverActivity", "Received message: " + apiUrl + " " + selectedProfessor);
            new ProfessorDetails.FetchDataTask(ProfessorDetails.this).execute(redditURL + selectedProfessor);

        } else {
            Log.e("ReceiverActivity", "No message received");
        }

    }


    private static class FetchDataTask extends AsyncTask<String, Void, List<Course>> {

        private WeakReference<ProfessorDetails> activityReference;

        public FetchDataTask(ProfessorDetails activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected List<Course> doInBackground(String... params) {
            fetchURL(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(List<Course> result) {
            ProfessorDetails activity = activityReference.get();
            Map<String, String> rMap = activity.redditMap;
            if (rMap != null) {
                activity.adapter.clear();
                for (Map.Entry<String, String> entry : rMap.entrySet()) {
                    String title = entry.getKey();
                    activity.adapter.add(title);
                }
            }
        }

        private void fetchURL(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Log.e("HTTP Request", "Connected");

                try {

                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        //Log.d("HTTP Request", line);
                        response.append(line);
                    }

                    parsePage(response.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("HTTP Request", "Error: " + e.getMessage());
            } catch (XmlPullParserException e){
                Log.e("HTTP Parse", "Error: " + e.getMessage());
            }
        }


        private void parsePage(String html) throws XmlPullParserException, IOException {
            ProfessorDetails activity = activityReference.get();
            Log.d("jkdn","factory");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            Log.d("jkdn","pullp");
            XmlPullParser parser = factory.newPullParser();
            Log.d("jkdn","reader");
            parser.setInput(new java.io.StringReader(html));
            int eventType = parser.getEventType();

            int count = 0;
            while (eventType != XmlPullParser.END_DOCUMENT && count < 3) {
                if (eventType == XmlPullParser.START_TAG && "a".equals(parser.getName())) {
                    // Assuming the desired links have the "data-testid" attribute
                    String dataTestId = parser.getAttributeValue(null, "data-testid");
                    Log.d("parser","found match");
                    if (dataTestId != null && "post-title".equals(dataTestId)) {
                        String href = parser.getAttributeValue(null, "href");
                        String ariaLabel = parser.getAttributeValue(null, "aria-label");
                        activity.redditMap.put(ariaLabel,href);
                        count++;
                    }
                }
                eventType = parser.next();
            }
        }
    }
}

