package com.example.leadmanager;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.leadmanager.adapters.HistoryAdapter;
import com.example.leadmanager.adapters.RecyclerViewTouchListener;
import com.example.leadmanager.adapters.TemplatesAdapter;
import com.example.leadmanager.leads.DescriptionBottomSheet;
import com.example.leadmanager.models.HistoryItem;
import com.example.leadmanager.models.Template;
import com.example.leadmanager.templates.TemplateBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends Fragment implements TemplateBottomSheet.NotifyParent, TemplatesAdapter.RecyclerViewAdapterListener {

    private RecyclerView recyclerView;
    private TemplatesAdapter templatesAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SearchView searchView;
    List<Template> itemsList;

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance(String param1, String param2) {
        return new TaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar( toolbar );

        TemplateBottomSheet templateBottomSheet = new TemplateBottomSheet(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        templatesAdapter = new TemplatesAdapter(getContext(), itemsList, this, "task");
        getTemplates();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bundle bundle = new Bundle();
                //bundle.putString("lead_uid", leadUid);
                //bundle.putString("category", category);

                //descriptionBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getChildFragmentManager(), TemplateBottomSheet.TAG);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.v("dippppp", "entered");
        inflater.inflate(R.menu.toolbar_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        //searchView.startAnimation(fab_close);
        //int searchBarId = searchView.getId();//getContext().getResources().getIdentifier("android:id/search_bar",null,null);
        //LinearLayout searchBar = (SearchView) searchView.findViewById(searchBarId);
        searchView.setLayoutTransition(new LayoutTransition());
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                templatesAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                templatesAdapter.getFilter().filter(s);
                return false;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        //super.onCreateOptionsMenu(menu,inflater);

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
    public void notifyAdded() {
        getTemplates();
    }

    private void getTemplates() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(templatesAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Source CACHE = Source.CACHE;

        db.collection("cache").document(user.getUid()).collection("templates")
                .get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult()!=null) {
                itemsList.clear();
                //Log.v("dipppppp", "kkk" + task.getResult().getData());

                //ArrayList<HistoryItem> historyItems = (ArrayList<HistoryItem>) task.getResult().get(category);


                //if(historyItems!=null) {
                    for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());
                        Template template = gson.fromJson(jsonElement, Template.class);
                        //lead.setUid(document.getId());
                        itemsList.add(template);

                    }
                    //itemsList.add(historyItem1);
                    templatesAdapter.notifyDataSetChanged();
                //}
            }
        });
    }

    @Override
    public void onValueChanged(float amount, String category) {

    }

    @Override
    public void onItemRemoved(Template item) {

    }

    @Override
    public void onItemUpdated(float amount, int count) {

    }

    @Override
    public void onSendClicked(int position) {

    }

    @Override
    public void onSendSmsClicked(int position) {

    }

    @Override
    public void onSendWhatsAppClicked(int position) {

    }

    @Override
    public void onSendEmailClicked(int position) {

    }

}