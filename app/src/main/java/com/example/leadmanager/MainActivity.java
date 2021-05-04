package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private Spinner spinner;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        spinner = findViewById(R.id.daySpinner);
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
//                R.array.day_array, R.layout.color_spinner_layout);
//             // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//       // spinner.setOnItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FragmentTransaction homeTransaction = getSupportFragmentManager().beginTransaction();
        homeTransaction.replace(R.id.content, new HomeFragment());
        homeTransaction.commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    case R.id.home:
                        transaction.replace(R.id.content, new HomeFragment());
                        break;


                    case R.id.contact:
                        transaction.replace(R.id.content, new ContactFragment());
                        break;


                    case R.id.task:
                        transaction.replace(R.id.content, new TaskFragment());
                        break;


                    case R.id.schedule:
                        transaction.replace(R.id.content, new ScheduleFragment());
                        break;


                }
                transaction.commit();
                return true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}