package com.example.leadmanager.templates;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.leadmanager.R;
import com.example.leadmanager.Utility;
import com.example.leadmanager.models.HistoryItem;
import com.example.leadmanager.models.Template;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class TemplateBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialogTemplate";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText description, name;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private TemplateBottomSheet.NotifyParent notifyParent;


    public TemplateBottomSheet(NotifyParent notifyParent) {
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
        View view = inflater.inflate(R.layout.template_bottom_sheet, container, false);

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
        name = view.findViewById(R.id.name);

        Button proceed = view.findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                progress.setMessage("adding new  template");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                Template template = new Template();

                if (!description.getText().toString().equals("") && !name.getText().toString().equals("")) {
                    template.setDescription(description.getText().toString());
                    template.setName(name.getText().toString());

                    db.collection("cache").document(user.getUid())
                            //.collection("contacts").document(contact.getUid())
                            .collection("templates")
                            .add(template).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progress.dismiss();
                            description.setText("");
                            name.setText("");
                            if (notifyParent != null)
                                notifyParent.notifyAdded();
                            dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.dismiss();
                            if (notifyParent != null)
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
//        notifyParent = (TemplateBottomSheet.NotifyParent) context;
  /*      if (context instanceof TemplateBottomSheet.NotifyParent) {
            Log.v("jhgfff", "called");
            notifyParent = (TemplateBottomSheet.NotifyParent) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }*/
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

        /*Activity activity = getActivity();
        if(activity instanceof NotifyParent)
            ((NotifyParent)activity).notifyAdded();*/
        //notifyParent.notifyAdded();
        super.onDismiss(dialog);
    }

    public interface NotifyParent {
        void notifyAdded();
    }


}
