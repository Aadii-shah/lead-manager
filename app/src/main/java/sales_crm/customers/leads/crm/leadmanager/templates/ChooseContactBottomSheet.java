package sales_crm.customers.leads.crm.leadmanager.templates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.ContactsAdapter;
import sales_crm.customers.leads.crm.leadmanager.RecyclerViewTouchListener;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class ChooseContactBottomSheet extends BottomSheetDialogFragment implements ContactsAdapter.RecyclerViewAdapterListener {

    public static final String TAG = "ActionBottomDialogChooseTemplate";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progress;
    private ChooseContactBottomSheet  .NotifyParent notifyParent;

    private RecyclerView recyclerView;
    private ContactsAdapter  contactsAdapter ;
    private SearchView searchView;
    List<Contact> itemsList;
    private String description, category;

    public ChooseContactBottomSheet  newInstance() {

        return new ChooseContactBottomSheet ();
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
        View view = inflater.inflate(R.layout.choose_contact_bottom_sheet, container, false);

        searchView = view.findViewById(R.id.search);

        recyclerView = view.findViewById(R.id.recyclerView);
        itemsList = new ArrayList<>();
        contactsAdapter  = new ContactsAdapter (getContext(), itemsList, this);
        getContacts();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsAdapter .getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle mArgs = getArguments();
        description = mArgs.getString("description");
        category = mArgs.getString("category");
        progress = new ProgressDialog(getContext());

    }

    private void getContacts() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setAdapter(contactsAdapter );
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), recyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightWhite));
                Contact contact = contactsAdapter.itemsFiltered.get(contactsAdapter.itemsFiltered.size() - 1 - position);
                switch (category) {

                    case "sms":
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contact.getPhone(), null)));
                        break;

                    case "whatsapp":
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, description);

                        whatsappIntent.putExtra("jid", contact.getPhone() + "@s.whatsapp.net");
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
                        intent.putExtra(Intent.EXTRA_EMAIL, contact.getEmail());
                        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        //if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                        //}
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Source CACHE = Source.CACHE;

        db.collection("cache").document(user.getUid()).collection("contacts")
                .get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult()!=null) {
                itemsList.clear();
                ////Log.v("dipppppp", "kkk" + task.getResult().getData());

                //ArrayList<HistoryItem> historyItems = (ArrayList<HistoryItem>) task.getResult().get(category);


                //if(historyItems!=null) {
                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());
                    Contact template = gson.fromJson(jsonElement, Contact.class);
                    //lead.setUid(document.getId());
                    itemsList.add(template);

                }
                //itemsList.add(historyItem1);
                contactsAdapter .notifyDataSetChanged();
                //}
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        notifyParent = (TemplateBottomSheet.NotifyParent) context;
  /*      if (context instanceof ChooseContactBottomSheet  .NotifyParent) {
            //Log.v("jhgfff", "called");
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
    public void onItemRemoved(Contact item) {

    }


    @Override
    public void onItemUpdated(float amount, int count) {

    }


    public interface NotifyParent {
        void notifyAdded();
    }


}
