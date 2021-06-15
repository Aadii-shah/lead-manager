package com.example.leadmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.leadmanager.models.HistoryItem;
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

    private RadioButton radio_newLead, radio_interested, radio_notInterested, radio_unanswered, radio_busy, radio_converted;

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


        /*radio_newLead = findViewById(R.id.radio_newLead);
        radio_interested = findViewById(R.id.radio_interested);
        radio_notInterested = findViewById(R.id.radio_notInterested);
        radio_busy = findViewById(R.id.radio_busy);
        radio_unanswered = findViewById(R.id.radio_unanswered);
        radio_converted = findViewById(R.id.radio_converted);

        //retrieving data from SP
        radio_newLead.setChecked(Update("radio_newLead"));
        radio_interested.setChecked(Update("radio_interested"));
        radio_notInterested.setChecked(Update("radio_notInterested"));
        radio_busy.setChecked(Update("radio_busy"));
        radio_unanswered.setChecked(Update("radio_unanswered"));
        radio_converted.setChecked(Update("radio_converted"));*/


        radio_newLead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean newLead_isChecked) {
                changeStatus(leadUid, "New Lead");
                radioSharedPrefs("radio_newLead", newLead_isChecked);

            }
        });

        radio_interested.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean interested_isChecked) {
                changeStatus(leadUid, "Interested");

                radioSharedPrefs("radio_interested", interested_isChecked);

            }
        });

        radio_notInterested.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean notinterested_isChecked) {
                changeStatus(leadUid, "Not Interested");

                radioSharedPrefs("radio_notInterested", notinterested_isChecked);

            }
        });

        radio_busy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean busy_isChecked) {
                changeStatus(leadUid, "Pending");

                radioSharedPrefs("radio_busy", busy_isChecked);

            }
        });

        radio_unanswered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean unAnswered_isChecked) {

                changeStatus(leadUid, "Unanswered");

                radioSharedPrefs("radio_unanswered", unAnswered_isChecked);

            }
        });

        radio_converted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean converted_isChecked) {

                changeStatus(leadUid, "Converted");

                radioSharedPrefs("radio_converted", converted_isChecked);

            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        statusesLayout = findViewById(R.id.statusesLayout);

       /* statusesLayout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
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
                changeStatus(leadUid, "Pending");
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
        });*/
    }


    private void radioSharedPrefs(String key, boolean value) {
        SharedPreferences sp = getSharedPreferences("status", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean Update(String key) {
        SharedPreferences sp = getSharedPreferences("status", MODE_PRIVATE);
        return sp.getBoolean(key, false);

    }


    private void changeStatus(String leadUid, String status) {
        db.collection("cache").document(user.getUid()).collection("leads")
                .document(leadUid)
                .update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HistoryItem historyItem = new HistoryItem();
                        historyItem.setDescription("Status changed to: " + status);
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