package sales_crm.customers.leads.crm.leadmanager;

import android.animation.LayoutTransition;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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

import sales_crm.customers.leads.crm.leadmanager.models.Contact;

import sales_crm.customers.leads.crm.leadmanager.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.firebase.firestore.Source.CACHE;

public class ContactFragment extends Fragment implements ContactsAdapter.RecyclerViewAdapterListener {

    FloatingActionButton addContact, importContact, addFab;
    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;
    private RecyclerView recyclerView;
    private ContactsAdapter contactsAdapter;
    private List<Contact> itemsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private Toolbar toolbar;

    private SearchView searchView;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        itemsList.clear();
        getContacts();

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar( toolbar );

        user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.contactsRecyclerView);
        itemsList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(requireContext(), itemsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(contactsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(requireContext(), recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                /*Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("contact", contactsAdapter.itemsFiltered.get(contactsAdapter.itemsFiltered.size() - 1 - position));
                startActivity(intent);*/
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        addContact = view.findViewById(R.id.addContact);
        importContact = view.findViewById(R.id.importContact);
        addFab = view.findViewById(R.id.addFab);

        //addContact.setVisibility(View.GONE);
        //importContact.setVisibility(View.GONE);

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false;


        ExtendedFloatingActionButton floatingActionButton = view.findViewById(R.id.extended_fab_contact);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewContactActivity.class);
                startActivity(intent);
            }
        });


        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!isAllFabsVisible) {

                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs VISIBLE.
                    addContact.show();
                    importContact.show();
                    // addAlarmActionText.setVisibility(View.VISIBLE);
                    // addPersonActionText.setVisibility(View.VISIBLE);

                    // make the boolean variable true as
                    // we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = true;
                    addFab.setImageResource(R.drawable.ic_notinterested);
                }
                else {
                    addContact.hide();
                    importContact.hide();
                    isAllFabsVisible = false;
                    addFab.setImageResource(R.drawable.ic_add);
                }*/
                Intent intent = new Intent(getActivity(), AddNewContactActivity.class);
                startActivity(intent);
            }
        });
        // below is the sample action to handle add person
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        addContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), AddNewContactActivity.class);
                        startActivity(intent);
                    }
                });

        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        importContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ImportCSV.class);
                        startActivity(intent);
                    }
                });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

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
                contactsAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                contactsAdapter.getFilter().filter(s);
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
    public void onValueChanged(float amount, String category) {

    }

    @Override
    public void onItemRemoved(Contact item) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("contact", item);
        startActivity(intent);
    }

    @Override
    public void onItemUpdated(float amount, int count) {

    }

    @Override
    public void hideToolBar(boolean hide) {
        if(hide)
            toolbar.setVisibility(View.GONE);
        else toolbar.setVisibility(View.VISIBLE);
    }

    private void getContacts() {

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("contacts");
        //.collection("purchases");

        Query.Direction DESCENDING = Query.Direction.DESCENDING;

        dataRef.orderBy("name", Query.Direction.DESCENDING).get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    Contact contact = gson.fromJson(jsonElement, Contact.class);
                    contact.setUid(document.getReference().getId());

                    ////Log.v("dipak", contact.getName());

                    //Contact contact = Objects.requireNonNull(document.toObject(Contact.class));

                    /*String number = decrypt(supplier.getSupplier_contact(), originalKey);
                    String email = decrypt(supplier.getSupplier_email(), originalKey);
                    if ((number != null && !number.equals("NA")) || (email != null && !email.equals("NA"))) {
                        supplier.setSupplier_name(decrypt(supplier.getSupplier_name(), originalKey));
                        supplier.setSupplier_address(decrypt(supplier.getSupplier_address(), originalKey));
                        supplier.setSupplier_contact(number);
                        supplier.setSupplier_email(email);
                        if(!supplierList.contains(supplier))
                            supplierList.add(supplier);
                    }*/


                    itemsList.add(contact);


                }
                contactsAdapter.notifyDataSetChanged();

            }

        });

    }

}