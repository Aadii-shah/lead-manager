package sales_crm.customers.leads.crm.leadmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.billing.InAppPurchase;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "ProfileBottomSheet";
    private String store_name, store_owner;

    public static ProfileBottomSheet newInstance() {
        return new ProfileBottomSheet();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_bottomsheet, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        TextView signOut = view.findViewById(R.id.signOut);

        TextView manageSubscription = view.findViewById(R.id.manageSubscription);
        manageSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), InAppPurchase.class);
                startActivity(i);
                //getActivity().finish();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        Glide.with(this)
                // .load(item.getThumbnail())
                .load(user.getPhotoUrl())
                .circleCrop()
                .into(imageView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Bundle mArgs = getArguments();
        //store_name = mArgs.getString("store_name");
        //store_owner = mArgs.getString("store_owner");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        signIn();
    }

    private void signIn() {
        Intent i = new Intent(getContext(), Login.class);
        startActivity(i);
        getActivity().finish();
    }

}
