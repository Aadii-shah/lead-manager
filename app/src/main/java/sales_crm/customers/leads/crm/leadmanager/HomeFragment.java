package sales_crm.customers.leads.crm.leadmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import sales_crm.customers.leads.crm.leadmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

import java.util.Objects;

import static sales_crm.customers.leads.crm.leadmanager.Utility.getMidNightTimeStamp;

public class HomeFragment extends Fragment {

    private CardView newLead, todayMeeting, unAnswered, pending, allLead, converted, notInterested, interested;
    private TextView newLeadCount, todayFollowupsCount, interestedCount, pendingCount, allLeadCount, notInterestedCount, unansweredCount, convertedCount;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private long time;
    private Spinner spinner;
    private boolean flag = true;

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



        spinner = view.findViewById(R.id.daySpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.day_array, R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ImageView profile = view.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileBottomSheet profileSheet = ProfileBottomSheet.newInstance();
                profileSheet.show(getChildFragmentManager(), ProfileBottomSheet.TAG);
            }
        });

        newLead = view.findViewById(R.id.newLead);
        todayMeeting = view.findViewById(R.id.todayMeeting);
        unAnswered = view.findViewById(R.id.unAnswered);
        pending = view.findViewById(R.id.pending);
        allLead = view.findViewById(R.id.allLead);
        converted = view.findViewById(R.id.converted);
        notInterested = view.findViewById(R.id.notInterested);
        interested = view.findViewById(R.id.interested);
        // spinner.setOnItemSelectedListener(this);

        allLeadCount = view.findViewById(R.id.allLeadCount);
        newLeadCount = view.findViewById(R.id.newLeadCount);
        convertedCount = view.findViewById(R.id.convertedCount);
        notInterestedCount = view.findViewById(R.id.notInterestedCount);
        unansweredCount = view.findViewById(R.id.unansweredCount);
        newLeadCount = view.findViewById(R.id.newLeadCount);
        todayFollowupsCount = view.findViewById(R.id.todayFollowupsCount);
        interestedCount = view.findViewById(R.id.interestedCount);
        pendingCount = view.findViewById(R.id.pendingCount);


        newLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "new_lead");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        todayMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "today_meetings");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        unAnswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "unanswered");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "pending");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        newLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "new_lead");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        allLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "all_leads");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        converted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "converted");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        notInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "not_interested");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("category", "interested");
                if (spinner.getSelectedItem().toString().equals("Overall"))
                    intent.putExtra("isToday", false);
                startActivity(intent);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1)
                    getCount(false);
                else getCount(true);
                flag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;

    }

    private void getCount(boolean isToday) {

        Log.v("htjfhgt", "called");
        time = getMidNightTimeStamp();

        if (!isToday)
            time = 0;

        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");

        Source CACHE = Source.CACHE;

        dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", "New Lead").get(CACHE).addOnCompleteListener(task -> {

            if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {
                newLeadCount.setText(task.getResult().size() + "");

            } else newLeadCount.setText("0");

            dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", "Interested").get(CACHE).addOnCompleteListener(task1 -> {

                if (task1.isSuccessful() && !Objects.requireNonNull(task1.getResult()).isEmpty()) {
                    interestedCount.setText(task1.getResult().size() + "");
                } else interestedCount.setText("0");

                dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", "Pending").get(CACHE).addOnCompleteListener(task2 -> {

                    if (task2.isSuccessful() && !Objects.requireNonNull(task2.getResult()).isEmpty()) {
                        pendingCount.setText(task2.getResult().size() + "");
                    } else pendingCount.setText("0");
                    dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", "Unanswered").get(CACHE).addOnCompleteListener(task3 -> {

                        if (task3.isSuccessful() && !Objects.requireNonNull(task3.getResult()).isEmpty()) {
                            unansweredCount.setText(task3.getResult().size() + "");
                        } else unansweredCount.setText("0");
                        dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", "Not Interested").get(CACHE).addOnCompleteListener(task4 -> {

                            if (task4.isSuccessful() && !Objects.requireNonNull(task4.getResult()).isEmpty()) {
                                notInterestedCount.setText(task4.getResult().size() + "");
                            } else notInterestedCount.setText("0");

                            dataRef.whereGreaterThan("creationDate", time).whereEqualTo("status", "Converted").get(CACHE).addOnCompleteListener(task5 -> {

                                if (task5.isSuccessful() && !Objects.requireNonNull(task5.getResult()).isEmpty()) {
                                    convertedCount.setText(task5.getResult().size() + "");
                                } else convertedCount.setText("0");

                                dataRef.whereGreaterThan("creationDate", time).get(CACHE).addOnCompleteListener(task6 -> {

                                    if (task6.isSuccessful() && !Objects.requireNonNull(task6.getResult()).isEmpty()) {
                                        allLeadCount.setText(task6.getResult().size() + "");
                                    } else allLeadCount.setText("0");

                                    dataRef.whereGreaterThan("latestFollowup", time).whereLessThan("latestFollowup", time + 86400).get(CACHE).addOnCompleteListener(task7 -> {

                                        if (task7.isSuccessful() && !Objects.requireNonNull(task7.getResult()).isEmpty()) {
                                            todayFollowupsCount.setText(task7.getResult().size() + "");
                                        } else todayFollowupsCount.setText("0");

                                    });

                                });

                            });
                        });

                    });

                });
            });

        });
    }

    @Override
    public void onResume() {

        Log.v("hfgytfyshfy", "called");

        if (flag) {
            if (spinner.getSelectedItem().toString().equals("Overall"))
                getCount(false);
            else getCount(true);
        }
        //getCount(true);
        super.onResume();
    }
}