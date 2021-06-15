package com.example.leadmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.leadmanager.R;
import com.example.leadmanager.models.Lead;
import com.example.leadmanager.models.LeadApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FollowUpAdapter extends RecyclerView.Adapter<FollowUpAdapter.MyViewHolder> implements Filterable {
    public List<LeadApp>  itemsFiltered;
    private Context context;
    private FollowUpAdapter.RecyclerViewAdapterListener listener;
    public List<LeadApp>  itemList;

    public FollowUpAdapter(Context context, List<LeadApp>  itemList, FollowUpAdapter.RecyclerViewAdapterListener recyclerViewAdapterListener) {
        this.listener = recyclerViewAdapterListener;
        this.context = context;
        this.itemList = itemList;
        this.itemsFiltered = itemList;
    }

    @Override
    public FollowUpAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.followup_item, parent, false);

        return new FollowUpAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FollowUpAdapter.MyViewHolder holder, final int position) {
        final LeadApp item = itemsFiltered.get(itemsFiltered.size() - position - 1);

        holder.description.setText(item.getDescription());

        if(item.getNotes() !=null )
        holder.note.setText(item.getLfd());
        else holder.note.setText("NA");
        java.util.Date d = new java.util.Date(item.getLatestFollowup()*1000L);
        String itemDateStr = new SimpleDateFormat("E, dd MMM hh:mm a").format(d);
        holder.time.setText(itemDateStr);

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
                    List<LeadApp>  filteredList = new ArrayList<>();
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
                itemsFiltered = (ArrayList<LeadApp> ) filterResults.values;
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

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView description, note, time;
        public ImageView imageUrl, plus, minus, remove;

        public MyViewHolder(View view) {
            super(view);

            description = view.findViewById(R.id.leadInfo);
            note = view.findViewById(R.id.note);
            time = view.findViewById(R.id.creationDate);


        }
    }
}
