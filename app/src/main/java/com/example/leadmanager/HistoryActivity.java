package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private SearchView searchView;
    private HistoryAdapter historyAdapter;

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
                historyAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                historyAdapter.getFilter().filter(s);
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
        setContentView(R.layout.activity_history);

        String leadUid = getIntent().getStringExtra("lead_uid");
        String category = getIntent().getStringExtra("category");



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        switch (category) {
            case "history":
                toolbar.setTitle("History");
                break;

            case "notes":
                toolbar.setTitle("Notes");
                break;

            case "deals":
                toolbar.setTitle("Deals");
                break;
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView recyclerView;
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

        Log.v("dipppppp",  leadUid);
        assert leadUid != null;
        db.collection("cache").document(user.getUid()).collection("leads")
                .document(leadUid).get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult()!=null) {

                Log.v("dipppppp", "kkk" + task.getResult().getData());

                ArrayList<HistoryItem> historyItems = (ArrayList<HistoryItem>) task.getResult().get(category);

                if(historyItems!=null) {
                    for(int i = 0; i<historyItems.size(); i++) {
                    Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(historyItems.get(i));
                        HistoryItem historyItem1 = gson.fromJson(jsonElement, HistoryItem.class);
                        //lead.setUid(document.getId());
                        itemsList.add(historyItem1);

                    }
                    //itemsList.add(historyItem1);
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