package com.example.leadmanager.calendar;

import android.app.ProgressDialog;
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

import com.example.leadmanager.LeadDetailsActivity;
import com.example.leadmanager.ListActivity;
import com.example.leadmanager.R;
import com.example.leadmanager.models.Contact;
import com.example.leadmanager.models.LeadApp;
import com.example.leadmanager.models.Template;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class CalendarBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialogTemplate";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView description, time;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private CalendarBottomSheet.NotifyParent notifyParent;


    public CalendarBottomSheet newInstance() {
        return new CalendarBottomSheet();
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
        View view = inflater.inflate(R.layout.calendar_bottom_sheet, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle mArgs = getArguments();
        String leadUid = mArgs.getString("lead_uid");
        String descriptionText = mArgs.getString("description");
        String contactUid = mArgs.getString("contactUid");
        Long startTime = mArgs.getLong("startTime");
        progress = new ProgressDialog(getContext());
        description = view.findViewById(R.id.description);
        TextView name = view.findViewById(R.id.name);

        db.collection("cache").document(user.getUid())
                .collection("contacts").document(contactUid)
                .get(Source.CACHE)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());
                        Contact contact = gson.fromJson(jsonElement, Contact.class);
                        name.setText(contact.getName());

                        //lead.setUid(documentSnapshot.getReference().getId());

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        description.setText(descriptionText);
        time = view.findViewById(R.id.time);

        java.util.Date d = new java.util.Date(startTime);
        String itemDateStr = new SimpleDateFormat("E, dd MMM yyyy hh:mm a zzzz").format(d);
        time.setText(itemDateStr);

        Button proceed = view.findViewById(R.id.open);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                db.collection("cache").document(user.getUid())
                        .collection("leads").document(leadUid)
                        .get(Source.CACHE)
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Gson gson = new Gson();
                                JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());
                                LeadApp lead = gson.fromJson(jsonElement, LeadApp.class);

                                lead.setUid(documentSnapshot.getReference().getId());

                                Intent intent = new Intent(getContext(), LeadDetailsActivity.class);
                                intent.putExtra("lead", lead);
                                startActivity(intent);
                                //requireActivity().finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notifyParent = null;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public interface NotifyParent {
        void notifyAdded();
    }


}
