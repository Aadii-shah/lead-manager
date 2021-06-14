package com.example.leadmanager.templates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leadmanager.R;
import com.example.leadmanager.adapters.RecyclerViewTouchListener;
import com.example.leadmanager.adapters.TemplatesAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class ChooseTemplateBottomSheet extends BottomSheetDialogFragment implements TemplatesAdapter.RecyclerViewAdapterListener {

    public static final String TAG = "ActionBottomDialogChooseTemplate";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText description, name;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private ChooseTemplateBottomSheet .NotifyParent notifyParent;

    private RecyclerView recyclerView;
    private TemplatesAdapter templatesAdapter;
    private SearchView searchView;
    List<Template> itemsList;
    private String contact, category;

    public ChooseTemplateBottomSheet newInstance() {

        return new ChooseTemplateBottomSheet();
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
        View view = inflater.inflate(R.layout.choose_template_bottom_sheet, container, false);

        searchView = view.findViewById(R.id.search);

        recyclerView = view.findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        templatesAdapter = new TemplatesAdapter(getContext(), itemsList, this, "details");
        getTemplates();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                templatesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle mArgs = getArguments();
        contact = mArgs.getString("contact");
        category = mArgs.getString("category");
        progress = new ProgressDialog(getContext());
        description = view.findViewById(R.id.description);
        name = view.findViewById(R.id.name);

    }

    private void getTemplates() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(templatesAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightWhite));

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Source CACHE = Source.CACHE;

        db.collection("cache").document(user.getUid()).collection("templates")
                .get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult()!=null) {
                itemsList.clear();
                //Log.v("dipppppp", "kkk" + task.getResult().getData());

                //ArrayList<HistoryItem> historyItems = (ArrayList<HistoryItem>) task.getResult().get(category);


                //if(historyItems!=null) {
                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());
                    Template template = gson.fromJson(jsonElement, Template.class);
                    //lead.setUid(document.getId());
                    itemsList.add(template);

                }
                Template template1 = new Template();
                template1.setName("Default");
                template1.setDescription("Hi");
                itemsList.add(template1);
                //itemsList.add(historyItem1);
                templatesAdapter.notifyDataSetChanged();
                //}
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        notifyParent = (TemplateBottomSheet.NotifyParent) context;
  /*      if (context instanceof ChooseTemplateBottomSheet .NotifyParent) {
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

        if(notifyParent!=null)
            notifyParent.notifyAdded();

        /*Activity activity = getActivity();
        if(activity instanceof NotifyParent)
            ((NotifyParent)activity).notifyAdded();*/
        //notifyParent.notifyAdded();
        super.onDismiss(dialog);
    }

    @Override
    public void onValueChanged(float amount, String category) {

    }

    @Override
    public void onItemRemoved(Template item) {

    }

    @Override
    public void onItemUpdated(float amount, int count) {

    }

    @Override
    public void onSendClicked(int position) {
        Template template = templatesAdapter.itemsFiltered.get(position);

        switch (category) {

            case "sms":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact, null)));
                break;

            case "whatsapp":
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, template.getDescription());

                whatsappIntent.putExtra("jid", contact + "@s.whatsapp.net");
                whatsappIntent.setPackage("com.whatsapp");
                //whatsappIntent.setType("application/pdf");
                try {
                    startActivity(whatsappIntent);
                    //startActivity(Intent.createChooser(whatsappIntent, ""));
                } catch (android.content.ActivityNotFoundException ex) {
                    // ToastHelper.MakeShortText("Whatsapp have not been installed.");
                }
                break;

            case "email":
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, contact);
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                //}
        }
    }

    @Override
    public void onSendSmsClicked(int position) {
        Template template = templatesAdapter.itemsFiltered.get(position);

        switch (category) {

            case "sms":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact, null)));
                break;

            case "whatsapp":
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, template.getDescription());

                whatsappIntent.putExtra("jid", contact + "@s.whatsapp.net");
                whatsappIntent.setPackage("com.whatsapp");
                //whatsappIntent.setType("application/pdf");
                try {
                    startActivity(whatsappIntent);
                    //startActivity(Intent.createChooser(whatsappIntent, ""));
                } catch (android.content.ActivityNotFoundException ex) {
                    // ToastHelper.MakeShortText("Whatsapp have not been installed.");
                }
                break;

            case "email":
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, contact);
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                //}
        }
    }

    @Override
    public void onSendWhatsAppClicked(int position) {
        Template template = templatesAdapter.itemsFiltered.get(position);

        switch (category) {

            case "sms":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact, null)));
                break;

            case "whatsapp":
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, template.getDescription());

                whatsappIntent.putExtra("jid", contact + "@s.whatsapp.net");
                whatsappIntent.setPackage("com.whatsapp");
                //whatsappIntent.setType("application/pdf");
                try {
                    startActivity(whatsappIntent);
                    //startActivity(Intent.createChooser(whatsappIntent, ""));
                } catch (android.content.ActivityNotFoundException ex) {
                    // ToastHelper.MakeShortText("Whatsapp have not been installed.");
                }
                break;

            case "email":
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, contact);
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                //}
        }
    }

    @Override
    public void onSendEmailClicked(int position) {
        Template template = templatesAdapter.itemsFiltered.get(position);

        switch (category) {

            case "sms":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact, null)));
                break;

            case "whatsapp":
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, template.getDescription());

                whatsappIntent.putExtra("jid", contact + "@s.whatsapp.net");
                whatsappIntent.setPackage("com.whatsapp");
                //whatsappIntent.setType("application/pdf");
                try {
                    startActivity(whatsappIntent);
                    //startActivity(Intent.createChooser(whatsappIntent, ""));
                } catch (android.content.ActivityNotFoundException ex) {
                    // ToastHelper.MakeShortText("Whatsapp have not been installed.");
                }
                break;

            case "email":
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, contact);
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                //}
        }
    }

    public interface NotifyParent {
        void notifyAdded();
    }


}
