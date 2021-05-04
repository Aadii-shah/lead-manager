package com.example.leadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Objects;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 134;
    private ProgressDialog progress;
    private TextView loginInstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progress = new ProgressDialog(this);

        loginInstruction = findViewById(R.id.loginInstruction);

        TextView privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://docs.google.com/document/d/1ILSOKTIRSIAKIsD8USTc3od-xmYC-9jizG7YDxoGIs4/view?usp=sharing";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso;
        //if (BuildConfig.DEBUG) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("647810365000-8lav1ecl9k7ph0h2mkdt9gnn07o25lki.apps.googleusercontent.com")
                .build();
        // do something for a debug build
        /*} else {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                    .requestEmail().requestIdToken("977994608505-skj2v7t3v9al20c2vk0gpjdr7jjlnq0a.apps.googleusercontent.com")
                    .build();
        }*/

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInButton signInButton = findViewById(R.id.googleSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount account) {


                    progress.setMessage("Setting up your space");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            });

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        //Log.v("cred1", store);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // To dismiss the dialog
                            progress.dismiss();
                            Intent i = new Intent(Login.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            signInAgain();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void signInAgain() {
//        Toast.makeText(Login.this, "Something went wrong:(\nPlease SignIn again",
        //             Toast.LENGTH_LONG).show();
        if(progress!=null && progress.isShowing())
            progress.dismiss();
        loginInstruction.setVisibility(View.GONE);
    }
}