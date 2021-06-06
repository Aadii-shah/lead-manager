package com.example.leadmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    private CardView newLead, todayMeeting, unAnswered, busy, allLead, converted, notInterested, interested;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Spinner spinner = view.findViewById(R.id.daySpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.day_array, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        newLead = view.findViewById(R.id.newLead);
        todayMeeting = view.findViewById(R.id.todayMeeting);
        unAnswered = view.findViewById(R.id.unAnswered);
        busy = view.findViewById(R.id.busy);
        allLead = view.findViewById(R.id.allLead);
        converted = view.findViewById(R.id.converted);
        notInterested = view.findViewById(R.id.notInterested);
        interested = view.findViewById(R.id.interested);
        // spinner.setOnItemSelectedListener(this);

        newLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "new_lead");
                startActivity(intent);
            }
        });

        todayMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "today_meetings");
                startActivity(intent);
            }
        });

        unAnswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "unanswered");
                startActivity(intent);
            }
        });

        busy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "busy");
                startActivity(intent);
            }
        });

        newLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "new_lead");
                startActivity(intent);
            }
        });

        allLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "all_leads");
                startActivity(intent);
            }
        });

        converted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "converted");
                startActivity(intent);
            }
        });

        notInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "not_interested");
                startActivity(intent);
            }
        });

        interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "interested");
                startActivity(intent);
            }
        });

        return view;

    }
}