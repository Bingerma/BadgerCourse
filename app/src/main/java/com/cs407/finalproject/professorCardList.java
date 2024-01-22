package com.cs407.finalproject;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class professorCardList extends AppCompatActivity {
    private Button backButton;
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardItemList;
    private String courseUrl;
    private String apiEndpoint;
    private String courseAbrv;
    private String courseName;
    private boolean isFolded;
    private Button searchButton;
    public static class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
        private final List<CardItem> cardItemList;
        public CardAdapter(List<CardItem> cardItemList) {
            this.cardItemList = cardItemList;
        }
        private OnCardClickListener onCardClickListener;
        public CardAdapter(List<CardItem> cardItemList, OnCardClickListener onCardClickListener) {
            this.cardItemList = cardItemList;
            this.onCardClickListener = onCardClickListener;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_course_card, parent, false);
            return new CardViewHolder(view, onCardClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            CardItem item = cardItemList.get(position);
            holder.titleTextView.setText(item.getTitle());
            holder.contentTextView.setText(item.getContent());
        }

        @Override
        public int getItemCount() {
            return cardItemList.size();
        }

        public interface OnCardClickListener {
            void onCardClick(int position);
        }


        static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView titleTextView, contentTextView;
            OnCardClickListener onCardClickListener;

            CardViewHolder(View view, OnCardClickListener onCardClickListener) {
                super(view);
                this.onCardClickListener = onCardClickListener;
                titleTextView = view.findViewById(R.id.titleTextView);
                contentTextView = view.findViewById(R.id.contentTextView);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onCardClickListener.onCardClick(getAdapterPosition());
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        isFolded = isDeviceFolded(configuration);
        if (isFolded) {
            setContentView(R.layout.activity_professor_card_list);
        } else {
            setContentView(R.layout.activity_professor_card_list_unfolded);
        }
        reinitializeComponents();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isFolded = isDeviceFolded(newConfig);

        if (isFolded) {
            setContentView(R.layout.activity_professor_card_list);
        } else {
            setContentView(R.layout.activity_professor_card_list_unfolded);
        }
        reinitializeComponents();
    }

    private boolean isDeviceFolded(Configuration config) {
        float aspectRatio = (float) config.screenWidthDp / config.screenHeightDp;
        return aspectRatio < 0.68;
    }

    private void reinitializeComponents() {
        initializeFields();
        setupBackButtonListener();
        setupRecyclerView();
        fetchDataFromApi();

    }

    private void initializeFields() {
        Intent intent = getIntent();
        courseUrl = intent.getStringExtra("courseUrl");
        courseAbrv = intent.getStringExtra("courseTitle");
        courseName = intent.getStringExtra("courseContent");
        apiEndpoint = courseUrl + "/grades";

        if (!isFolded){
            TextView textView = findViewById(R.id.courseAbrv);
            textView.setText(courseAbrv);
            textView = findViewById(R.id.courseName);
            textView.setText(courseName);
            fetchCourseInfo(courseAbrv);
            setUpSearchButton();
        }

        backButton = findViewById(R.id.buttonPf);
        recyclerView = findViewById(R.id.recyclerViewPf);
        cardItemList = new ArrayList<>();
    }

    private String fetchCourseInfo(String courseAbrv){
        String processedCourseAbrv = processCourseAbrvWisc(courseAbrv);
        String webUrl = "https://guide.wisc.edu/courses/" + processedCourseAbrv;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, webUrl,
                this::onWebResponse, this::onWebErrorResponse) {

        };
        queue.add(stringRequest);
        return null;
    }

    private void onWebResponse(String res){
        try {
            processWebResponse(res);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void processWebResponse(String response) throws JSONException {
        String noHtmlString = response.replaceAll("<[^>]*>", "");
        String normalizedString = noHtmlString.replace('\u00A0', ' ');
        String endString = "View detailsRequisites:";
        String extractedText;
        String credits;
        String description;
        String pre_requisite;
        String designation;
        String repeatable;
        String lastTaught;
        boolean isValid = false;

        Log.d("Test", normalizedString);
        String formattedCourseAbrv = courseAbrv + " — ";
        int startIndex = normalizedString.indexOf(formattedCourseAbrv);
        int endIndex = normalizedString.indexOf("\n", normalizedString.indexOf(endString, startIndex));

        if (startIndex != -1 && endIndex != -1){
            extractedText = normalizedString.substring(startIndex, endIndex);
        }
        else{
            extractedText = "";
            Log.d("newTag4", "not avaiable");
        }

        if (!extractedText.equals("")) {
            isValid = true;
            startIndex = extractedText.indexOf("\n");
            endIndex = extractedText.indexOf("\n", extractedText.indexOf("\n", startIndex + 1));
            credits = "<b>Cr: </b>" + extractedText.substring(startIndex, endIndex);

            startIndex = extractedText.indexOf(".");
            endIndex = extractedText.indexOf("View detailsRequisites");
            description = "<b>Description: </b>" + extractedText.substring(startIndex+2, endIndex-1);


            Log.d("newTag4", extractedText);
            startIndex = extractedText.indexOf("detailsRequisites:");
            if (extractedText.contains("Course Designation:")){
                endIndex = extractedText.indexOf("Course Designation:");
            }
            else{
                endIndex = extractedText.indexOf("CourseRepeatable");
            }
            pre_requisite = "<b>Pre Requisite: </b>" + extractedText.substring(startIndex+19, endIndex);

            startIndex = extractedText.indexOf("Course Designation: ");
            endIndex = extractedText.indexOf("Repeatable for Credit");
            designation = "<b>Designation: </b>" + extractedText.substring(startIndex+20, endIndex);
            designation = designation.replace("amp;", "");

            startIndex = extractedText.indexOf("Repeatable for Credit: ");
            endIndex = extractedText.indexOf("Last Taught: ");
            repeatable = "<b>Repeatable for Credits: </b>" + extractedText.substring(startIndex+23, endIndex);

            startIndex = extractedText.indexOf("Last Taught: ");
            lastTaught = "<b>Last Taught: </b>" + extractedText.substring(startIndex + 13);

            TextView textView = findViewById(R.id.courseCredit);
            textView.setText(Html.fromHtml(credits));
            textView = findViewById(R.id.courseRequisite);
            textView.setText(Html.fromHtml(pre_requisite));
            textView = findViewById(R.id.courseDesignation);
            textView.setText(Html.fromHtml(designation));
            textView = findViewById(R.id.courseRepeatable);
            textView.setText(Html.fromHtml(repeatable));
            textView = findViewById(R.id.courseLastTaught);
            textView.setText(Html.fromHtml(lastTaught));
            textView = findViewById(R.id.courseDescription);
            textView.setText(Html.fromHtml(description));
        }
    }

    private void setUpSearchButton() {
        searchButton = findViewById(R.id.home_button2);
        searchButton.setOnClickListener(click -> {
            Intent intent = new Intent(professorCardList.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void onWebErrorResponse(VolleyError e){
        Log.d("Error.Response", e.toString());
    }

    private String processCourseAbrvWisc (String courseAbrv){
        return courseAbrv.replaceAll("[0-9]", "")
                .trim().toLowerCase().replace(" ", "_").replace("&", "_");
    }

    private void setupBackButtonListener() {
        backButton.setOnClickListener(v -> navigateToCourseCardList());
    }

    private void navigateToCourseCardList() {
        Intent intent = new Intent(professorCardList.this, courseCardList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardAdapter(cardItemList, this::onCardClick);
        recyclerView.setAdapter(adapter);
    }

    private void onCardClick(int position) {
        Intent intent = new Intent(professorCardList.this, ProfessorDetails.class);
        CardItem clickedItem = cardItemList.get(position);
        intent.putExtra("courseUrl", clickedItem.getUrl());
        intent.putExtra("selectedProfessorName", clickedItem.getSelectedProfName());
        startActivity(intent);
    }

    private void fetchDataFromApi() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiEndpoint,
                this::onApiResponse, this::onApiErrorResponse) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token token=405f8fbc02dd4b7eb560ce722c7be74a");
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    private void onApiResponse(String response) {
        try {
            processApiResponse(response);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processApiResponse(String response) throws JSONException {
        ArrayList<String> profNameHolder = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray courseOfferings = jsonObject.getJSONArray("courseOfferings");
        extractInstructorNames(profNameHolder, courseOfferings);
        populateCardItemList(profNameHolder);
        adapter.notifyDataSetChanged();
    }

    private void extractInstructorNames(ArrayList<String> profNameHolder, JSONArray courseOfferings) throws JSONException {
        for (int i = 0; i < courseOfferings.length(); i++) {
            JSONObject courseOffering = courseOfferings.getJSONObject(i);
            JSONArray sections = courseOffering.getJSONArray("sections");
            for (int j = 0; j < sections.length(); j++) {
                JSONObject section = sections.getJSONObject(j);
                JSONArray instructors = section.getJSONArray("instructors");
                for (int k = 0; k < instructors.length(); k++) {
                    String instructorName = instructors.getJSONObject(k).getString("name");
                    if (!profNameHolder.contains(instructorName)) {
                        profNameHolder.add(instructorName);
                    }
                }
            }
        }
    }

    private void populateCardItemList(ArrayList<String> profNameHolder) {
        for (String name : profNameHolder) {
            cardItemList.add(new CardItem(name, name, courseUrl, name));
        }
    }

    private void onApiErrorResponse(VolleyError error) {
        Log.d("Error.Response", error.toString());
    }
    public static class CardItem {
        private final String title;
        private final String content;
        private final String url;
        private final String selectedProfName;
        public CardItem(String title, String content, String url, String selectedProfName){
            this.title = title;
            this.content = content;
            this.url = url;
            this.selectedProfName = selectedProfName;
        }
        public String getTitle() {
            return title;
        }
        public String getContent(){
            return content;
        }
        public String getUrl(){
            return url;
        }
        public String getSelectedProfName(){
            return selectedProfName;
        }
    }
}
