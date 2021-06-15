package com.example.leadmanager.leads;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.leadmanager.R;
import com.example.leadmanager.Utility;
import com.example.leadmanager.models.HistoryItem;
import com.example.leadmanager.templates.TemplateBottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class FollowUpBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText description;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private FollowUpBottomSheet.NotifyParent notifyParent;
    private TextView time;
    private Date date;

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

                if (!description.getText().toString().equals("")) {

                    progress.setMessage("scheduling followup");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    /*progress.setMessage("updating followup");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();*/
                    // Date is get on positive button click
                    // Do something
                    Log.v("dddddddd", date.toString() + "");
                    //SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
                    //Date date1 = df.parse(date);
                    long epoch = date.getTime() / 1000;
                    db.collection("cache").document(user.getUid()).collection("leads")
                            .document(leadUid)
                            .update("latestFollowup", epoch, "lfd", description.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                    String itemDateStr = new SimpleDateFormat("dd-MMM-YYYY HH:mm").format(d);
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
                    });

                    Log.v("dddddddd", epoch + "");
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
            Log.v("jhgfff", "called");
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
        void notifyAdded(String itemDateStr, String lfd);
    }

}
