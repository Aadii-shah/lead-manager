package sales_crm.customers.leads.crm.leadmanager;

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

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {
    public List<Contact> itemsFiltered;
    private Context context;
    private ContactsAdapter.RecyclerViewAdapterListener listener;
    public List<Contact> itemList;

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
    public void onBindViewHolder(ContactsAdapter.MyViewHolder holder, final int position) {
        final Contact item = itemsFiltered.get(itemsFiltered.size() - position - 1);

        holder.name.setText(item.getName());
        holder.address.setText(item.getAddress());
        holder.phone.setText(item.getPhone());
        holder.email.setText(item.getEmail());
        /*java.util.Date d = new java.util.Date(item.getCreationDate()*1000L);
        String itemDateStr = new SimpleDateFormat("dd-MMM-YYYY HH:mm").format(d);
        holder.time.setText(itemDateStr);*/

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

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, address, email, phone;
        public ImageView imageUrl, plus, minus, remove;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.contactName);
            address = view.findViewById(R.id.contactAddress);
            phone = view.findViewById(R.id.phone);
            email = view.findViewById(R.id.email);


        }
    }
}
