package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.leadmanager.adapters.AutoCompleteAdapterContact;
import com.example.leadmanager.adapters.FollowUpAdapter;
import com.example.leadmanager.leads.DescriptionBottomSheet;
import com.example.leadmanager.leads.FollowUpBottomSheet;
import com.example.leadmanager.models.Contact;
import com.example.leadmanager.models.HistoryItem;
import com.example.leadmanager.models.Lead;
import com.example.leadmanager.models.LeadApp;
import com.example.leadmanager.templates.ChooseTemplateBottomSheet;
import com.example.leadmanager.templates.TemplateBottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class LeadDetailsActivity extends AppCompatActivity implements FollowUpBottomSheet.NotifyParent {

    private CardView status, followUp, deals, notes, history;
    private RelativeLayout call, sms, whatsApp, email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private Contact contactGlobal;
    private ProgressDialog progress;
    private FollowUpBottomSheet.NotifyParent context = this;
    private LeadApp lead;

    private TextView statusText, followUpText, dealsText, notesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_details);


        progress = new ProgressDialog(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

        lead = (LeadApp) getIntent().getSerializableExtra("lead");
        getContact(lead.getContactUid());

        statusText = findViewById(R.id.tvStatus);
        followUpText = findViewById(R.id.tvFollowup);
        dealsText = findViewById(R.id.tvDeals);
        notesText = findViewById(R.id.tvNotes);

        statusText.setText(lead.getStatus());
        java.util.Date d = new java.util.Date(lead.getLatestFollowup() * 1000L);
        String itemDateStr = new SimpleDateFormat("dd-MMM-YYYY HH:mm").format(d);
        followUpText.setText(itemDateStr);
        if (lead.getNotes() != null)
            notesText.setText(lead.getNotes()[lead.getNotes().length - 1].getDescription());
        if (lead.getDeals() != null)
            notesText.setText(lead.getDeals()[lead.getDeals().length - 1].getDescription());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        status = findViewById(R.id.statusCard);
        followUp = findViewById(R.id.followUpCard);
        deals = findViewById(R.id.dealsCard);
        notes = findViewById(R.id.notesCard);
        history = findViewById(R.id.historyCard);

        call = findViewById(R.id.call);
        sms = findViewById(R.id.sms);
        whatsApp = findViewById(R.id.whatsApp);
        email = findViewById(R.id.email);

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, StatusActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                startActivity(intent);
            }
        });

        followUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("lead_uid", lead.getUid());
                bundle.putLong("latestFollowUp", lead.getLatestFollowup());
                //Log.v("ifhrgbuergv", lead.getLfd());
                bundle.putString("lfd", lead.getLfd());
                FollowUpBottomSheet followUpBottomSheet = new FollowUpBottomSheet(context);
                followUpBottomSheet.setArguments(bundle);
                followUpBottomSheet.show(getSupportFragmentManager(), DescriptionBottomSheet.TAG);

                //Intent intent = new Intent(LeadDetailsActivity.this, ListActivity.class);
                //intent.putExtra("category", "new_lead");
                //startActivity(intent);
            }
        });

        deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, HistoryActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                intent.putExtra("category", "deals");
                startActivity(intent);
            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, HistoryActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                intent.putExtra("category", "notes");
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, HistoryActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                intent.putExtra("category", "history");
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactGlobal.getPhone()));
                startActivity(intent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseTemplateBottomSheet templateBottomSheet = new ChooseTemplateBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("contact", contactGlobal.getPhone());
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
                bundle.putString("contact", contactGlobal.getPhone());
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
                bundle.putString("contact", contactGlobal.getEmail());
                bundle.putString("category", "email");
                templateBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getSupportFragmentManager(), TemplateBottomSheet.TAG);
            }
        });

    }

    private void getContact(String uid) {

        Log.v("dipakUID", "uid:" + uid);

        DocumentReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("contacts").document(uid);
        //.collection("purchases");


        Source CACHE = Source.CACHE;
        Query.Direction DESCENDING = Query.Direction.DESCENDING;

        dataRef.get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult().exists()) {
                Log.v("dipakUID", "uid:" + task.getResult().getData());
                Gson gson = new Gson();
                JsonElement jsonElement = gson.toJsonTree(task.getResult().getData());
                Contact contact = gson.fromJson(jsonElement, Contact.class);
                contactGlobal = contact;
                Log.v("dipak23", contact.getEmail());
                Log.v("dipak23", contact.getPhone());

            }
        });

    }

    @Override
    public void notifyAdded(String itemDateStr, String lfd) {
        followUpText.setText(itemDateStr);
        lead.setLfd(lfd);
    }
}