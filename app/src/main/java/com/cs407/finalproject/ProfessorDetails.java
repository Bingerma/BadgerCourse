package com.cs407.finalproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;


import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.github.mikephil.charting.charts.BarChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

//class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
//
//    private List<CardItem> cardList;
//
//    public CardAdapter(List<CardItem> cardList) {
//        this.cardList = cardList;
//    }
//
//    @NonNull
//    @Override
//    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.card_item, parent, false);
//        return new CardViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
//        CardItem item = cardList.get(position);
//        holder.leftText.setText(item.getLeftText());
//        holder.rightText.setText(item.getRightText());
//
//        // Set the background color of rightText
//        int color = interpolateColor(item.getColorValue());
//        holder.rightText.setBackgroundColor(color);
//    }
//
//    private int interpolateColor(float value) {
//        float[] from = new float[3];
//        float[] to = new float[3];
//        float[] result = new float[3];
//
//        Color.colorToHSV(Color.RED, from);   // from red
//        Color.colorToHSV(Color.GREEN, to);   // to green
//
//        for (int i = 0; i < 3; i++) {
//            result[i] = from[i] + (to[i] - from[i]) * value / 5f; // Interpolating
//        }
//
//        return Color.HSVToColor(result);
//    }
//
//    @Override
//    public int getItemCount() {
//        return cardList.size();
//    }
//
////    static class CardViewHolder extends RecyclerView.ViewHolder {
////        TextView leftText, rightText;
////
////        public CardViewHolder(View view) {
////            super(view);
////            leftText = view.findViewById(R.id.left_text);
////            rightText = view.findViewById(R.id.right_text);
////        }
////    }
//}

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

//    private int interpolateColor(float value) {
//        float[] from = new float[3];
//        float[] to = new float[3];
//        float[] result = new float[3];
//
//        Color.colorToHSV(Color.RED, from);   // from red
//        Color.colorToHSV(Color.GREEN, to);   // to green
//
//        for (int i = 0; i < 3; i++) {
//            result[i] = from[i] + (to[i] - from[i]) * value / 5f; // Interpolating
//        }
//
//        return Color.HSVToColor(result);
//    }

