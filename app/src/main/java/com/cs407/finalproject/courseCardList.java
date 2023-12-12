package com.cs407.finalproject;
import android.content.Intent;
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
import com.android.volley.Response;
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
    Button backButton;

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
        setContentView(R.layout.activity_course_card_list);
        Intent intent = getIntent();
        String userInput = intent.getStringExtra("userInput");

        backButton = findViewById(R.id.button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(courseCardList.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize cardItemList and populate it with data
        cardItemList = new ArrayList<>();
        {
            String api = "https://api.madgrades.com/v1/courses?query=" + userInput;
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray results = jsonObject.getJSONArray("results");
                                cardItemList.clear(); // Clear existing data
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject result = results.getJSONObject(i);
                                    String courseName = result.getString("name");
                                    int code = result.getInt("number");
                                    String abrv = result.getJSONArray("subjects").getJSONObject(0).getString("abbreviation");
                                    String courseNameShort = abrv + " " + String.valueOf(code);
                                    String urlPage = result.getString("url");
                                    Log.d("testlog", urlPage);
                                    cardItemList.add(new CardItem(courseNameShort, courseName, urlPage));
                                }
                                adapter.notifyDataSetChanged(); // Notify adapter about data change
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                    Log.d("Error.Response", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Token token=405f8fbc02dd4b7eb560ce722c7be74a");
                    return headers;
                }
            };
            queue.add(stringRequest);
        }
        adapter = new CardAdapter(cardItemList, new CardAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(int position) {
                Intent intent = new Intent(courseCardList.this, professorCardList.class);
                CardItem clickedItem = cardItemList.get(position);
//                intent.putExtra("title", clickedItem.getTitle());
//                intent.putExtra("content", clickedItem.getContent());
                intent.putExtra("courseUrl", clickedItem.getUrl());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
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
        public CardItem(String title, String content, String url){
            this.title = title;
            this.content = content;
            this.url = url;
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
    }
}
