package com.example.leadmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.leadmanager.R;
import com.example.leadmanager.models.HistoryItem;
import com.example.leadmanager.models.LeadApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> implements Filterable {
    public List<HistoryItem> itemsFiltered;
    private Context context;
    private HistoryAdapter.RecyclerViewAdapterListener listener;
    public List<HistoryItem> itemList;

    public HistoryAdapter(Context context, List<HistoryItem> itemList, HistoryAdapter.RecyclerViewAdapterListener recyclerViewAdapterListener) {
        this.listener = recyclerViewAdapterListener;
        this.context = context;
        this.itemList = itemList;
        this.itemsFiltered = itemList;
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new HistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.MyViewHolder holder, final int position) {
        final HistoryItem item = itemsFiltered.get(itemsFiltered.size() - position - 1);

        holder.description.setText(item.getDescription());
        //holder.source.setText(item.getDescription());

        Log.v("sgfgdsfgyh", item.getDate() + "");
        java.util.Date d = new java.util.Date(item.getDate()*1000L);
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
                    List<HistoryItem> filteredList = new ArrayList<>();
                    for (HistoryItem row : itemList) {

                        if (row.getDescription().toLowerCase().contains(charString.toLowerCase())) {
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
                itemsFiltered = (ArrayList<HistoryItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        assert data != null;
    }


    public interface RecyclerViewAdapterListener {
        void onValueChanged(float amount, String category);

        void onItemRemoved(HistoryItem item);

        void onItemUpdated(float amount, int count);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView description, source, time;
        public ImageView imageUrl, plus, minus, remove;

        public MyViewHolder(View view) {
            super(view);

            description = view.findViewById(R.id.description);
            time = view.findViewById(R.id.timeStamp);


        }
    }
}