//    private void updateNumberBox(float number, TextView textView) {
//        textView.setText(String.format(Locale.getDefault(), "%.1f", number));
//        int color = interpolateColor(number);
//        textView.setBackgroundColor(color);
//    }


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
                if (value.contains(".")) {
                    value = value.substring(0, value.indexOf("."));
                }
                Log.d("testtest", value);
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
    private boolean isFolded;

    public void updateUIWithResults(ArrayList<String> results) {

        if (!isFolded){
            TextView textView = findViewById(R.id.averageRating);
            textView.setText(results.get(0));
            textView = findViewById(R.id.numRatings);
            textView.setText(results.get(1));
            textView = findViewById(R.id.wouldRetake);
            textView.setText(results.get(2));
            textView = findViewById(R.id.difficulty);
            textView.setText(results.get(3));
        }else{
            TextView textView = findViewById(R.id.avgRatingText);
            textView.setText(results.get(0));
            textView = findViewById(R.id.numRatingsText);
            textView.setText(results.get(1));
            textView = findViewById(R.id.retakeText);
            textView.setText(results.get(2));
            textView = findViewById(R.id.diffText);
            textView.setText(results.get(3));
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        isFolded = isDeviceFolded(configuration);

        if (isFolded) {
            setContentView(R.layout.activity_professor_detail);
        } else {
            setContentView(R.layout.activity_professor_detail_unfolded);
        }

        initializeField();
        fetchData();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isFolded = isDeviceFolded(newConfig);

        if (isFolded) {
            setContentView(R.layout.activity_professor_detail);
        } else {
            setContentView(R.layout.activity_professor_detail_unfolded);
        }
        reinitializeComponents();
    }


    private boolean isDeviceFolded(Configuration config) {
        float aspectRatio = (float) config.screenWidthDp / config.screenHeightDp;
        return aspectRatio < 0.68;
    }

    private void reinitializeComponents() {
        initializeField();
        fetchData();
    }


    private void initializeField() {
        Intent intent = getIntent();
        apiUrl = intent.getStringExtra("courseUrl");
        selectedProfessor = intent.getStringExtra("selectedProfessorName");

        if (!isFolded) {
            setUpUnfolded();
        }
        else{
            setUpFolded();
        }
    }

    private void setUpFolded(){
        Button backButton = findViewById(R.id.backButtonDetailPage);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorDetails.this, professorCardList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }
    private void setUpUnfolded() {
        TextView textView = findViewById(R.id.professorName);
        textView.setText(selectedProfessor);
        Button backButton = findViewById(R.id.backButtonDetailPage);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfessorDetails.this, professorCardList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        Button homeButton = findViewById(R.id.home_button3);
        homeButton.setOnClickListener(click -> {
            Intent intent = new Intent(ProfessorDetails.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void fetchData() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest getGradeUrl = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String gradeUrl = jsonObject.getString("gradesUrl");
                    StringRequest getGrades = new StringRequest(Request.Method.GET, gradeUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
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
                                    Log.d("MyApp", "Grades for " + selectedProfessor + ": " + "A Count: " + selectedProfGradeCounts.aCount + ", " + "AB Count: " + selectedProfGradeCounts.abCount + ", ");
                                    if (!isFolded) {
                                        TextView textView = findViewById(R.id.gradeDistText);
                                        String displayText = "<b>A Count: </b>" + selectedProfGradeCounts.aCount + "<br>" + "<b>AB Count: </b>" + selectedProfGradeCounts.abCount + "<br>" + "<b>B Count: </b>" + selectedProfGradeCounts.bCount + "<br>" + "<b>BC Cont: </b>" + selectedProfGradeCounts.bcCount + "<br>" + "<b>C Cont: </b>" + selectedProfGradeCounts.cCount + "<br>" + "<b>D Cont: </b>" + selectedProfGradeCounts.dCount + "<br>" + "<b>F Cont: </b>" + selectedProfGradeCounts.fCount + "<br>";
                                        textView.setText(Html.fromHtml(displayText));

                                        PieChart pieChart = findViewById(R.id.piechart);
                                        int TotalCount = selectedProfGradeCounts.aCount + selectedProfGradeCounts.abCount + selectedProfGradeCounts.bCount + selectedProfGradeCounts.bcCount + selectedProfGradeCounts.cCount + selectedProfGradeCounts.dCount + selectedProfGradeCounts.fCount;
                                        float aPercent = (float) selectedProfGradeCounts.aCount / TotalCount;
                                        textView = findViewById(R.id.aText);
                                        String holder = "A   (" + String.format(Locale.getDefault(), "%.2f", aPercent*100) + "%)";
                                        textView.setText(holder);
                                        float abPercent = (float) selectedProfGradeCounts.abCount / TotalCount;
                                        textView = findViewById(R.id.abText);
                                        holder = "AB (" + String.format(Locale.getDefault(), "%.2f", abPercent*100) + "%)";
                                        textView.setText(holder);
                                        float bPercent = (float) selectedProfGradeCounts.bCount / TotalCount;
                                        textView = findViewById(R.id.bText);
                                        holder = "B   (" + String.format(Locale.getDefault(), "%.2f", bPercent*100) + "%)";
                                        textView.setText(holder);
                                        float bcPercent = (float) selectedProfGradeCounts.bcCount / TotalCount;
                                        textView = findViewById(R.id.bcText);
                                        holder = "BC (" + String.format(Locale.getDefault(), "%.2f", bcPercent*100) + "%)";
                                        textView.setText(holder);
                                        float cPercent = (float) selectedProfGradeCounts.cCount / TotalCount;
                                        textView = findViewById(R.id.cText);
                                        holder = "C   (" + String.format(Locale.getDefault(), "%.2f", cPercent*100) + "%)";
                                        textView.setText(holder);
                                        float dPercent = (float) selectedProfGradeCounts.dCount / TotalCount;
                                        textView = findViewById(R.id.dText);
                                        holder = "D   (" + String.format(Locale.getDefault(), "%.2f", dPercent*100) + "%)";
                                        textView.setText(holder);
                                        float fPercent = (float) selectedProfGradeCounts.fCount / TotalCount;
                                        textView = findViewById(R.id.fText);
                                        holder = "F   (" + String.format(Locale.getDefault(), "%.2f", fPercent*100) + "%)";
                                        textView.setText(holder);
                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        "A",
                                                        aPercent,
                                                        Color.parseColor("#39d0ae")));
                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        "AB",
                                                        abPercent,
                                                        Color.parseColor("#58508d")));
                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        "B",
                                                        bPercent,
                                                        Color.parseColor("#bc5090")));
                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        "BC",
                                                        bcPercent,
                                                        Color.parseColor("#ff6361")));
                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        "C",
                                                        cPercent,
                                                        Color.parseColor("#ffa600")));
                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        "D",
                                                        dPercent,
                                                        Color.parseColor("#091cd0")));
                                        pieChart.addPieSlice(
                                                new PieModel(
                                                        "F",
                                                        fPercent,
                                                        Color.parseColor("#d00505")));
                                    }

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

                                    String[] labels = new String[]{"A", "AB", "B", "BC", "C", "D", "F"};
                                    XAxis xAxis = barChart.getXAxis();
                                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                                    xAxis.setGranularity(1f);
                                    xAxis.setGranularityEnabled(true);
                                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Labels at the bottom
                                    xAxis.setDrawGridLines(false);

                                    barChart.invalidate();
                                }
                            } catch (JSONException error) {
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

