package sales_crm.customers.leads.crm.leadmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import sales_crm.customers.leads.crm.leadmanager.billing.InAppPurchase;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;
import sales_crm.customers.leads.crm.leadmanager.models.ContactDetails;
import sales_crm.customers.leads.crm.leadmanager.models.HistoryItem;
import sales_crm.customers.leads.crm.leadmanager.models.Lead;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.models.Pro;
import sales_crm.customers.leads.crm.leadmanager.models.TemplateApp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static sales_crm.customers.leads.crm.leadmanager.Utility.getCurrentTime;

public class NewLeadActivity extends AppCompatActivity {

    private EditText editName, editAddress, editEmail, editNumber, editDescription;
    private Spinner spinnerStatus, spinnerSource;
    private Button submitButton;
    private AutoCompleteTextView contactName;
    private List<Contact> contactList;
    private AutoCompleteAdapterContact contactAdapter;
    private Contact availableContact;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private ProgressDialog progress;
    private Contact contactGlobal;
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lead);

        progress = new ProgressDialog(this);
        contactGlobal = new Contact();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            //signIn();
        } else {
            //syncData();
        }

        TextView importCSV = findViewById(R.id.importCSV);
        importCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewLeadActivity.this, ImportCSV.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("");

        ccp = findViewById(R.id.ccp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //spinner for status
        spinnerStatus = findViewById(R.id.spinnerStatus);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(NewLeadActivity.this,
                R.array.status, R.layout.status_color_spinner);
        adapter.setDropDownViewResource(R.layout.status_dropdown_layout);
        spinnerStatus.setAdapter(adapter);

        //spinner for leadSource
        spinnerSource = findViewById(R.id.spinnerLeadSource);
        ArrayAdapter adapterSource = ArrayAdapter.createFromResource(NewLeadActivity.this,
                R.array.lead_source, R.layout.status_color_spinner);
        adapterSource.setDropDownViewResource(R.layout.status_dropdown_layout);
        spinnerSource.setAdapter(adapterSource);


        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editEmail = findViewById(R.id.editEmail);
        editNumber = findViewById(R.id.editNumber);
        editDescription = findViewById(R.id.editDescription);

        contactName = findViewById(R.id.contactName);
        contactList = new ArrayList<>();
        getContacts();

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contactName.getText().toString().equals("") && !editDescription.getText().toString().equals("")) {

                    db.collection("cache")
                            .document(user.getUid()).collection("account").document("pro")
                            .get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());

                            Pro pro = gson.fromJson(jsonElement, Pro.class);
                            int count = pro.getCount();


                            if(count <= 100) {
                                db.collection("cache")
                                        .document(user.getUid()).collection("account").document("pro")
                                        .update("count", count +1);
                                addNewLead(contactGlobal);
                            } else if(pro.getValidTill()>(System.currentTimeMillis()/1000)) {
                                addNewLead(contactGlobal);
                            } else {
                                //Subscription Message
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!isFinishing()){
                                            new AlertDialog.Builder(NewLeadActivity.this)
                                                    .setTitle("Get Premium")
                                                    .setMessage("You have reached free 100 contact limit.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent i = new Intent(NewLeadActivity.this, InAppPurchase.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    //finish();
                                                }
                                            }).show();
                                        }
                                    }
                                });
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("dipak", "failed" + e.toString());

                            Pro pro = new Pro();
                            pro.setCount(1);
                            pro.setValidTill(0);

                                db.collection("cache")
                                        .document(user.getUid()).collection("account").document("pro")
                                        .set(pro);
                                addNewLead(contactGlobal);


                        }
                    });

                } else {
                    Toast.makeText(NewLeadActivity.this, "Please fill the description and contact form", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getContacts() {

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("contacts");


        Source CACHE = Source.CACHE;

        dataRef.orderBy("name", Query.Direction.DESCENDING)
                .get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.getData());
                    Contact contact = gson.fromJson(jsonElement, Contact.class);
                    contact.setUid(document.getId());
                    contactList.add(contact);


                }
                //contactAdapter.notifyDataSetChanged();

                contactAdapter = new AutoCompleteAdapterContact(this, contactList);

                contactName.setAdapter(contactAdapter);


                contactName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        //Log.v("hsgftsfts", "tsds");
                        //isSupplierAvailable = true;
                        availableContact = contactAdapter.getItem(i);
                        editAddress.setText(availableContact.getAddress());
                        editEmail.setText(availableContact.getEmail());
                        editNumber.setText(availableContact.getPhone());
                        contactGlobal = availableContact;
                      /*  editAddress.setEnabled(false);
                        editEmail.setEnabled(false);
                        editNumber.setEnabled(false);*/
                        //newSupplierLayout.setVisibility(GONE);

                        //addItem.setVisibility(View.VISIBLE);
                    }
                });

                //suppliersAdapter.notifyDataSetChanged();
            }

        });

    }

    public void addNewLead(Contact contact) {

        progress.setMessage("adding new lead");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();


        contact.setName(contactName.getText().toString());
        contact.setAddress(editAddress.getText().toString());
        contact.setPhone(ccp.getSelectedCountryCode() + editNumber.getText().toString());
        contact.setEmail(editEmail.getText().toString());

        if (contactList.contains(contact)) {

            Lead lead = new Lead();
            lead.setStatus(spinnerStatus.getSelectedItem().toString());
            lead.setSource(spinnerSource.getSelectedItem().toString());
            lead.setDescription(editDescription.getText().toString());
            lead.setCreationDate(getCurrentTime());
            lead.setContactUid(contact.getUid());

            DocumentReference documentReference = db.collection("cache").document(user.getUid())
                    //.collection("contacts").document(contact.getUid())
                    .collection("leads").document();

            documentReference.set(lead);


            HistoryItem historyItem = new HistoryItem();
            historyItem.setDescription("Created");
            historyItem.setDate(getCurrentTime());

            db.collection("cache").document(user.getUid())
                    //.collection("contacts").document(contact.getUid())
                    .collection("leads")
                    .document(documentReference.getId())
                    .update("history", FieldValue.arrayUnion(historyItem));

            progress.dismiss();
            finish();


            /*db.collection("cache").document(user.getUid())
                    //.collection("contacts").document(contact.getUid())
                    .collection("leads").add(lead).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                    HistoryItem historyItem = new HistoryItem();
                    historyItem.setDescription("Created");
                    historyItem.setDate(getCurrentTime());

                    db.collection("cache").document(user.getUid())
                            //.collection("contacts").document(contact.getUid())
                            .collection("leads")
                            .document(documentReference.getId())
                            .update("history", FieldValue.arrayUnion(historyItem)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progress.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.dismiss();
                            finish();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.dismiss();
                    finish();

                }
            });*/
        } else {

            ContactDetails contactDetails = new ContactDetails();
            contactDetails.setDetails(contact);

            DocumentReference documentSnapshot = db.collection("cache").document(user.getUid()).collection("contacts").document();
            documentSnapshot.set(contact);
            //.set(contact);

            Lead lead = new Lead();
            lead.setStatus(spinnerStatus.getSelectedItem().toString());
            lead.setSource(spinnerSource.getSelectedItem().toString());
            lead.setDescription(editDescription.getText().toString());
            lead.setCreationDate(getCurrentTime());
            lead.setContactUid(documentSnapshot.getId());

            documentSnapshot = db.collection("cache").document(user.getUid())
                    //.collection("contacts").document(documentReference.getId())
                    .collection("leads").document();

            documentSnapshot.set(lead);


            HistoryItem historyItem = new HistoryItem();
            historyItem.setDescription("Created");
            historyItem.setDate(Utility.getCurrentTime());

            db.collection("cache").document(user.getUid())
                    //.collection("contacts").document(contact.getUid())
                    .collection("leads")
                    .document(documentSnapshot.getId())
                    .update("history", FieldValue.arrayUnion(historyItem));

            progress.dismiss();
            finish();


            /*db.collection("cache").document(user.getUid()).collection("contacts")
                    .add(contact).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // if(newLeadLayout.getVisibility() == View.VISIBLE) {
                    Lead lead = new Lead();
                    lead.setStatus(spinnerStatus.getSelectedItem().toString());
                    lead.setSource(spinnerSource.getSelectedItem().toString());
                    lead.setDescription(editDescription.getText().toString());
                    lead.setCreationDate(getCurrentTime());
                    lead.setContactUid(documentReference.getId());


                    db.collection("cache").document(user.getUid())
                            //.collection("contacts").document(documentReference.getId())
                            .collection("leads").add(lead)

                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {



                            HistoryItem historyItem = new HistoryItem();
                            historyItem.setDescription("Created");
                            historyItem.setDate(Utility.getCurrentTime());

                            db.collection("cache").document(user.getUid())
                                    //.collection("contacts").document(contact.getUid())
                                    .collection("leads")
                                    .document(documentReference.getId())
                                    .update("history", FieldValue.arrayUnion(historyItem)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progress.dismiss();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progress.dismiss();
                                    finish();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.dismiss();
                            finish();

                        }
                    });

                  /*  } else {
                        progress.dismiss();
                    }*/


                /*}
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });*/

        }
    }
}