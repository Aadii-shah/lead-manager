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
import com.google.firebase.firestore.FirebaseFirestore;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.models.LeadApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.MyViewHolder> implements Filterable {
    public List<LeadApp> itemsFiltered;
    private Context context;
    private RecyclerViewAdapterListener listener;
    public List<LeadApp> itemList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    boolean isEnable = false;
    boolean isSelectAll = false;
    public List<LeadApp> selectList;
    private boolean isDeleteOption = false;

    public LeadAdapter(Context context, List<LeadApp> itemList, RecyclerViewAdapterListener recyclerViewAdapterListener, boolean isDeleteOption) {
        this.listener = recyclerViewAdapterListener;
        this.context = context;
        this.itemList = itemList;
        this.itemsFiltered = itemList;
        this.isDeleteOption = isDeleteOption;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lead_item, parent, false);
        selectList = new ArrayList<>();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final LeadApp item = itemsFiltered.get(itemsFiltered.size() - position - 1);

        holder.description.setText(item.getDescription());
        holder.source.setText(item.getSource());
        java.util.Date d = new java.util.Date(item.getCreationDate() * 1000L);
        String itemDateStr = new SimpleDateFormat("E, dd MMM hh:mm a").format(d);
        holder.time.setText(itemDateStr);
        holder.status.setText(item.getStatus());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(isDeleteOption) {
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
                                                .setTitle("Delete Leads")
                                                .setMessage("Are you sure you want to delete selected items?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        for (LeadApp lead : selectList) {
                                                            itemList.remove(lead);
                                                            db.collection("cache")
                                                                    .document(user.getUid())
                                                                    .collection("leads")
                                                                    .document(lead.getUid())
                                                                    .delete();
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
        LeadApp leadApp = itemList.get(itemsFiltered.size() - holder.getAdapterPosition() - 1);

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
                    List<LeadApp> filteredList = new ArrayList<>();
                    for (LeadApp row : itemList) {

                        if (row.getDescription().toLowerCase().contains(charString.toLowerCase())
                                || row.getSource().contains(charSequence)) {

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
                itemsFiltered = (ArrayList<LeadApp>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        assert data != null;
    }


    public interface RecyclerViewAdapterListener {
        void onValueChanged(float amount, String category);

        void onItemRemoved(LeadApp item);

        void onItemUpdated(float amount, int count);

        void hideToolBar(boolean hide);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView description, source, time, status;
        public ImageView ivCheckBox;

        public MyViewHolder(View view) {
            super(view);

            ivCheckBox = view.findViewById(R.id.iv_check_box);
            description = view.findViewById(R.id.leadInfo);
            source = view.findViewById(R.id.sources);
            time = view.findViewById(R.id.creationDate);
            status = view.findViewById(R.id.status);


        }
    }
}
