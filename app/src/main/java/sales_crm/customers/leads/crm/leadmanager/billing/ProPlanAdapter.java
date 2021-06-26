package sales_crm.customers.leads.crm.leadmanager.billing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.SkuDetails;

import java.util.ArrayList;
import java.util.List;

import sales_crm.customers.leads.crm.leadmanager.R;

public class ProPlanAdapter extends RecyclerView.Adapter<ProPlanAdapter.MyViewHolder> implements Filterable {
    private Context context;
    public List<SkuDetails> itemsFiltered;
    private ProPlanAdapter.RecyclerViewAdapterListener listener;
    private List<SkuDetails> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, price;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.Title);
            price = view.findViewById(R.id.price);
        }
    }


    public ProPlanAdapter(Context context, List<SkuDetails> itemList) {
        //this.listener = recyclerViewAdapterListener;
        this.context = context;
        this.itemList = itemList;
        this.itemsFiltered = itemList;
    }

    @Override
    public ProPlanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pro_plan, parent, false);

        return new ProPlanAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ProPlanAdapter.MyViewHolder holder, final int position) {
        final SkuDetails item = itemsFiltered.get(position);

        holder.title.setText(item.getDescription());
        holder.price.setText(item.getPrice());
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
                    List<SkuDetails> filteredList = new ArrayList<>();
                    for (SkuDetails row : itemList) {

                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())
                                || row.getPrice().contains(charSequence)) {

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
                itemsFiltered = (ArrayList<SkuDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface RecyclerViewAdapterListener {
        void onItemSelected(int position);

    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        assert data != null;
    }
}
