package sales_crm.customers.leads.crm.leadmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import sales_crm.customers.leads.crm.leadmanager.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private Spinner spinner;
    private BottomNavigationView bottomNavigationView;

    private BottomAppBar bottomAppBar;
    private boolean isHome = true;
    private FragmentTransaction homeTransaction;
    private LinearLayout bottomLayout;
    private ImageView home, contact, task, calendar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            signIn();
        } else {
            //  syncData();
        }

        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));
                contact.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                task.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                calendar.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                isHome = true;
            }
        });

        contact = findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));
                home.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                task.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                calendar.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new ContactFragment()).commit();
                isHome = false;
            }
        });

        task = findViewById(R.id.task);
        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));
                home.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                contact.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                calendar.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new TaskFragment()).commit();
                isHome = false;
            }
        });

        calendar = findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));
                home.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                contact.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                task.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new ScheduleFragment()).commit();
                isHome = false;
            }
        });

        home.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));

        bottomAppBar = findViewById(R.id.bottomAppBar);
        homeTransaction = getSupportFragmentManager().beginTransaction();
        homeTransaction.replace(R.id.content, new HomeFragment());
        homeTransaction.commit();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewLeadActivity.class);
                startActivity(intent);
            }
        });

//        bottomAppBar.getMenu().getItem(0).getIcon().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Log.v("fjgeryfgyer", "eyfet");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                MenuItem item1;
                for(int i = 0; i<4; i++) {
                    item1 = bottomAppBar.getMenu().getItem(i);
                    if(item.getItemId() != item1.getItemId()) {

                        switch (item1.getItemId()) {
                            case R.id.home:
                                item1.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_home));
                                break;


                            case R.id.contact:
                                item1.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_contact));
                                break;


                            case R.id.task:
                                item1.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_task));
                                break;


                            case R.id.schedule:
                                item1.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_calendar));
                                break;
                        }

                        item1.getIcon().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
                    }
                }

                item.getIcon().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));

                switch (item.getItemId()) {
                    case R.id.home:
                        transaction.replace(R.id.content, new HomeFragment());
                        isHome = true;
                        break;


                    case R.id.contact:
                        transaction.replace(R.id.content, new ContactFragment());
                        isHome = false;
                        break;


                    case R.id.task:
                        transaction.replace(R.id.content, new TaskFragment());
                        isHome = false;
                        break;


                    case R.id.schedule:
                        transaction.replace(R.id.content, new ScheduleFragment());
                        isHome = false;
                        break;
                }
                transaction.commit();
                return true;
            }
        });
//        spinner = findViewById(R.id.daySpinner);
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
//                R.array.day_array, R.layout.color_spinner_layout);
//             // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//       // spinner.setOnItemSelectedListener(this);

        /*bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
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
        });*/
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

    @Override
    public void onBackPressed() {

        if (isHome) {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing App")
                    .setMessage("Are you sure you want to close this app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
            home.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightBlue));
            contact.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
            task.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
            calendar.getDrawable().setTint(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey));
            isHome = true;
        }
    }

}