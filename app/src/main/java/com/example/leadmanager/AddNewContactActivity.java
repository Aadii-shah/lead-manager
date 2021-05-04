package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AddNewContactActivity extends AppCompatActivity {

    private EditText editName, editAddress, editEmail, editNumber, editDescription;
    private Spinner spinnerStatus, spinnerSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

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
    }
}