package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.leadmanager.models.HistoryItem;
import com.example.leadmanager.models.LeadApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatusActivity extends AppCompatActivity {
    LinearLayout statusesLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String leadUid = getIntent().getStringExtra("lead_uid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        statusesLayout = findViewById(R.id.statusesLayout);

        statusesLayout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(leadUid, "New Lead");
            }
        });

        statusesLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(leadUid, "Interested");
            }
        });

        statusesLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(leadUid, "Unanswered");
            }
        });

        statusesLayout.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(leadUid, "Busy");
            }
        });

        statusesLayout.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(leadUid, "Not Interested");
            }
        });

        statusesLayout.getChildAt(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(leadUid, "Converted");
            }
        });
    }

    private void changeStatus(String leadUid, String status) {
        db.collection("cache").document(user.getUid()).collection("leads")
                .document(leadUid)
                .update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HistoryItem historyItem = new HistoryItem();
                        historyItem.setDescription("Status changed to: " +  status);
                        historyItem.setDate(Utility.getCurrentTime());

                        db.collection("cache").document(user.getUid())
                                //.collection("contacts").document(contact.getUid())
                                .collection("leads")
                                .document(leadUid)
                                .update("history", FieldValue.arrayUnion(historyItem)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //TODO Update UI
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}