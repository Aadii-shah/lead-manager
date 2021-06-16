package com.example.leadmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.leadmanager.R;
import com.example.leadmanager.models.LeadApp;
import com.example.leadmanager.models.Template;
import com.example.leadmanager.models.TemplateApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TemplatesAdapter extends RecyclerView.Adapter<TemplatesAdapter.MyViewHolder> implements Filterable {
    public List<TemplateApp> itemsFiltered;
    private Context context;
    private TemplatesAdapter.RecyclerViewAdapterListener listener;
    public List<TemplateApp>  itemList;
    private int row_index = -1;
    private String category = "details";

    public TemplatesAdapter(Context context, List<TemplateApp>  itemList, TemplatesAdapter.RecyclerViewAdapterListener recyclerViewAdapterListener, String category) {
        this.listener = recyclerViewAdapterListener;
        this.context = context;
        this.itemList = itemList;
        this.itemsFiltered = itemList;
        this.category = category;
    }

    @Override
    public TemplatesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_template_item, parent, false);

        return new TemplatesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TemplatesAdapter.MyViewHolder holder, final int position) {
        final TemplateApp item = itemsFiltered.get(itemsFiltered.size() - position - 1);

        holder.description.setText(item.getDescription());
        holder.name.setText(item.getName());

        if(category.equals("details")) {
            holder.sendImage.setVisibility(View.VISIBLE);
            holder.sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSendClicked(itemsFiltered.size() - position - 1);
                }
            });
        } else {
            holder.sendSms.setVisibility(View.VISIBLE);
            holder.sendWhatsApp.setVisibility(View.VISIBLE);
            holder.sendEmail.setVisibility(View.VISIBLE);
            holder.sendSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSendSmsClicked(itemsFiltered.size() - position - 1);
                }
            });

            holder.sendWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSendWhatsAppClicked(itemsFiltered.size() - position - 1);
                }
            });

            holder.sendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSendEmailClicked(itemsFiltered.size() - position - 1);
                }
            });

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(position);
                }
            });
        }

        /*holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index=itemsFiltered.size() - position - 1;
                notifyDataSetChanged();
            }
        });
        if(row_index==itemsFiltered.size() - position - 1){
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#D9E7F3"));
            //holder.relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        else
        {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            //holder.tv1.setTextColor(Color.parseColor("#000000"));
        }*/

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
                    List<TemplateApp>  filteredList = new ArrayList<>();
                    for (TemplateApp row : itemList) {

                        if (row.getDescription().toLowerCase().contains(charString.toLowerCase())
                                || row.getName().contains(charString)) {

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
                itemsFiltered = (ArrayList<TemplateApp> ) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        assert data != null;
    }


    public interface RecyclerViewAdapterListener {
        void onValueChanged(float amount, String category);

        void onItemRemoved(TemplateApp item);

        void onItemUpdated(float amount, int count);

        void onSendClicked(int position);

        void onSendSmsClicked(int position);

        void onSendWhatsAppClicked(int position);

        void onSendEmailClicked(int position);

        void onItemClick(int position);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView description, name;
        public ImageView sendImage, sendSms, sendWhatsApp, sendEmail;
        private RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);

            description = view.findViewById(R.id.description);
            name = view.findViewById(R.id.name);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            sendImage = view.findViewById(R.id.sendImage);
            sendSms = view.findViewById(R.id.sendSms);
            sendWhatsApp = view.findViewById(R.id.sendWhatsApp);
            sendEmail = view.findViewById(R.id.sendEmail);


        }
    }
}
