package com.example.leadmanager.calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.example.leadmanager.models.Template;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialogTemplate";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText description, time;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private CalendarBottomSheet.NotifyParent notifyParent;



    public CalendarBottomSheet(CalendarBottomSheet.NotifyParent notifyParent) {
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
        View view = inflater.inflate(R.layout.calendar_bottom_sheet, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle mArgs = getArguments();
//        String leadUid = mArgs.getString("lead_uid");
        //      String category = mArgs.getString("category");
        progress = new ProgressDialog(getContext());
        description = view.findViewById(R.id.description);
        time = view.findViewById(R.id.time);

        Button proceed = view.findViewById(R.id.open);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

            }
        });

        TextView cancel = view.findViewById(R.id.cancel);
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
