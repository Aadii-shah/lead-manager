package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.leadmanager.models.Contact;
import com.example.leadmanager.models.ContactDetails;
import com.example.leadmanager.models.Lead;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNewContactActivity extends AppCompatActivity {

    private EditText editName, editAddress, editEmail, editNumber, editDescription;
    private Spinner spinnerStatus, spinnerSource;
    private LinearLayout newLeadLayout;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        progress = new ProgressDialog(this);


        newLeadLayout = findViewById(R.id.newLeadLayout);
        TextView newLeadText = findViewById(R.id.newLeadText);
        newLeadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newLeadLayout.getVisibility() == View.VISIBLE) {
                    newLeadLayout.setVisibility(View.GONE);
                } else {
                    newLeadLayout.setVisibility(View.VISIBLE);
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




                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String number = editNumber.getText().toString();




                if(!name.equals("") && (!email.equals("") || !number.equals(""))) {
                    progress.setMessage("adding new contact");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();

                    Contact contact = new Contact();
                    contact.setName(name);
                    contact.setAddress(editAddress.getText().toString());
                    contact.setEmail(email);
                    contact.setPhone(number);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    ContactDetails contactDetails = new ContactDetails();
                    contactDetails.setDetails(contact);

                    db.collection("cache").document(user.getUid()).collection("contacts")
                            .add(contactDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if(newLeadLayout.getVisibility() == View.VISIBLE) {
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

                                    }
                                });

                            } else {
                                progress.dismiss();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }
            }
        });
    }
}