package com.example.leadmanager.leads;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leadmanager.NewLeadActivity;
import com.example.leadmanager.R;
import com.example.leadmanager.Utility;
import com.example.leadmanager.models.HistoryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.view.View.GONE;

public class DescriptionBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText description;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private NotifyParent notifyParent;

    public DescriptionBottomSheet newInstance() {
        return new DescriptionBottomSheet();
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
        View view = inflater.inflate(R.layout.activity_description_bottom_sheet, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle mArgs = getArguments();
        String leadUid = mArgs.getString("lead_uid");
        String category = mArgs.getString("category");
        progress = new ProgressDialog(getContext());
        description = view.findViewById(R.id.description);

        Button proceed = view.findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                progress.setMessage("adding new " + category);
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                HistoryItem historyItem = new HistoryItem();

                if(!description.getText().toString().equals("")) {
                    historyItem.setDescription(description.getText().toString());
                    historyItem.setDate(Utility.getCurrentTime());

                    db.collection("cache").document(user.getUid())
                            //.collection("contacts").document(contact.getUid())
                            .collection("leads")
                            .document(leadUid)
                            .update(category, FieldValue.arrayUnion(historyItem)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.v("ttttttt", historyItem.getDate() + "");
                            historyItem.setDescription("Added " + category);
                            db.collection("cache").document(user.getUid())
                                    //.collection("contacts").document(contact.getUid())
                                    .collection("leads")
                                    .document(leadUid)
                                    .update("history", FieldValue.arrayUnion(historyItem)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    notifyParent.notifyAdded();
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
                            progress.dismiss();
                            notifyParent.notifyAdded();
                            dismiss();
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Please fill the form", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        notifyParent = (NotifyParent) context;
        if (context instanceof NotifyParent) {
            Log.v("jhgfff", "called");
            notifyParent = (NotifyParent) context;
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
        void notifyAdded();
    }


}