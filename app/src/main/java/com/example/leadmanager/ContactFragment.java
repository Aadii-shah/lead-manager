package com.example.leadmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactFragment extends Fragment {

    FloatingActionButton addContact, importContact, addFab;
    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        addContact = view.findViewById(R.id.addContact);
        importContact = view.findViewById(R.id.importContact);
        addFab = view.findViewById(R.id.addFab);

        //addContact.setVisibility(View.GONE);
        //importContact.setVisibility(View.GONE);

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false;


        ExtendedFloatingActionButton floatingActionButton = view.findViewById(R.id.extended_fab_contact);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewContactActivity.class);
                startActivity(intent);
            }
        });


        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!isAllFabsVisible) {

                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs VISIBLE.
                    addContact.show();
                    importContact.show();
                    // addAlarmActionText.setVisibility(View.VISIBLE);
                    // addPersonActionText.setVisibility(View.VISIBLE);

                    // make the boolean variable true as
                    // we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = true;
                    addFab.setImageResource(R.drawable.ic_notinterested);
                }
                else {
                    addContact.hide();
                    importContact.hide();
                    isAllFabsVisible = false;
                    addFab.setImageResource(R.drawable.ic_add);
                }*/
                Intent intent = new Intent(getActivity(), AddNewContactActivity.class);
                startActivity(intent);
            }
        });
        // below is the sample action to handle add person
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        addContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), AddNewContactActivity.class);
                        startActivity(intent);
                    }
                });

        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        importContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), DealsActivity.class);
                        startActivity(intent);
                    }
                });
        return view;
    }
}