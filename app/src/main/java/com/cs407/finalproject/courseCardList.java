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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

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

        {
            String api = "https://api.madgrades.com/v1/courses?query=" + userInput;
            Log.d("testlog", api);

        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize cardItemList and populate it with data
        cardItemList = new ArrayList<>();

        // TODO: Fetch data and populate the list
        cardItemList.add(new CardItem("Card Title 1", "This is the content for Card 1."));
        cardItemList.add(new CardItem("Card Title 2", "This is the content for Card 2."));
        cardItemList.add(new CardItem("Card Title 3", "This is the content for Card 3."));
        cardItemList.add(new CardItem("Card Title 4", "This is the content for Card 4."));
        cardItemList.add(new CardItem("Card Title 5", "This is the content for Card 5."));


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
