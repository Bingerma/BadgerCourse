package com.cs407.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_card_list);
        Intent intent = getIntent();
        String userInput = intent.getStringExtra("userInput");



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize cardItemList and populate it with data
        cardItemList = new ArrayList<>();

//        cardItemList.add(new CardItem("Card Title 1", "This is the content for Card 1."));
//        cardItemList.add(new CardItem("Card Title 2", "This is the content for Card 2."));
        {
//            cardItemList.add(new CardItem("Card Title 1", "This is the content for Card 1."));
//            cardItemList.add(new CardItem("Card Title 2", "This is the content for Card 2."));
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


//                                    String code = result.getJSONArray("subjects").getJSONObject(0).getString("code");
                                    String abrv = result.getJSONArray("subjects").getJSONObject(0).getString("abbreviation");
                                    String courseNameShort = abrv + " " + String.valueOf(code);
                                    cardItemList.add(new CardItem(courseNameShort, courseName));
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


        adapter = new CardAdapter(cardItemList);
        recyclerView.setAdapter(adapter);
    }




    // Method to update data in adapter
    public void updateData(List<CardItem> newData) {
        cardItemList.clear();
        cardItemList.addAll(newData);
        adapter.notifyDataSetChanged();
    }


    public static class CardItem {
        private final String title;
        private final String content;

        public CardItem(String title, String content){
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getContent(){
            return content;
        }
    }

    public static class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
        private final List<CardItem> cardItemList;

        public CardAdapter(List<CardItem> cardItemList) {
            this.cardItemList = cardItemList;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_course_card, parent, false);
            return new CardViewHolder(view);
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

        static class CardViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, contentTextView;

            CardViewHolder(View view) {
                super(view);
                titleTextView = view.findViewById(R.id.titleTextView);
                contentTextView = view.findViewById(R.id.contentTextView);
            }
        }
    }
}
