package sales_crm.customers.leads.crm.leadmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sales_crm.customers.leads.crm.leadmanager.models.Contact;
import sales_crm.customers.leads.crm.leadmanager.models.LeadApp;
import sales_crm.customers.leads.crm.leadmanager.templates.ChooseTemplateBottomSheet;
import sales_crm.customers.leads.crm.leadmanager.templates.TemplateBottomSheet;

import sales_crm.customers.leads.crm.leadmanager.R;
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

import static sales_crm.customers.leads.crm.leadmanager.Utility.getMidNightTimeStamp;

public class DetailsActivity extends AppCompatActivity implements LeadAdapter.RecyclerViewAdapterListener{

    private RecyclerView recyclerView;
    private LeadAdapter leadAdapter;
    private List<LeadApp> itemsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String category;
    private SearchView searchView;
    private Toolbar toolbar;
    private boolean isToday = true;
    private String contactUid = "";
    private RelativeLayout call, sms, whatsApp, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        call = findViewById(R.id.call);
        sms = findViewById(R.id.sms);
        whatsApp = findViewById(R.id.whatsApp);
        email = findViewById(R.id.email);

        TextView contactName = findViewById(R.id.contactName);
        TextView contactImage = findViewById(R.id.contactImage);

        Contact contact = (Contact) getIntent().getSerializableExtra("contact");

        contactName.setText(contact.getName());
        contactImage.setText(contact.getName().substring(0,1).toUpperCase());

        contactUid = contact.getUid();

        recyclerView = findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        getLeads();

        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                leadAdapter.getFilter().filter(newText);
                return false;
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhone()));
                startActivity(intent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseTemplateBottomSheet templateBottomSheet = new ChooseTemplateBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("contact", contact.getPhone());
                bundle.putString("category", "sms");
                templateBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getSupportFragmentManager(), TemplateBottomSheet.TAG);
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contactGlobal.getPhone(), null)));
            }
        });

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChooseTemplateBottomSheet templateBottomSheet = new ChooseTemplateBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("contact", contact.getPhone());
                bundle.putString("category", "whatsapp");
                templateBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getSupportFragmentManager(), TemplateBottomSheet.TAG);

                /*Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hi *" + contactGlobal.getName() + "*");

                whatsappIntent.putExtra("jid", contactGlobal.getPhone() + "@s.whatsapp.net");
                whatsappIntent.setPackage("com.whatsapp");
                //whatsappIntent.setType("application/pdf");
                try {
                    startActivity(whatsappIntent);
                    //startActivity(Intent.createChooser(whatsappIntent, ""));
                } catch (android.content.ActivityNotFoundException ex) {
                    // ToastHelper.MakeShortText("Whatsapp have not been installed.");
                }*/
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, contactGlobal.getEmail());
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }*/
                ChooseTemplateBottomSheet templateBottomSheet = new ChooseTemplateBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("contact", contact.getEmail());
                bundle.putString("category", "email");
                templateBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getSupportFragmentManager(), TemplateBottomSheet.TAG);
            }
        });
    }

    private void getLeads() {

        leadAdapter = new LeadAdapter(this, itemsList, this, true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(leadAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LeadApp lead = leadAdapter.itemsFiltered.get(leadAdapter.itemsFiltered.size() - position - 1);
                Intent intent = new Intent(DetailsActivity.this, LeadDetailsActivity.class);
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

        dataRef.whereEqualTo("contactUid", contactUid)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    LeadApp lead = gson.fromJson(jsonElement, LeadApp.class);
                    //document.getReference().getId();

                    lead.setUid(document.getReference().getId());
                    itemsList.add(lead);
                }
                leadAdapter.notifyDataSetChanged();
            }
        });
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

    @Override
    public void hideToolBar(boolean hide) {

    }
}