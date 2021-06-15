package com.example.leadmanager;

import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.leadmanager.models.Contact;
import com.example.leadmanager.models.LeadApp;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.FluentIterable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import static com.example.leadmanager.Utility.getMidNightTimeStampByMonth;

public class ScheduleFragment extends Fragment implements WeekView.EventClickListener, WeekView.EventLongPressListener
        , WeekView.EmptyViewLongPressListener
, DatePickerListener {

    private static ArrayList<WeekViewEvent> mNewEvents;
    //private List<WeekViewEvent> events;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
    private WeekView mWeekView;
    private boolean flag = false;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        // Get a reference for the week view in the layout.
        mWeekView = view.findViewById(R.id.weekView);

        events = new ArrayList<WeekViewEvent>();



        HorizontalPicker picker = (HorizontalPicker) view.findViewById(R.id.datePicker);
        //DateTime dateTime= new DateTime();

        // initialize it and attach a listener
        picker
                .setListener(this)
                .init();

        picker.setDate(new DateTime());

        // Get a reference for the week view in the layout.
        //mWeekView = view.findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        //mWeekView.setMonthChangeListener(this);
        // Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });
    /*Calendar cal = Calendar.getInstance();
    cal.set(2015, 5 - 1, 30, 12, 00);
    mWeekView.goToDate(cal);*/
        /*mWeekView.setNumberOfVisibleDays(5);
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));//espacio entre columnas
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));//tamanho de letra de la api
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));//tamanho de lettra del evento
        mWeekView.setEventPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
*/

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                getEvents(newYear, newMonth);
                List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
                for (WeekViewEvent event : events) {
                    if (eventMatches(event, newYear, newMonth)) {
                        matchedEvents.add(event);
                    }
                }
                return matchedEvents;
            }
        });


        return view;
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }


    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }


    private void getEvents(int newYear, int newMonth) {
        CollectionReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("leads");


        Source CACHE = Source.CACHE;

        Log.v("gggg:", "entered1");

        Calendar calendar = Calendar.getInstance();
        calendar.set(newYear, newMonth, 1, 0, 0);
        Log.v("dygfyds", calendar.get(Calendar.MONTH) + "");
        if (!containsLocation(events, calendar)) {

            Log.v("dygfyds", "called");

            dataRef.whereGreaterThan("latestFollowup", getMidNightTimeStampByMonth(newYear, newMonth))
                    .whereLessThanOrEqualTo("latestFollowup", getMidNightTimeStampByMonth(newYear, newMonth + 1) - 1)
                    .get(CACHE).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {

                        List<WeekViewEvent> eventsNew = new ArrayList<WeekViewEvent>();

                        WeekViewEvent weekViewEvent = new WeekViewEvent();
                        int i = 0;
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(document.getData());
                            LeadApp leadApp = gson.fromJson(jsonElement, LeadApp.class);
                            leadApp.setUid(document.getReference().getId());
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(2021, 5, 14, 5, 10);
                            calendar.setTimeInMillis(leadApp.getLatestFollowup()*1000L);
                            Calendar calendar1 = Calendar.getInstance();
                            //calendar1.set(2021, 5, 14, 5, 50);
                            calendar1.setTimeInMillis((leadApp.getLatestFollowup()*1000L) + 1800000);
                            WeekViewEvent toAdd = new WeekViewEvent(i, leadApp.getStatus(), calendar, calendar1);
                            toAdd.setLocation("ooooooooooooo");


                            switch (leadApp.getStatus()) {
                                case "New Lead":
                                    toAdd.setColor(ContextCompat.getColor(getContext(), R.color.leadColor));
                                    break;

                                case "Interested":
                                    toAdd.setColor(ContextCompat.getColor(getContext(), R.color.interestedColor));
                                    break;

                                case "Unanswered":
                                    toAdd.setColor(ContextCompat.getColor(getContext(), R.color.orange));
                                    break;

                                case "Pending":
                                    toAdd.setColor(ContextCompat.getColor(getContext(), R.color.pendingColor));
                                    break;

                                default:
                                    toAdd.setColor(ContextCompat.getColor(getContext(), R.color.lightWhite));

                            }

                            eventsNew.add(toAdd);
                            weekViewEvent = toAdd;
                            Log.v("kjfhruygfru", "" + eventsNew.get(0).getName());
                            i++;
                        }

                        // Refresh the week view.

                        if (!events.contains(weekViewEvent)) {
                            events.addAll(eventsNew);
                            mWeekView.notifyDatasetChanged();
                        }

                        if (!events.contains(weekViewEvent)) {

                        }


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("gggg:", "enterederror");
                }
            });

        }
    }


    public static boolean containsLocation(List<WeekViewEvent> c, Calendar calendar) {

        for (WeekViewEvent o : c) {
            Calendar calendar1 = o.getStartTime();
            calendar1.get(Calendar.MONTH);
            if (calendar1.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {

        dateSelected.toDate();

        Log.v("hbchbch", "called" + dateSelected.toDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateSelected.toDate());
        mWeekView.goToDate(calendar);

    }


    /*
    Calendar calendar = Calendar.getInstance();
Calendar calendarMy = Calendar.getInstance();

nextMonth.setOnClickListener(new View.OnClickListener() {
 @Override
  public void onClick(View v) {

   if (month > 11) {//checking the month
     month = 1;
     year++;
   } else {
   month++;
   }

   //update current time
    calendarMy.set(Calendar.MONTH,month);
    calendarMy.set(Calendar.YEAR,year);

    disableButton();

   setGridCellAdapterToDate(month, year);
   }
   });

//prev arrow click to show prev month
    prevMonth.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

    if (month <= 1) {//checking the month
        month = 12;
        year--;
    } else {
    month--;
    }

    //update current time
     calendarMy.set(Calendar.MONTH,month);
    calendarMy.set(Calendar.YEAR,year);

    disableButton();


    setGridCellAdapterToDate(month, year);
                        }
   });
     */
}