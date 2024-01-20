package com.cs407.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Locale;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class CardItem {
    private String leftText;
    private String rightText;
    private float colorValue;

    public CardItem(String leftText, String rightText, float colorValue) {
        this.leftText = leftText;
        this.rightText = rightText;
        this.colorValue = colorValue;
    }

    // getters and setters
    public String getLeftText() {
        return leftText;
    }

    public String getRightText() {
        return rightText;
    }

    public float getColorValue() {
        return colorValue;
    }
}

class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> cardList;

    public CardAdapter(List<CardItem> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem item = cardList.get(position);
        holder.leftText.setText(item.getLeftText());
        holder.rightText.setText(item.getRightText());

        // Set the background color of rightText
        int color = interpolateColor(item.getColorValue());
        holder.rightText.setBackgroundColor(color);
    }

    private int interpolateColor(float value) {
        float[] from = new float[3];
        float[] to = new float[3];
        float[] result = new float[3];

        Color.colorToHSV(Color.RED, from);   // from red
        Color.colorToHSV(Color.GREEN, to);   // to green

        for (int i = 0; i < 3; i++) {
            result[i] = from[i] + (to[i] - from[i]) * value / 5f; // Interpolating
        }

        return Color.HSVToColor(result);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView leftText, rightText;

        public CardViewHolder(View view) {
            super(view);
            leftText = view.findViewById(R.id.left_text);
            rightText = view.findViewById(R.id.right_text);
        }
    }
}

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


class FetchData extends AsyncTask<String, Void, ArrayList<String>> {


    private WeakReference<ProfessorDetails> activityReference;
    ArrayList<String> rmpDataArray = new ArrayList<>();
    String data = "";

    private int interpolateColor(float value) {
        float[] from = new float[3];
        float[] to = new float[3];
        float[] result = new float[3];

        Color.colorToHSV(Color.RED, from);   // from red
        Color.colorToHSV(Color.GREEN, to);   // to green

        for (int i = 0; i < 3; i++) {
            result[i] = from[i] + (to[i] - from[i]) * value / 5f; // Interpolating
        }

        return Color.HSVToColor(result);
    }

    private void updateNumberBox(float number, TextView textView) {
        textView.setText(String.format(Locale.getDefault(), "%.1f", number));
        int color = interpolateColor(number);
        textView.setBackgroundColor(color);
    }


    public FetchData(ProfessorDetails activity) {
        this.activityReference = new WeakReference<>(activity);
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        try {
            // URL of the web page
            String url = "https://www.ratemyprofessors.com/search/professors/18418?q=";
            String profName = params[0];
            url += profName;
            Document doc = Jsoup.connect(url).get();

            String htmlStr = doc.toString();
            int startIndex = htmlStr.indexOf("avgRating");
            if (startIndex != -1) {
                int endIndex = htmlStr.indexOf(",", startIndex);
                String value = htmlStr.substring((startIndex - 2) + "averageRating".length(), endIndex).trim();
                rmpDataArray.add(value);
            } else {
                rmpDataArray.add("0");
            }

            startIndex = htmlStr.indexOf("numRatings");
            if (startIndex != -1) {
                int endIndex = htmlStr.indexOf(",", startIndex);
                String value = htmlStr.substring((startIndex) + "numRatings".length() + 2, endIndex).trim();
                rmpDataArray.add(value);
            } else {
                rmpDataArray.add("0");
            }

            startIndex = htmlStr.indexOf("wouldTakeAgainPercent");
            if (startIndex != -1) {
                int endIndex = htmlStr.indexOf(",", startIndex);
                String value = htmlStr.substring((startIndex) + "wouldTakeAgainPercent".length() + 2, endIndex).trim();
                rmpDataArray.add(value);
            } else {
                rmpDataArray.add("0");
            }

            startIndex = htmlStr.indexOf("avgDifficulty");
            if (startIndex != -1) {
                int endIndex = htmlStr.indexOf(",", startIndex);
                String value = htmlStr.substring((startIndex) + "avgDifficulty".length() + 2, endIndex).trim();
                rmpDataArray.add(value);
            } else {
                rmpDataArray.add("0");
            }
            Log.d("testlog", rmpDataArray.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rmpDataArray;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        ProfessorDetails activity = activityReference.get();
        if (activity != null && result != null) {
            activity.updateUIWithResults(result);
        }
    }
}


public class ProfessorDetails extends AppCompatActivity {
    BarChart barChart;
    private Button backButton;
    private Map<String, GradeCounts> instructorGradeMap = new HashMap<>();
    private String apiUrl;
    private String selectedProfessor;

    public void updateUIWithResults(ArrayList<String> results) {
        List<CardItem> items = new ArrayList<>();
        List<String> names = new ArrayList<>();
        names.add("Average Rating: ");
        names.add("Number of Ratings");
        names.add("Would Take Again (%): ");
        names.add("Difficulty ");
        int counter = 0;
        for (String result : results) {
            float colorValue = Float.parseFloat(result);
            if (counter == 2) {
                colorValue = colorValue / 10;
                colorValue = colorValue / 2;
            }
            if (counter == 1) {
                if (colorValue < 5) {
                    colorValue = 1;
                } else if (colorValue > 5 && colorValue < 10) {
                    colorValue = 2;
                } else if (colorValue > 10 && colorValue < 15) {
                    colorValue = 3;
                } else if (colorValue > 15 && colorValue < 20) {
                    colorValue = 4;
                } else if (colorValue > 20) {
                    colorValue = 5;
                }
            }
            if (counter == 3) {
                colorValue = 5 - colorValue;
            }
            items.add(new CardItem(names.get(counter), result, colorValue));
            counter++;
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CardAdapter adapter = new CardAdapter(items);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Make the layout adaptable
        setContentView(R.layout.activity_professor_detail);

        initializeField();
        setBackButton();
        fetchData();
    }

    private void initializeField() {
        Intent intent = getIntent();
        apiUrl = intent.getStringExtra("courseUrl");
        selectedProfessor = intent.getStringExtra("selectedProfessorName");

    }

    private void setBackButton() {
        backButton = findViewById(R.id.backDetails);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfessorDetails.this, professorCardList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    private void fetchData(){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest getGradeUrl = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String gradeUrl = jsonObject.getString("gradesUrl");

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
                                                if (instructorGradeMap.containsKey(selectedProfessor)) {
                                                    GradeCounts selectedProfGradeCounts = instructorGradeMap.get(selectedProfessor);
                                                    Log.d("MyApp", "Grades for " + selectedProfessor + ": " +
                                                            "A Count: " + selectedProfGradeCounts.aCount + ", " +
                                                            "AB Count: " + selectedProfGradeCounts.abCount + ", "
                                                    );
                                                    //TODO: consider removing
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
        new FetchData(this).execute(selectedProfessor);
    }
}

