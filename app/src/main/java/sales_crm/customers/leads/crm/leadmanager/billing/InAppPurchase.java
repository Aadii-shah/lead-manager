package sales_crm.customers.leads.crm.leadmanager.billing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.RecyclerViewTouchListener;
import sales_crm.customers.leads.crm.leadmanager.models.Pro;

public class InAppPurchase extends AppCompatActivity {
    private ProPlanAdapter proPlanAdapter;
    private RecyclerView recyclerView;
    private TextView subscriptionTypeText, dateView, free_trial;
    LinearLayout subscriptionLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private long date = 0;

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {

            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchase);

        Toolbar toolbar = findViewById(R.id.showPlanToolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //progress.dismiss();
            }
        });

        recyclerView = findViewById(R.id.plans);

        subscriptionTypeText = findViewById(R.id.subscriptionTypeText);
        dateView = findViewById(R.id.date);
        free_trial = findViewById(R.id.free_trial);

        subscriptionLayout = findViewById(R.id.plansLayout);

        SharedPreferences prefs = getSharedPreferences("date", MODE_PRIVATE);

        db.collection("cache")
                .document(user.getUid()).collection("account").document("pro")
                .get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //long time = (long) documentSnapshot.get("validTill");
                Gson gson = new Gson();
                JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());

                Pro pro = gson.fromJson(jsonElement, Pro.class);
                Log.v("dipak", pro.getCount() + "");
                date = pro.getValidTill();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        //boolean isFreeTrial = prefs.getBoolean("isFreeTrial", true);
        //long date = prefs.getLong("subD", 0);


        /*if (!isFreeTrial) {
            subscriptionTypeText.setText("Next renews on: ");
            free_trial.setVisibility(View.GONE);
        } else {
            free_trial.setVisibility(View.VISIBLE);
        }*/

        if (date < System.currentTimeMillis() / 1000)
            subscriptionTypeText.setText("You don't have any subscription");
        else {
            subscriptionTypeText.setText("Subscription valid till: ");


            java.util.Date d = new java.util.Date(date * 1000L);
            String itemDateStr = new SimpleDateFormat("dd-MMM-YYYY HH:mm").format(d);
            dateView.setText(itemDateStr);

        }


        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
//        Log.v("ddd", "hhhh" + purchasesResult.getPurchasesList().get(0));

        startConnection();

    }

    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.


                    List<String> skuList = new ArrayList<>();
                    skuList.add("leadmanager_annual");
                    skuList.add("leadmanager_monthly");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {

                                    //Log.v("dipak", "" + skuDetailsList.size());

                                    proPlanAdapter = new ProPlanAdapter(InAppPurchase.this, skuDetailsList);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InAppPurchase.this);
                                    recyclerView.setLayoutManager(mLayoutManager);

                                    recyclerView.setAdapter(proPlanAdapter);
                                    recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(InAppPurchase.this, recyclerView, new RecyclerViewTouchListener.ClickListener() {
                                        @Override
                                        public void onClick(View view, int position) {
                                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                    .setSkuDetails(skuDetailsList.get(position))
                                                    .build();
                                            int responseCode = billingClient.launchBillingFlow(InAppPurchase.this, billingFlowParams).getResponseCode();
                                        }

                                        @Override
                                        public void onLongClick(View view, int position) {

                                        }
                                    }));

                                }
                            });


                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
            finish();
        }
    };

    private void handlePurchase(Purchase purchase) {

        long date = 0;
        if (purchase.getSku().equals("paperboats_monthly")) {
            date = (purchase.getPurchaseTime() / 1000) + 86400 * 30;
        } else {
            date = (purchase.getPurchaseTime() / 1000) + 86400 * 365;
        }

        db.collection("cache")
                .document(user.getUid()).collection("account").document("pro").update("validTill", date);

        //mDatabase.setValue(date);

        SharedPreferences.Editor editor = getSharedPreferences("date", MODE_PRIVATE).edit();
        editor.putLong("subD", date);
        editor.putBoolean("isFreeTrial", false);
        editor.apply();


        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            //fetchPurchaseDetails(purchase.getSku(), purchase.getPurchaseToken());
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    private void fetchPurchaseDetails(String subscriptionId, String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/inventory.stock.management.invoice.billing.paperboats/purchases/subscriptions/" + subscriptionId + "/tokens/" + token;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //textView.setText("Response: " + response.toString());

                        /*DatabaseReference mDatabase;

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("cache")
                                .child(Objects.requireNonNull(decrypt(store_owner.replaceAll("%2E", "/"), originalKey).replaceAll("\\.", "%2E")))
                                .child(store_name.replaceAll(" ", "**"))
                                .child("proDetails");*/

                        //mDatabase.push().setValue()

                        // SharedPreferences.Editor editor = getSharedPreferences("date", MODE_PRIVATE).edit();
                        //editor.putLong("subD", date);
                        //editor.apply();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.v("erroryyy", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                //headers.put("Authorization", "Bearer " + "");
                return headers;
            }
        };


// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}