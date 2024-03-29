package sales_crm.customers.leads.crm.leadmanager.leads;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.Utility;
import sales_crm.customers.leads.crm.leadmanager.models.HistoryItem;
import sales_crm.customers.leads.crm.leadmanager.NotificationEventService;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.Context.ALARM_SERVICE;

public class FollowUpBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText description;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private FollowUpBottomSheet.NotifyParent notifyParent;
    private TextView time;
    private Date date;

    private static final int JOB_ID = 0;
    private JobScheduler mScheduler;

    public FollowUpBottomSheet(FollowUpBottomSheet.NotifyParent notifyParent) {
        this.notifyParent = notifyParent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.followup_bottomsheet, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle mArgs = getArguments();
        String leadUid = mArgs.getString("lead_uid");
        long latestFollowUp = mArgs.getLong("latestFollowUp");
        String lfd = mArgs.getString("lfd");
        progress = new ProgressDialog(getContext());
        description = view.findViewById(R.id.description);
        description.setText(lfd);
        time = view.findViewById(R.id.timeStamp);

        java.util.Date d = new java.util.Date(latestFollowUp*1000L);
        date = d;
        String itemDateStr = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss a zzzz").format(d);
        time.setText(itemDateStr);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        "Follow-up time",
                        "OK",
                        "Cancel"
                );

                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(false);
                dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
                dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2080, Calendar.DECEMBER, 31).getTime());

                if (latestFollowUp > 0)
                    dateTimeDialogFragment.setDefaultDateTime(new java.util.Date(latestFollowUp * 1000L)/*new GregorianCalendar(2021, 6, 12, 6, 20).getTime()*/);
                else
                    dateTimeDialogFragment.setDefaultDateTime(new java.util.Date(Utility.getCurrentTime() * 1000L)/*new GregorianCalendar(2021, 6, 12, 6, 20).getTime()*/);


                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date1) {
                        date = date1;
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        // Date is get on negative button click
                    }
                });

                dateTimeDialogFragment.show(getChildFragmentManager(), "dialog_time");

            }
        });

        Button proceed = view.findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                //createNotification(1000L);

                if (!description.getText().toString().equals("")) {

                    progress.setMessage("scheduling followup");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    /*progress.setMessage("updating followup");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();*/
                    // Date is get on positive button click
                    // Do something
                    //Log.v("dddddddd", date.toString() + "");
                    //SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
                    //Date date1 = df.parse(date);
                    long epoch = date.getTime() / 1000;
                    db.collection("cache").document(user.getUid()).collection("leads")
                            .document(leadUid)
                            .update("latestFollowup", epoch, "lfd", description.getText().toString());

                    HistoryItem historyItem = new HistoryItem();
                    historyItem.setDescription("Follow-up updated to: " + date);
                    historyItem.setDate(Utility.getCurrentTime());

                    db.collection("cache").document(user.getUid())
                            //.collection("contacts").document(contact.getUid())
                            .collection("leads")
                            .document(leadUid)
                            .update("history", FieldValue.arrayUnion(historyItem));

                    java.util.Date d = new java.util.Date(epoch * 1000L);
                    String itemDateStr = new SimpleDateFormat("E, dd MMM hh:mm a").format(d);
                    if (notifyParent != null)
                        notifyParent.notifyAdded(itemDateStr, description.getText().toString(), epoch);

                    //createNotification(epoch * 1000L);

                    //followUpText.setText(itemDateStr);
                    progress.dismiss();
                    dismiss();

                            /*.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            HistoryItem historyItem = new HistoryItem();
                            historyItem.setDescription("Follow-up updated to: " + date);
                            historyItem.setDate(Utility.getCurrentTime());

                            db.collection("cache").document(user.getUid())
                                    //.collection("contacts").document(contact.getUid())
                                    .collection("leads")
                                    .document(leadUid)
                                    .update("history", FieldValue.arrayUnion(historyItem)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    java.util.Date d = new java.util.Date(epoch * 1000L);
                                    String itemDateStr = new SimpleDateFormat("E, dd MMM hh:mm a").format(d);
                                    if (notifyParent != null)
                                        notifyParent.notifyAdded(itemDateStr, description.getText().toString());

                                    //followUpText.setText(itemDateStr);
                                    progress.dismiss();
                                    dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });*/

                    //Log.v("dddddddd", epoch + "");
                } else {
                    Toast.makeText(getContext(), "Please fill the form", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        notifyParent = (FollowUpBottomSheet.NotifyParent) context;
        if (context instanceof FollowUpBottomSheet.NotifyParent) {
            //Log.v("jhgfff", "called");
            notifyParent = (FollowUpBottomSheet.NotifyParent) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notifyParent = null;
    }

    /**
     * PDF Gen should run in own thread to not slow the GUI
     */

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface NotifyParent {
        void notifyAdded(String itemDateStr, String lfd, long epoch);
    }
    /**
     * onClick method for cancelling all existing jobs.
     */
    public void cancelJobs(View view) {

        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(getContext(), "cancelled meeting", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void createNotification (long time) {

        Intent myIntent = new Intent(getActivity() , NotificationEventService. class ) ;
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE );
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( getActivity(), 0 , myIntent , 0 ) ;
        /*Calendar calendar = Calendar. getInstance () ;
        calendar.set(Calendar. SECOND , 0 ) ;
        calendar.set(Calendar. MINUTE , 0 ) ;
        calendar.set(Calendar. HOUR , 0 ) ;
        calendar.set(Calendar. AM_PM , Calendar. AM ) ;
        calendar.add(Calendar. DAY_OF_MONTH , 1 ) ;*/
        //alarmManager.setRepeating(AlarmManager. RTC_WAKEUP , calendar.getTimeInMillis() , 1000 * 60 * 60 * 24 , pendingIntent) ;

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);

        Toast.makeText(getContext(), "scheduled meeting", Toast.LENGTH_SHORT)
                .show();
    }

}
