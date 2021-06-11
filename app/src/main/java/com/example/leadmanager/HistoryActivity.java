package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.leadmanager.adapters.FollowUpAdapter;
import com.example.leadmanager.adapters.HistoryAdapter;
import com.example.leadmanager.adapters.LeadAdapter;
import com.example.leadmanager.adapters.RecyclerViewTouchListener;
import com.example.leadmanager.models.HistoryItem;
import com.example.leadmanager.models.LeadApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.RecyclerViewAdapterListener{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        String leadUid = getIntent().getStringExtra("lead_uid");

        RecyclerView recyclerView;
        HistoryAdapter historyAdapter;
        List<HistoryItem> itemsList;
        itemsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        historyAdapter = new HistoryAdapter(this, itemsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(historyAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Source CACHE = Source.CACHE;

        Log.v("dipppppp", "kkk" + leadUid);
        assert leadUid != null;
        db.collection("cache").document(user.getUid()).collection("Leads")
                .document(leadUid).get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult()!=null) {

                HistoryItem historyItems[] = (HistoryItem[]) task.getResult().get("history");

                if(historyItems!=null) {
                    /*Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(document.getData());
                        LeadApp lead = gson.fromJson(jsonElement, LeadApp.class);
                        lead.setUid(document.getId());
                        itemsList.add(lead);*/
                    itemsList.addAll(Arrays.asList(historyItems));
                    historyAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onValueChanged(float amount, String category) {

    }

    @Override
    public void onItemRemoved(HistoryItem item) {

    }

    @Override
    public void onItemUpdated(float amount, int count) {

    }
}