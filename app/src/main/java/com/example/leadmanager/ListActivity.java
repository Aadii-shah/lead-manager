package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.leadmanager.adapters.AutoCompleteAdapterContact;
import com.example.leadmanager.adapters.FollowUpAdapter;
import com.example.leadmanager.adapters.LeadAdapter;
import com.example.leadmanager.adapters.RecyclerViewTouchListener;
import com.example.leadmanager.models.Contact;
import com.example.leadmanager.models.Lead;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ListActivity extends AppCompatActivity implements LeadAdapter.RecyclerViewAdapterListener, FollowUpAdapter.RecyclerViewAdapterListener {
    private RecyclerView recyclerView;
    private LeadAdapter leadAdapter;
    private FollowUpAdapter followUpAdapter;
    private List<Lead> itemsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        category = getIntent().getStringExtra("category");
        if (category == null) {
            category = "new_lead";
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.extended_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, NewLeadActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView = findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();

        switch (category) {
            case "new_lead":
                getLeads("New Lead");
                break;

            case "today_meetings":
                getLeadsWithFollowup();
                break;

            case "unanswered":
                getLeads("Unanswered");
                break;

            case "busy":
                getLeads("Busy");
                break;

            case "interested":
                getLeads("Interested");
                break;

            case "not_interested":
                getLeads("Not Interested");
                break;

            case "converted":
                getLeads("Converted");
                break;

            case "all_leads":
                getAllLeads();
                break;
        }
    }

    @Override
    public void onValueChanged(float amount, String category) {

    }

    @Override
    public void onItemRemoved(Lead item) {

    }

    @Override
    public void onItemUpdated(float amount, int count) {

    }

    private void getLeadsv1(String category) {

        long time = getMidNightTimeStamp();
        Log.v("dipak", "" + time);
        Task<QuerySnapshot> dataRef = db
                .collectionGroup("leads").whereGreaterThan("creationDate", time).whereEqualTo("status", category).get(Source.CACHE)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.v("dipak", "" + queryDocumentSnapshots.getDocuments());
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            Log.v("dipak1111", "" + document.getReference().getParent().getParent());
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(document.getData());
                            Lead lead = gson.fromJson(jsonElement, Lead.class);
                            //lead = (Lead) document.getData();
                            itemsList.add(lead);
                            leadAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    private void getLeads(String category) {

        leadAdapter = new LeadAdapter(this, itemsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(leadAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(ListActivity.this, LeadDetailsActivity.class);
                intent.putExtra("contactUid", "new_lead");
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        long time = getMidNightTimeStamp();

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");

        Source CACHE = Source.CACHE;

        dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", category).get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    Lead lead = gson.fromJson(jsonElement, Lead.class);
                    itemsList.add(lead);
                }
                leadAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllLeads() {
        leadAdapter = new LeadAdapter(this, itemsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(leadAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(ListActivity.this, LeadDetailsActivity.class);
                intent.putExtra("contactUid", "new_lead");
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        long time = getMidNightTimeStamp();

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");

        Source CACHE = Source.CACHE;

        dataRef.whereGreaterThan("creationDate", time).get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    Lead lead = gson.fromJson(jsonElement, Lead.class);
                    itemsList.add(lead);
                }
                leadAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getLeadsWithFollowup() {

        followUpAdapter = new FollowUpAdapter(this, itemsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(leadAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(ListActivity.this, LeadDetailsActivity.class);
                intent.putExtra("contactUid", "new_lead");
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        long time = getMidNightTimeStamp();

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");

        Source CACHE = Source.CACHE;

        dataRef.whereGreaterThan("latestFollowup", time).get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    Lead lead = gson.fromJson(jsonElement, Lead.class);
                    itemsList.add(lead);
                }
                leadAdapter.notifyDataSetChanged();
            }
        });
    }

    private Long getMidNightTimeStamp() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getDefault());
        //Log.v("dipak", TimeZone.getDefault() + "");
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis() / 1000;
    }

}