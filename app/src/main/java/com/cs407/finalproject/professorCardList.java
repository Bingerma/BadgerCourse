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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class professorCardList extends AppCompatActivity {

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
        setContentView(R.layout.activity_professor_card_list);
        Intent intent = getIntent();
        String message = intent.getStringExtra("courseUrl");
        String api = intent.getStringExtra("courseUrl") + "/grades";

        backButton = findViewById(R.id.buttonPf);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(professorCardList.this, courseCardList.class);
                startActivity(intent1);
            }
        });

        recyclerView = findViewById(R.id.recyclerViewPf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize cardItemList and populate it with data
        cardItemList = new ArrayList<>();
        {

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ArrayList<String> profNameHolder = new ArrayList<>();
                            try{
                                JSONObject jsonObject1 = new JSONObject(response);
                                JSONArray courseOfferings = jsonObject1.getJSONArray("courseOfferings");
                                for (int i = 0; i < courseOfferings.length(); i++) {
                                    JSONObject courseOffering = courseOfferings.getJSONObject(i);
                                    JSONArray sections = courseOffering.getJSONArray("sections");
                                    for (int j = 0; j < sections.length(); j++) {
                                        JSONObject section = sections.getJSONObject(j);
                                        JSONArray instructors = section.getJSONArray("instructors");

                                        for (int k = 0; k < instructors.length(); k++) {
                                            JSONObject instructor = instructors.getJSONObject(k);
                                            String instructorName = instructor.getString("name");
                                            if (!(profNameHolder.contains(instructorName))){
                                                profNameHolder.add(instructorName);
                                            }
                                        }
                                    }
                                }
                                for (int i = 0; i < profNameHolder.size(); i++){
                                    cardItemList.add(new CardItem(profNameHolder.get(i), profNameHolder.get(i), message, profNameHolder.get(i)));
//                                    Log.d("testlog", profNameHolder.get(i));
                                }
                                adapter.notifyDataSetChanged();
                            }catch (JSONException error){
                                throw new RuntimeException(error);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                Intent intent = new Intent(professorCardList.this, ProfessorDetails.class);
                CardItem clickedItem = cardItemList.get(position);
                intent.putExtra("courseUrl", clickedItem.url);
                intent.putExtra("selectedProfessorName", clickedItem.selectedProfName);
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
