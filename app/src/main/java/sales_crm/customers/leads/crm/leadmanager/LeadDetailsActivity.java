package sales_crm.customers.leads.crm.leadmanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import sales_crm.customers.leads.crm.leadmanager.leads.DescriptionBottomSheet;
import sales_crm.customers.leads.crm.leadmanager.leads.FollowUpBottomSheet;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;
import sales_crm.customers.leads.crm.leadmanager.models.LeadApp;
import sales_crm.customers.leads.crm.leadmanager.templates.ChooseTemplateBottomSheet;
import sales_crm.customers.leads.crm.leadmanager.templates.TemplateBottomSheet;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;

public class LeadDetailsActivity extends AppCompatActivity implements FollowUpBottomSheet.NotifyParent {

    private CardView status, followUp, deals, notes, history;
    private RelativeLayout call, sms, whatsApp, email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private Contact contactGlobal;
    private ProgressDialog progress;
    private FollowUpBottomSheet.NotifyParent context = this;
    private LeadApp lead;

    private TextView statusText, followUpText, dealsText, notesText, contactName, contactImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead_details);



        /*if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new
                    NotificationChannel( "leadManager" , "lead manager" , importance) ;
            notificationChannel.setDescription("lead manager meeting");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel) ;
        }*/



        ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 20) {
                            statusText.setText(result.getData().getStringExtra("status"));
                            lead.setStatus(result.getData().getStringExtra("status"));
                        }
                        // Do your code from onActivityResult
                    }
                });

        progress = new ProgressDialog(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

        lead = (LeadApp) getIntent().getSerializableExtra("lead");
        getContact(lead.getContactUid());

        statusText = findViewById(R.id.tvStatus);
        followUpText = findViewById(R.id.tvFollowup);
        dealsText = findViewById(R.id.tvDeals);
        notesText = findViewById(R.id.tvNotes);

        contactName = findViewById(R.id.contactName);
        contactImage = findViewById(R.id.contactImage);

        statusText.setText(lead.getStatus());

        if (lead.getLatestFollowup() == 0) {
            followUpText.setText("No followups yet");
        } else {
            java.util.Date d = new java.util.Date(lead.getLatestFollowup() * 1000L);
            String itemDateStr = new SimpleDateFormat("E, dd MMM hh:mm a").format(d);
            followUpText.setText(itemDateStr);
        }
        if (lead.getNotes() != null)
            notesText.setText(lead.getNotes()[lead.getNotes().length - 1].getDescription());
        if (lead.getDeals() != null)
            dealsText.setText(lead.getDeals()[lead.getDeals().length - 1].getDescription());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        status = findViewById(R.id.statusCard);
        followUp = findViewById(R.id.followUpCard);
        deals = findViewById(R.id.dealsCard);
        notes = findViewById(R.id.notesCard);
        history = findViewById(R.id.historyCard);

        call = findViewById(R.id.call);
        sms = findViewById(R.id.sms);
        whatsApp = findViewById(R.id.whatsApp);
        email = findViewById(R.id.email);

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, StatusActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                intent.putExtra("status", lead.getStatus());

                mLauncher.launch(intent);

                //startActivity(intent);
            }
        });

        followUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("lead_uid", lead.getUid());
                bundle.putLong("latestFollowUp", lead.getLatestFollowup());
                ////Log.v("ifhrgbuergv", lead.getLfd());
                bundle.putString("lfd", lead.getLfd());
                FollowUpBottomSheet followUpBottomSheet = new FollowUpBottomSheet(context);
                followUpBottomSheet.setArguments(bundle);
                followUpBottomSheet.show(getSupportFragmentManager(), DescriptionBottomSheet.TAG);

                //Intent intent = new Intent(LeadDetailsActivity.this, ListActivity.class);
                //intent.putExtra("category", "new_lead");
                //startActivity(intent);
            }
        });

        deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, HistoryActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                intent.putExtra("category", "deals");
                startActivity(intent);
            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, HistoryActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                intent.putExtra("category", "notes");
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeadDetailsActivity.this, HistoryActivity.class);
                intent.putExtra("lead_uid", lead.getUid());
                intent.putExtra("category", "history");
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactGlobal.getPhone()));
                startActivity(intent);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseTemplateBottomSheet templateBottomSheet = new ChooseTemplateBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("contact", contactGlobal.getPhone());
                bundle.putString("category", "sms");
                templateBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getSupportFragmentManager(), TemplateBottomSheet.TAG);
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contactGlobal.getPhone(), null)));
            }
        });

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChooseTemplateBottomSheet templateBottomSheet = new ChooseTemplateBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("contact", contactGlobal.getPhone());
                bundle.putString("category", "whatsapp");
                templateBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getSupportFragmentManager(), TemplateBottomSheet.TAG);

                /*Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                //whatsappIntent.putExtra(Intent.EXTRA_STREAM, generateBill(getContext(), billingDetails));
                whatsappIntent.setType("text/plain");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hi *" + contactGlobal.getName() + "*");

                whatsappIntent.putExtra("jid", contactGlobal.getPhone() + "@s.whatsapp.net");
                whatsappIntent.setPackage("com.whatsapp");
                //whatsappIntent.setType("application/pdf");
                try {
                    startActivity(whatsappIntent);
                    //startActivity(Intent.createChooser(whatsappIntent, ""));
                } catch (android.content.ActivityNotFoundException ex) {
                    // ToastHelper.MakeShortText("Whatsapp have not been installed.");
                }*/
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, contactGlobal.getEmail());
                //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }*/
                ChooseTemplateBottomSheet templateBottomSheet = new ChooseTemplateBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("contact", contactGlobal.getEmail());
                bundle.putString("category", "email");
                templateBottomSheet.setArguments(bundle);
                templateBottomSheet.show(getSupportFragmentManager(), TemplateBottomSheet.TAG);
            }
        });

    }

    private void getContact(String uid) {

        DocumentReference dataRef = db.collection("cache")
                .document(user.getUid())
                .collection("contacts").document(uid);
        //.collection("purchases");


        Source CACHE = Source.CACHE;
        Query.Direction DESCENDING = Query.Direction.DESCENDING;

        dataRef.get(CACHE).addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.getResult().exists()) {
                //Log.v("dipakUID", "uid:" + task.getResult().getData());
                Gson gson = new Gson();
                JsonElement jsonElement = gson.toJsonTree(task.getResult().getData());
                Contact contact = gson.fromJson(jsonElement, Contact.class);
                contactGlobal = contact;
                contactName.setText(contact.getName());
                contactImage.setText(contact.getName().substring(0, 1).toUpperCase());

            }
        });

    }

    @Override
    public void notifyAdded(String itemDateStr, String lfd, long epoch) {
        followUpText.setText(itemDateStr);
        lead.setLfd(lfd);
        //createNotification(1000);

        //scheduleNotification(getNotification( "10 second delay" ) , 10000 ) ;
        createNotification(epoch, lfd);

        /*OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(workRequest);*/


    }

    public void createNotification (long time, String lfd) {

        Intent myIntent = new Intent(this, NotificationEventService.class);
        myIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        //myIntent.putExtra("epoch", time);
        myIntent.putExtra("lfd", lfd);
        myIntent.putExtra("name", contactGlobal.getName());
        myIntent.putExtra("number", contactGlobal.getPhone());
        myIntent.putExtra("email", contactGlobal.getEmail());
        myIntent.putExtra("uid", lead.getUid());

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( getApplicationContext(), (int) System.currentTimeMillis()/1000 , myIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ((time-19000) * 1000), pendingIntent);

        Toast.makeText(LeadDetailsActivity.this, "scheduled meeting", Toast.LENGTH_SHORT)
                .show();

    }

    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( this, NotificationEventService.class ) ;
        notificationIntent.putExtra(NotificationEventService.NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(NotificationEventService.NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }

    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, "lead_manager_app" ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( "lead_manager_app" ) ;
        return builder.build() ;
    }

}