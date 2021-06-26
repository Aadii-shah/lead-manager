package sales_crm.customers.leads.crm.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import sales_crm.customers.leads.crm.leadmanager.billing.InAppPurchase;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;
import sales_crm.customers.leads.crm.leadmanager.models.ContactDetails;
import sales_crm.customers.leads.crm.leadmanager.models.HistoryItem;
import sales_crm.customers.leads.crm.leadmanager.models.Lead;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.models.Pro;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class AddNewContactActivity extends AppCompatActivity {

    private EditText editName, editAddress, editEmail, editNumber, editDescription;
    private Spinner spinnerStatus, spinnerSource;
    private LinearLayout newLeadLayout;
    private ProgressDialog progress;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("");

        CountryCodePicker ccp = findViewById(R.id.ccp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progress = new ProgressDialog(this);


        newLeadLayout = findViewById(R.id.newLeadLayout);
        ImageView newLeadText = findViewById(R.id.newLeadText);
        newLeadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newLeadLayout.getVisibility() == View.VISIBLE) {
                    newLeadLayout.setVisibility(View.GONE);
                    newLeadText.setImageResource(R.drawable.ic_add_contact);

                } else {
                    newLeadLayout.setVisibility(View.VISIBLE);
                    newLeadText.setImageResource(R.drawable.ic_up_arrow);


                }
            }
        });

        //spinner for status
        spinnerStatus = findViewById(R.id.spinnerStatus);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(AddNewContactActivity.this,
                R.array.status, R.layout.status_color_spinner);
        adapter.setDropDownViewResource(R.layout.status_dropdown_layout);
        spinnerStatus.setAdapter(adapter);

        //spinner for leadSource
        spinnerSource = findViewById(R.id.spinnerLeadSource);
        ArrayAdapter adapterSource = ArrayAdapter.createFromResource(AddNewContactActivity.this,
                R.array.lead_source, R.layout.status_color_spinner);
        adapterSource.setDropDownViewResource(R.layout.status_dropdown_layout);
        spinnerSource.setAdapter(adapterSource);


        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editEmail = findViewById(R.id.editEmail);
        editNumber = findViewById(R.id.editNumber);
        editDescription = findViewById(R.id.editDescription);

        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                db.collection("cache")
                        .document(user.getUid()).collection("account").document("pro")
                        .get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //long time = (long) documentSnapshot.get("validTill");
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());

                        Pro pro = gson.fromJson(jsonElement, Pro.class);
                        int count = pro.getCount();

                        if (count < 100) {

                            db.collection("cache")
                                    .document(user.getUid()).collection("account").document("pro")
                                    .update("count", count + 1);

                            String name = editName.getText().toString();
                            String email = editEmail.getText().toString();
                            String number = editNumber.getText().toString();


                            if (!name.equals("") && (!email.equals("") || !number.equals(""))) {
                                progress.setMessage("adding new contact");
                                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                                progress.show();

                                Contact contact = new Contact();
                                contact.setName(name);
                                contact.setAddress(editAddress.getText().toString());
                                contact.setEmail(email);
                                contact.setPhone(ccp.getSelectedCountryCode() + number);

                                ContactDetails contactDetails = new ContactDetails();
                                contactDetails.setDetails(contact);

                                DocumentReference documentReference = db.collection("cache").document(user.getUid()).collection("contacts")
                                        .document();
                                documentReference.set(contact);
                                if (newLeadLayout.getVisibility() == View.VISIBLE) {
                                    Lead lead = new Lead();
                                    lead.setStatus(spinnerStatus.getSelectedItem().toString());
                                    lead.setSource(spinnerSource.getSelectedItem().toString());
                                    lead.setDescription(editDescription.getText().toString());
                                    lead.setContactUid(documentReference.getId());
                                    documentReference = db.collection("cache").document(user.getUid()).collection("leads")
                                            //.document(documentReference.getId()).collection("leads")
                                            .document();

                                    documentReference.set(lead);

                                    HistoryItem historyItem = new HistoryItem();
                                    historyItem.setDescription("Created");
                                    historyItem.setDate(Utility.getCurrentTime());

                                    db.collection("cache").document(user.getUid())
                                            //.collection("contacts").document(contact.getUid())
                                            .collection("leads")
                                            .document(documentReference.getId())
                                            .update("history", FieldValue.arrayUnion(historyItem));

                                    progress.dismiss();
                                    finish();

                                } else {
                                    progress.dismiss();
                                    finish();
                                }

                            }

                        } else if (pro.getValidTill() > (System.currentTimeMillis() / 1000)) {

                            String name = editName.getText().toString();
                            String email = editEmail.getText().toString();
                            String number = editNumber.getText().toString();


                            if (!name.equals("") && (!email.equals("") || !number.equals(""))) {
                                progress.setMessage("adding new contact");
                                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                                progress.show();

                                Contact contact = new Contact();
                                contact.setName(name);
                                contact.setAddress(editAddress.getText().toString());
                                contact.setEmail(email);
                                contact.setPhone(ccp.getSelectedCountryCode() + number);

                                ContactDetails contactDetails = new ContactDetails();
                                contactDetails.setDetails(contact);

                                DocumentReference documentReference = db.collection("cache").document(user.getUid()).collection("contacts")
                                        .document();
                                documentReference.set(contact);
                                if (newLeadLayout.getVisibility() == View.VISIBLE) {
                                    Lead lead = new Lead();
                                    lead.setStatus(spinnerStatus.getSelectedItem().toString());
                                    lead.setSource(spinnerSource.getSelectedItem().toString());
                                    lead.setDescription(editDescription.getText().toString());
                                    lead.setContactUid(documentReference.getId());
                                    documentReference = db.collection("cache").document(user.getUid()).collection("leads")
                                            //.document(documentReference.getId()).collection("leads")
                                            .document();

                                    documentReference.set(lead);

                                    HistoryItem historyItem = new HistoryItem();
                                    historyItem.setDescription("Created");
                                    historyItem.setDate(Utility.getCurrentTime());

                                    db.collection("cache").document(user.getUid())
                                            //.collection("contacts").document(contact.getUid())
                                            .collection("leads")
                                            .document(documentReference.getId())
                                            .update("history", FieldValue.arrayUnion(historyItem));

                                    progress.dismiss();
                                    finish();

                                } else {
                                    progress.dismiss();
                                    finish();
                                }

                            }

                        } else {

                            //Subscription message dialog

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (!isFinishing()) {
                                        new AlertDialog.Builder(AddNewContactActivity.this)
                                                .setTitle("Get Premium")
                                                .setMessage("You have reached free 100 contact limit.")
                                                .setCancelable(false)
                                                .setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent i = new Intent(AddNewContactActivity.this, InAppPurchase.class);
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

                        String name = editName.getText().toString();
                        String email = editEmail.getText().toString();
                        String number = editNumber.getText().toString();


                        if (!name.equals("") && (!email.equals("") || !number.equals(""))) {
                            progress.setMessage("adding new contact");
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();

                            Contact contact = new Contact();
                            contact.setName(name);
                            contact.setAddress(editAddress.getText().toString());
                            contact.setEmail(email);
                            contact.setPhone(ccp.getSelectedCountryCode() + number);

                            ContactDetails contactDetails = new ContactDetails();
                            contactDetails.setDetails(contact);

                            DocumentReference documentReference = db.collection("cache").document(user.getUid()).collection("contacts")
                                    .document();
                            documentReference.set(contact);
                            if (newLeadLayout.getVisibility() == View.VISIBLE) {
                                Lead lead = new Lead();
                                lead.setStatus(spinnerStatus.getSelectedItem().toString());
                                lead.setSource(spinnerSource.getSelectedItem().toString());
                                lead.setDescription(editDescription.getText().toString());
                                lead.setContactUid(documentReference.getId());
                                documentReference = db.collection("cache").document(user.getUid()).collection("leads")
                                        //.document(documentReference.getId()).collection("leads")
                                        .document();

                                documentReference.set(lead);

                                HistoryItem historyItem = new HistoryItem();
                                historyItem.setDescription("Created");
                                historyItem.setDate(Utility.getCurrentTime());

                                db.collection("cache").document(user.getUid())
                                        //.collection("contacts").document(contact.getUid())
                                        .collection("leads")
                                        .document(documentReference.getId())
                                        .update("history", FieldValue.arrayUnion(historyItem));

                                progress.dismiss();
                                finish();

                            } else {
                                progress.dismiss();
                                finish();
                            }

                        }

                    }
                });




                    /*db.collection("cache").document(user.getUid()).collection("contacts")
                            .add(contact).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if(newLeadLayout.getVisibility() == View.VISIBLE) {
                                Lead lead = new Lead();
                                lead.setStatus(spinnerStatus.getSelectedItem().toString());
                                lead.setSource(spinnerSource.getSelectedItem().toString());
                                lead.setDescription(editDescription.getText().toString());
                                lead.setContactUid(documentReference.getId());
                                db.collection("cache").document(user.getUid()).collection("leads")
                                        //.document(documentReference.getId()).collection("leads")
                                        .add(lead).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

                                    }
                                });

                            } else {
                                progress.dismiss();
                                finish();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });*/


            }

        });
    }
}