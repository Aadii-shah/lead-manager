package sales_crm.customers.leads.crm.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import sales_crm.customers.leads.crm.leadmanager.R;
import sales_crm.customers.leads.crm.leadmanager.billing.InAppPurchase;
import sales_crm.customers.leads.crm.leadmanager.models.Contact;
import sales_crm.customers.leads.crm.leadmanager.models.ContactDetails;
import sales_crm.customers.leads.crm.leadmanager.models.HistoryItem;
import sales_crm.customers.leads.crm.leadmanager.models.Lead;
import sales_crm.customers.leads.crm.leadmanager.models.Pro;

import static sales_crm.customers.leads.crm.leadmanager.Utility.getCurrentTime;

public class ImportCSV extends AppCompatActivity {

    private TextView csvName;
    private Uri uri = null;
    private ProgressDialog progress;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_c_s_v);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView downloadSCSV = findViewById(R.id.downloadSampleCSV);
        downloadSCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadSampleCSV();
            }
        });

        progress = new ProgressDialog(this);

        csvName = findViewById(R.id.fileName);

        TextView choose = findViewById(R.id.chooser);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        TextView importButton = findViewById(R.id.importButton);

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (uri != null) {

                    progress.setMessage("adding new lead");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();

                    InputStream input = null;
                    try {
                        input = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    CSVReader reader = new CSVReader(new InputStreamReader(input));

                    try {
                        if(reader.readNext()!=null) {
                            String[] nextLine;
                            while ((nextLine = reader.readNext()) != null) {
                                // nextLine[] is an array of values from the line

                                Contact contact = new Contact();

                                contact.setName(nextLine[0]);
                                contact.setAddress(nextLine[1]);
                                contact.setPhone(nextLine[2]);
                                contact.setEmail(nextLine[3]);

                                Lead lead = new Lead();
                                lead.setCreationDate(getCurrentTime());
                                lead.setStatus(nextLine[4]);
                                lead.setSource(nextLine[5]);
                                lead.setDescription(nextLine[6]);



                                db.collection("cache")
                                        .document(user.getUid()).collection("account").document("pro")
                                        .get(Source.CACHE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Gson gson = new Gson();
                                        JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());

                                        Pro pro = gson.fromJson(jsonElement, Pro.class);
                                        int count = pro.getCount();

                                        if(count <= 100) {
                                            db.collection("cache")
                                                    .document(user.getUid()).collection("account").document("pro")
                                                    .update("count", count +1);
                                            addNewLead(contact, lead);
                                        } else if(pro.getValidTill()>(System.currentTimeMillis()/1000)) {
                                            addNewLead(contact, lead);
                                        } else {
                                            //Subscription Message
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if (!isFinishing()){
                                                        new AlertDialog.Builder(ImportCSV.this)
                                                                .setTitle("Get Premium")
                                                                .setMessage("You have reached free 100 contact limit.")
                                                                .setCancelable(false)
                                                                .setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent i = new Intent(ImportCSV.this, InAppPurchase.class);
                                                                        startActivity(i);
                                                                        finish();
                                                                    }
                                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                //finish();
                                                            }
                                                        }).show();
                                                    }
                                                }
                                            });
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v("dipak", "failed" + e.toString());

                                        Pro pro = new Pro();
                                        pro.setCount(1);
                                        pro.setValidTill(0);

                                        db.collection("cache")
                                                .document(user.getUid()).collection("account").document("pro")
                                                .set(pro);
                                        addNewLead(contact, lead);


                                    }
                                });
                                //Log.d("error-csv", nextLine[0]  + "   " + nextLine[1] + "   " + nextLine[2] + "   " + nextLine[3] + "   " + nextLine[4] + "   " + nextLine[5] + "   " + nextLine[6] + "etc...");
                            }

                        }

                        progress.dismiss();
                        finish();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No CSV file selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Log.d("error-csv", "File Path: " + "test");
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();
                    //Log.d("error-csv", "File Uri: " + uri.toString());
                    String[] splits = uri.toString().split("/");
                    csvName.setText("File name: " + splits[splits.length - 1]);

                    /*InputStream input = null;
                    try {
                        input = this.getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }



                    CSVReader reader = new CSVReader(new InputStreamReader(input));

                    try {
                        String[] nextLine;
                        while ((nextLine = reader.readNext()) != null) {
                            // nextLine[] is an array of values from the line
                            //Log.d("error-csv", nextLine[0] + nextLine[1] + "etc...");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    // Get the path
                    String path = null;
                    try {
                        path = getPath(this, uri);
                    } catch (URISyntaxException e) {
                        //e.printStackTrace();
                        //Log.d("error-csv", "File Path: " + e);
                    }

                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimetypes = {"text/csv", "text/comma-separated-values", "application/csv"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void addNewLead(Contact contact, Lead lead) {

        DocumentReference documentReference = db.collection("cache").document(user.getUid()).collection("contacts").document();
        documentReference.set(contact);
        //.set(contact);

        lead.setContactUid(documentReference.getId());

        documentReference = db.collection("cache").document(user.getUid())
                //.collection("contacts").document(documentReference.getId())
                .collection("leads").document();

        documentReference.set(lead);


        HistoryItem historyItem = new HistoryItem();
        historyItem.setDescription("Created");
        historyItem.setDate(Utility.getCurrentTime());

        db.collection("cache").document(user.getUid())
                //.collection("contacts").document(contact.getUid())
                .collection("leads")
                .document(documentReference.getId())
                .update("history", FieldValue.arrayUnion(historyItem));
    }

    private void downloadSampleCSV() {
        String url = "https://docs.google.com/spreadsheets/d/19ixd-b_DKXCi-jVh5KeiKdIKlI8oF6gJE-p7EKqPjJQ/export?format=csv";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}