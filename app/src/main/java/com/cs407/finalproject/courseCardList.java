package com.cs407.finalproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class courseCardList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardItemList;
    private Button backButton;
    private Button searchButton;
    private boolean isFolded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        isFolded = isDeviceFolded(configuration);
        if (isFolded) {
            setContentView(R.layout.activity_course_card_list);
        } else {
            setContentView(R.layout.activity_course_card_list_unfolded);
        }

        reinitializeComponents();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isFolded = isDeviceFolded(newConfig);

        if (isFolded) {
            setContentView(R.layout.activity_course_card_list);
        } else {
            setContentView(R.layout.activity_course_card_list_unfolded);
        }
        reinitializeComponents();
    }

    private boolean isDeviceFolded(Configuration config) {
        float aspectRatio = (float) config.screenWidthDp / config.screenHeightDp;
        return aspectRatio < 0.68;
    }

    private void reinitializeComponents() {
        setupBackButton();
        setupRecyclerView();
        fetchCourseData();
        if (!isFolded) {
            setUpSearchButton();
        }
    }


    private void setupBackButton() {
        backButton = findViewById(R.id.button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(courseCardList.this, MainActivity.class);
            startActivity(intent);
        });
    }


    private void setUpSearchButton() {
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(click -> {
            Intent intent = new Intent(courseCardList.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardItemList = new ArrayList<>();
        adapter = new CardAdapter(cardItemList, this::onCardClicked);
        recyclerView.setAdapter(adapter);
    }

    private void onCardClicked(int position) {
        Intent intent = new Intent(courseCardList.this, professorCardList.class);
        CardItem clickedItem = cardItemList.get(position);
        intent.putExtra("courseUrl", clickedItem.getUrl());
        intent.putExtra("courseTitle", clickedItem.getTitle());
        intent.putExtra("courseContent", clickedItem.getContent());
        startActivity(intent);
    }

    private void fetchCourseData() {
        Intent intent = getIntent();
        String userInput = intent.getStringExtra("userInput");

        String api = "https://api.madgrades.com/v1/courses?query=" + userInput;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                this::onResponse, this::onErrorResponse) {
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

    private void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray("results");
            cardItemList.clear();
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                processCourse(result);
            }
            if (!isFolded){
                int resultCount = cardItemList.size();
                String searchResult = "Your search returned " + resultCount + " result(s).";
                TextView textView = findViewById(R.id.courseInfoTextContent);
                textView.setText(searchResult);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processCourse(JSONObject result) throws JSONException {
        String courseName = result.getString("name");
        int code = result.getInt("number");
        String abrv = result.getJSONArray("subjects").getJSONObject(0).getString("abbreviation");
        String courseNameShort = abrv + " " + code;
        String urlPage = result.getString("url");
        cardItemList.add(new CardItem(courseNameShort, courseName, urlPage));
    }

    private void onErrorResponse(VolleyError error) {
        Log.d("Error.Response", error.toString());
    }

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


    public void updateData(List<CardItem> newData) {
        cardItemList.clear();
        cardItemList.addAll(newData);
        adapter.notifyDataSetChanged();
    }

    public static class CardItem {
        private final String title;
        private final String content;
        private final String url;

        public CardItem(String title, String content, String url) {
            this.title = title;
            this.content = content;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            return url;
        }
    }
}
