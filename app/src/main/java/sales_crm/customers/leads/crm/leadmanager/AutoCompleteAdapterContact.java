package sales_crm.customers.leads.crm.leadmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapterContact extends ArrayAdapter<Contact> {
    private List<Contact> allContactsList;
    public List<Contact> filteredContactsList;

    public AutoCompleteAdapterContact(@NonNull Context context, @NonNull List<Contact> placesList) {
        super(context, 0, placesList);

        allContactsList = new ArrayList<>(placesList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return placeFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.contact_item, parent, false
            );
        }

        TextView contactName = convertView.findViewById(R.id.contactName);
        TextView contactPhone = convertView.findViewById(R.id.phone);
        TextView contactAddress = convertView.findViewById(R.id.contactAddress);
        TextView contactEmail = convertView.findViewById(R.id.email);

        Contact contact = getItem(position);
        if (contact != null) {
            contactName.setText(contact.getName());
            contactPhone.setText(contact.getPhone());
            contactAddress.setText(contact.getAddress());
            contactEmail.setText(contact.getEmail());
        }

        return convertView;
    }

    private Filter placeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            filteredContactsList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredContactsList.addAll(allContactsList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Contact contact: allContactsList) {
                    if (contact.getName().toLowerCase().contains(filterPattern)) {
                        filteredContactsList.add(contact);
                    }
                }
            }

            results.values = filteredContactsList;
            results.count = filteredContactsList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Contact) resultValue).getName();
        }
    };
}
