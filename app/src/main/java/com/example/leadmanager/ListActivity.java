package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.leadmanager.adapters.AutoCompleteAdapterContact;
import com.example.leadmanager.adapters.FollowUpAdapter;
import com.example.leadmanager.adapters.LeadAdapter;
import com.example.leadmanager.adapters.RecyclerViewTouchListener;
import com.example.leadmanager.models.Contact;
import com.example.leadmanager.models.Lead;
import com.example.leadmanager.models.LeadApp;
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

import static com.example.leadmanager.Utility.getMidNightTimeStamp;

public class ListActivity extends AppCompatActivity implements LeadAdapter.RecyclerViewAdapterListener, FollowUpAdapter.RecyclerViewAdapterListener {
    private RecyclerView recyclerView;
    private LeadAdapter leadAdapter;
    private FollowUpAdapter followUpAdapter;
    private List<LeadApp> itemsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private String category;
    private SearchView searchView;
    private Toolbar toolbar;
    private boolean isToday = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        //searchView.startAnimation(fab_close);
        //int searchBarId = searchView.getId();//getContext().getResources().getIdentifier("android:id/search_bar",null,null);
        //LinearLayout searchBar = (SearchView) searchView.findViewById(searchBarId);
        searchView.setLayoutTransition(new LayoutTransition());
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(this.getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                leadAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(category.equals("today_meetings")) {
                    followUpAdapter.getFilter().filter(s);
                } else {
                    leadAdapter.getFilter().filter(s);
                }
                return false;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        category = getIntent().getStringExtra("category");
        if (category == null) {
            category = "new_lead";
        }

        isToday = getIntent().getBooleanExtra("isToday", true);

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
                toolbar.setTitle("New Leads");
                getLeads("New Lead");
                break;

            case "today_meetings":
                toolbar.setTitle("Todays' Followups");
                getLeadsWithFollowup();
                break;

            case "unanswered":
                toolbar.setTitle("Unanswered");
                getLeads("Unanswered");
                break;

            case "pending":
                toolbar.setTitle("Pending");
                getLeads("Pending");
                break;

            case "interested":
                toolbar.setTitle("Interested");
                getLeads("Interested");
                break;

            case "not_interested":
                toolbar.setTitle("Not Interested");
                getLeads("Not Interested");
                break;

            case "converted":
                toolbar.setTitle("Converted");
                getLeads("Converted");
                break;

            case "all_leads":
                toolbar.setTitle("All Leads");
                getAllLeads();
                break;
        }
    }

    @Override
    public void onValueChanged(float amount, String category) {

    }

    @Override
    public void onItemRemoved(LeadApp item) {

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
                            LeadApp lead = gson.fromJson(jsonElement, LeadApp.class);
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
                LeadApp lead = leadAdapter.itemsFiltered.get(leadAdapter.itemsFiltered.size() - position - 1);
                Intent intent = new Intent(ListActivity.this, LeadDetailsActivity.class);
                intent.putExtra("lead", lead);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        long time = getMidNightTimeStamp();
        if(!isToday)
            time = 0;

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");

        Source CACHE = Source.CACHE;

        dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", category).get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    LeadApp lead = gson.fromJson(jsonElement, LeadApp.class);
                    //document.getReference().getId();
                    Log.v("gdgdgdgd", "" +  document.getReference().getId());

                    lead.setUid(document.getReference().getId());
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
                LeadApp lead = leadAdapter.itemsFiltered.get(leadAdapter.itemsFiltered.size() - position - 1);
                Intent intent = new Intent(ListActivity.this, LeadDetailsActivity.class);
                intent.putExtra("lead", lead);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        long time = getMidNightTimeStamp();
        if(!isToday)
            time = 0;

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");

        Source CACHE = Source.CACHE;

        dataRef.whereGreaterThan("creationDate", time).get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    LeadApp lead = gson.fromJson(jsonElement, LeadApp.class);
                    lead.setUid(document.getId());
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
        recyclerView.setAdapter(followUpAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LeadApp lead = followUpAdapter.itemsFiltered.get(followUpAdapter.itemsFiltered.size() - position - 1);
                Intent intent = new Intent(ListActivity.this, LeadDetailsActivity.class);
                intent.putExtra("lead", lead);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        long time = getMidNightTimeStamp();
        if(!isToday)
            time = 0;

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");

        Source CACHE = Source.CACHE;

        dataRef.whereGreaterThan("latestFollowup", time).whereLessThan("latestFollowup", time + 86400).get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    LeadApp lead = gson.fromJson(jsonElement, LeadApp.class);
                    lead.setUid(document.getId());
                    itemsList.add(lead);
                }
                followUpAdapter.notifyDataSetChanged();
            }
        });
    }

}