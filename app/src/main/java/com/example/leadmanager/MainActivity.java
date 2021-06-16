package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private Spinner spinner;
    private BottomNavigationView bottomNavigationView;
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore.getInstance().setFirestoreSettings(settings);;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            signIn();
        } else {
          //  syncData();
        }





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

    private void signIn() {
        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);
        finish();
    }

    private void signOut() {

        SharedPreferences.Editor editor = getSharedPreferences("date", MODE_PRIVATE).edit();
        editor.putString("store_name", null);
        editor.putString("store_owner", null);
        editor.putString("country", null);
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        signIn();
    }
}