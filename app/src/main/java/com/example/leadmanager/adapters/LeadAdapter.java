package com.example.leadmanager.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leadmanager.R;
import com.example.leadmanager.models.Lead;
import com.example.leadmanager.models.LeadApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.MyViewHolder> implements Filterable  {
    public List<LeadApp> itemsFiltered;
    private Context context;
    private RecyclerViewAdapterListener listener;
    public List<LeadApp> itemList;

    public LeadAdapter(Context context, List<LeadApp> itemList, RecyclerViewAdapterListener recyclerViewAdapterListener) {
        this.listener = recyclerViewAdapterListener;
        this.context = context;
        this.itemList = itemList;
        this.itemsFiltered = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lead_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final LeadApp item = itemsFiltered.get(itemsFiltered.size() - position - 1);

        holder.description.setText(item.getDescription());
        holder.source.setText(item.getSource());
        java.util.Date d = new java.util.Date(item.getCreationDate()*1000L);
        String itemDateStr = new SimpleDateFormat("E, dd MMM hh:mm a").format(d);
        holder.time.setText(itemDateStr);

        /*holder.name.setText(item.getName());
        holder.sellingPrice.setText(String.format("%.02f", item.getPrice()));

        holder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCount(item, holder);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemRemoved(item);
                itemList.remove(item);
                //notifyDataSetChanged();
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getCount() != 0) {
                    item.setCount(item.getCount() - 1);
                    item.setTotal((float) (item.getCount() * (item.getPrice() + item.getTaxAmount())));
                    holder.count.setText(String.valueOf(item.getCount()));
                    holder.total.setText(String.format("%.02f", (item.getCount() * (item.getPrice() + item.getTaxAmount()))));
                    listener.onValueChanged((float) (item.getPrice() + item.getTaxAmount()), "minus");
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setCount(item.getCount() + 1);
                item.setTotal((float) (item.getCount() * (item.getPrice() + item.getTaxAmount())));
                holder.count.setText(String.valueOf(item.getCount()));
                holder.total.setText(String.format("%.02f", (item.getCount() * (item.getPrice() + item.getTaxAmount()))));
                listener.onValueChanged((float) (item.getPrice() + item.getTaxAmount()), "plus");
            }
        });


        holder.count.setText(String.valueOf(item.getCount()));
        holder.total.setText(String.format("%.02f", item.getCount() * (item.getPrice() + item.getTaxAmount())));*/

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

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView description, source, time;
        public ImageView imageUrl, plus, minus, remove;

        public MyViewHolder(View view) {
            super(view);

            description = view.findViewById(R.id.leadInfo);
            source = view.findViewById(R.id.sources);
            time = view.findViewById(R.id.creationDate);


        }
    }
}
