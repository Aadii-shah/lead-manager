package com.example.leadmanager.adapters;

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

import com.example.leadmanager.R;
import com.example.leadmanager.models.Contact;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

public class AutoCompleteAdapterContact extends ArrayAdapter<Contact> {
    private List<Contact> allSuppliersList;
    public List<Contact> filteredSuppliersList;

    public AutoCompleteAdapterContact(@NonNull Context context, @NonNull List<Contact> placesList) {
        super(context, 0, placesList);

        allSuppliersList = new ArrayList<>(placesList);
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

        TextView supplierName = convertView.findViewById(R.id.contactName);
        TextView supplierPhone = convertView.findViewById(R.id.phone);

        Contact supplier = getItem(position);
        if (supplier != null) {
            supplierName.setText(supplier.getName());
            supplierPhone.setText(supplier.getPhone());
        }

        return convertView;
    }

    private Filter placeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            filteredSuppliersList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredSuppliersList.addAll(allSuppliersList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Contact supplier: allSuppliersList) {
                    Log.v("dipak", supplier.getName() + "");
                    if (supplier.getName().toLowerCase().contains(filterPattern)) {
                        filteredSuppliersList.add(supplier);
                    }
                }
            }

            results.values = filteredSuppliersList;
            results.count = filteredSuppliersList.size();

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
