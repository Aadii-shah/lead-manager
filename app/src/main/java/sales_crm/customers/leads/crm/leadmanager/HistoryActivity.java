package sales_crm.customers.leads.crm.leadmanager;

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

import sales_crm.customers.leads.crm.leadmanager.leads.DescriptionBottomSheet;
import sales_crm.customers.leads.crm.leadmanager.models.HistoryItem;

import sales_crm.customers.leads.crm.leadmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.RecyclerViewAdapterListener, DescriptionBottomSheet.NotifyParent {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SearchView searchView;
    private HistoryAdapter historyAdapter;
    private String leadUid, category;
    List<HistoryItem> itemsList;

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



        itemsList = new ArrayList<>();

        leadUid = getIntent().getStringExtra("lead_uid");
        category = getIntent().getStringExtra("category");



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabHome = findViewById(R.id.fabHome);
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(home);
                finish();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);

        switch (category) {
            case "history":
                fab.setVisibility(View.GONE);
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


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("lead_uid", leadUid);
                bundle.putString("category", category);
                DescriptionBottomSheet descriptionBottomSheet = new DescriptionBottomSheet();
                descriptionBottomSheet.setArguments(bundle);
                descriptionBottomSheet.show(getSupportFragmentManager(), DescriptionBottomSheet.TAG);
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

    @Override
    protected void onResume() {
        RecyclerView recyclerView;
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

        //Log.v("dipppppp",  leadUid);
        assert leadUid != null;
        db.collection("cache").document(user.getUid()).collection("leads")
                .document(leadUid).get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult()!=null) {

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
        super.onResume();
    }

    @Override
    public void notifyAdded() {

        //Log.v("jhgfff", "called1");

        itemsList.clear();
        Source CACHE = Source.CACHE;

        assert leadUid != null;
        db.collection("cache").document(user.getUid()).collection("leads")
                .document(leadUid).get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult()!=null) {



                ArrayList<HistoryItem> historyItems = (ArrayList<HistoryItem>) task.getResult().get(category);

                //Log.v("dipppppjjjp", "kkk" + task.getResult().get("history"));

                if(historyItems!=null) {
                    for(int i = 0; i<historyItems.size(); i++) {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(historyItems.get(i));
                        HistoryItem historyItem1 = gson.fromJson(jsonElement, HistoryItem.class);
                        //Log.v("sgfgdsfgyhj", historyItem1 + "");
                        //lead.setUid(document.getId());
                        itemsList.add(historyItem1);

                    }
                    //itemsList.add(historyItem1);
                    historyAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}