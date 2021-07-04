package sales_crm.customers.leads.crm.leadmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;
import sales_crm.customers.leads.crm.leadmanager.models.LeadApp;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {
    public List<Contact> itemsFiltered;
    private Context context;
    private ContactsAdapter.RecyclerViewAdapterListener listener;
    public List<Contact> itemList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    boolean isEnable = false;
    boolean isSelectAll = false;
    public List<Contact> selectList = new ArrayList<>();

    public ContactsAdapter(Context context, List<Contact> itemList, ContactsAdapter.RecyclerViewAdapterListener recyclerViewAdapterListener) {
        this.listener = recyclerViewAdapterListener;
        this.context = context;
        this.itemList = itemList;
        this.itemsFiltered = itemList;
    }

    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        return new ContactsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Contact item = itemsFiltered.get(itemsFiltered.size() - position - 1);

        holder.name.setText(item.getName());
        holder.address.setText(item.getAddress());
        holder.phone.setText(item.getPhone());
        holder.email.setText(item.getEmail());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEnable) {
                    isEnable = true;
                    listener.hideToolBar(true);
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.action_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            isEnable = true;
                            ClickItem(holder);
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.delete:
                                    new AlertDialog.Builder(context)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Closing App")
                                            .setMessage("Are you sure you want to delete selected contacts and associated leads?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    for (Contact contact : selectList) {
                                                        itemList.remove(contact);
                                                        db.collection("cache")
                                                                .document(user.getUid())
                                                                .collection("contacts")
                                                                .document(contact.getUid())
                                                                .delete();

                                                        deleteLeadsByContactUID(contact.getUid());
                                                    }
                                                    listener.hideToolBar(false);
                                                    actionMode.finish();
                                                }

                                            })
                                            .setNegativeButton("No", null)
                                            .show();


                                    break;

                                case R.id.select_all:
                                    //listener.hideToolBar(false);
                                    //actionMode.finish();
                                    if (selectList.size() == itemList.size()) {
                                        isSelectAll = false;
                                        menuItem.setIcon(R.drawable.ic_checked);
                                        selectList.clear();
                                    } else {
                                        isSelectAll = true;
                                        menuItem.setIcon(R.drawable.ic_close_a);
                                        selectList.clear();
                                        selectList.addAll(itemList);
                                    }
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            listener.hideToolBar(false);
                            isEnable = false;
                            isSelectAll = false;
                            selectList.clear();
                            notifyDataSetChanged();
                        }
                    };

                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    ClickItem(holder);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v("isEnabled", "" + isEnable);
                if (isEnable) {
                    ClickItem(holder);
                } else {
                    listener.onItemRemoved(item);
                }
            }
        });

        if (isSelectAll) {
            holder.ivCheckBox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.ivCheckBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

    }

    private void ClickItem(MyViewHolder holder) {
        Contact leadApp = itemList.get(itemsFiltered.size() - holder.getAdapterPosition() - 1);

        if (holder.ivCheckBox.getVisibility() == View.GONE) {

            holder.ivCheckBox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);

            selectList.add(leadApp);
        } else {
            holder.ivCheckBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
            selectList.remove(leadApp);
        }
    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemsFiltered = itemList;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : itemList) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getPhone().contains(charSequence)
                                || row.getEmail().contains(charSequence)
                                || row.getAddress().contains(charSequence)) {

                            filteredList.add(row);
                        }
                    }

                    itemsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        assert data != null;
    }


    public interface RecyclerViewAdapterListener {
        void onValueChanged(float amount, String category);

        void onItemRemoved(Contact item);

        void onItemUpdated(float amount, int count);

        void hideToolBar(boolean hide);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, address, email, phone;
        public ImageView ivCheckBox;

        public MyViewHolder(View view) {
            super(view);
            ivCheckBox = view.findViewById(R.id.iv_check_box);
            name = view.findViewById(R.id.contactName);
            address = view.findViewById(R.id.contactAddress);
            phone = view.findViewById(R.id.phone);
            email = view.findViewById(R.id.email);


        }
    }

    private void deleteLeadsByContactUID(String uid) {
        db.collection("cache")
                .document(user.getUid())
                .collection("leads")
                .whereEqualTo("contactUid", uid).get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        db.collection("cache")
                                .document(user.getUid())
                                .collection("leads")
                                .document(documentSnapshot.getReference().getId()).delete();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
