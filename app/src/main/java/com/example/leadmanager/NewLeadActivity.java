package com.example.leadmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.leadmanager.adapters.AutoCompleteAdapterContact;
import com.example.leadmanager.models.Contact;

import java.util.List;

import static android.view.View.GONE;

public class NewLeadActivity extends AppCompatActivity {

    private EditText editName, editAddress, editEmail, editNumber, editDescription;
    private Spinner spinnerStatus, spinnerSource;
    private Button submitButton;
    private AutoCompleteTextView contactName;
    private List<Contact> contactList;
    private AutoCompleteAdapterContact contactAdapter;
    private Contact availableContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lead);

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
                //newSupplierLayout.setVisibility(GONE);
                //addItem.setVisibility(View.VISIBLE);
            }
        });

        submitButton = findViewById(R.id.submitButton);
    }

    public void finish(View view) {
        //some code
        NewLeadActivity.this.finish();
    }
}