package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.leadmanager.adapters.AutoCompleteAdapterContact;
import com.example.leadmanager.models.Contact;
import com.example.leadmanager.models.ContactDetails;
import com.example.leadmanager.models.Lead;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;

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
                if(!contactName.getText().toString().equals(""))
                addNewLead(contactGlobal);
                else {
                    Toast.makeText(NewLeadActivity.this, "Please fill the form", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void finish(View view) {
        //some code
        NewLeadActivity.this.finish();
    }

    private void getContacts() {

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("contacts");
        //.collection("purchases");


        Source CACHE = Source.CACHE;
        Query.Direction DESCENDING = Query.Direction.DESCENDING;

        dataRef.get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                for (DocumentSnapshot document : task.getResult().getDocuments()) {

                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(document.get("details"));
                    Contact contact = gson.fromJson(jsonElement, Contact.class);
                    contact.setUid(document.getId());


                    Log.v("dipak", contact.getName() + "");
                    //Log.v("dipak", contact.getName());

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


                    contactList.add(contact);


                }
                //contactAdapter.notifyDataSetChanged();

                contactAdapter = new AutoCompleteAdapterContact(this, contactList);

                contactName.setAdapter(contactAdapter);

                contactName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (!contactName.isPopupShowing()) {
                            //isSupplierAvailable = false;
                            //newSupplierLayout.setVisibility(View.VISIBLE);
                            //addItem.setVisibility(GONE);
                            //Toast.makeText(requireContext(), "No Item Found", Toast.LENGTH_SHORT).show();
                        } else {
                            //newSupplierLayout.setVisibility(GONE);
                            //addItem.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                contactName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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


        contact.setName(contactName.getText().toString());
        contact.setAddress(editAddress.getText().toString());
        contact.setPhone(editNumber.getText().toString());
        contact.setEmail(editEmail.getText().toString());

        progress.setMessage("adding new lead");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        Lead lead = new Lead();
        lead.setStatus(spinnerStatus.getSelectedItem().toString());
        lead.setSource(spinnerSource.getSelectedItem().toString());
        lead.setDescription(editDescription.getText().toString());

        if (contactList.contains(contact)) {
            db.collection("cache").document(user.getUid()).collection("contacts").document(contact.getUid())
                    .collection("leads").add(lead).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    progress.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.dismiss();

                }
            });
        } else {

            ContactDetails contactDetails = new ContactDetails();
            contactDetails.setDetails(contact);

            db.collection("cache").document(user.getUid()).collection("contacts")
                    .add(contactDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // if(newLeadLayout.getVisibility() == View.VISIBLE) {
                    Lead lead = new Lead();
                    lead.setStatus(spinnerStatus.getSelectedItem().toString());
                    lead.setSource(spinnerSource.getSelectedItem().toString());
                    lead.setDescription(editDescription.getText().toString());
                    db.collection("cache").document(user.getUid()).collection("contacts")
                            .document(documentReference.getId()).collection("leads").add(lead).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progress.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.dismiss();

                        }
                    });

                  /*  } else {
                        progress.dismiss();
                    }*/


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
    }
}