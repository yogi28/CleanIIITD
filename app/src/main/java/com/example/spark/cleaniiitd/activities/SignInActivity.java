package com.example.spark.cleaniiitd.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spark.cleaniiitd.CleanWiseApp;
import com.example.spark.cleaniiitd.R;
import com.example.spark.cleaniiitd.pojo.Supervisor;
import com.example.spark.cleaniiitd.utilities.Utilities;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = SignInActivity.class.getSimpleName();
    SignInButton signInButton;
    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 9221;
    private GoogleSignInClient mGoogleSignInClient;
    private CleanWiseApp application;
    private ArrayList<String> whitelistSupervisors;
    private DatabaseReference mWhitelistRef;
    private ConnectivityManager cm;
    private boolean isConnected;
    private boolean hasClicked = false;
    private boolean isSync = false;
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        signInButton = findViewById(R.id.googleBtn);
        application = CleanWiseApp.getInstance();

        mWhitelistRef = application.getFirebaseDatabaseInstance().getReference("whitelist").child("supervisors");
        mWhitelistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                whitelistSupervisors = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    whitelistSupervisors.add(ds.getValue(String.class));
                }
                isSync = true;
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .setHostedDomain("iiitd.ac.in")
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected && isSync) {
                    hasClicked = true;
                    signIn();
                } else if (isConnected) {
                    Toast.makeText(SignInActivity.this, "Syncing... Please wait for a few moments...", Toast.LENGTH_SHORT).show();
                    showProgressDialog("Syncing... Please wait for a few moments...");
                } else
                    Toast.makeText(SignInActivity.this, "Please connect to internet...", Toast.LENGTH_SHORT).show();
            }
        });
        setGooglePlusButtonText(signInButton, "Sign in with IIITD account");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            signInButton.setEnabled(false);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignInActivity.this);
            alertDialogBuilder.setTitle("Internet Connection Required")
                    .setMessage("Please enable wifi or mobile data to sign-in.")
                    .setPositiveButton(R.string.connect_wifi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
        } else {
            signInButton.setEnabled(true);
        }
    }

    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, "sign in intent" + signInIntent.toString());
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        showProgressDialog(getString(R.string.loading));
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    public void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
//        finish();
    }


    public void updateUI(FirebaseUser currentUser) {
        hideProgressDialog();
        if (currentUser != null) {
            boolean access = false;
            String msg;
//            Toast.makeText(this, "Email: " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
            if (currentUser.getEmail() != null && currentUser.getEmail().endsWith("@iiitd.ac.in")) {

                msg = "User: " + currentUser.getDisplayName() + "\nEmail: " + currentUser.getEmail()
                        + "\nAccess: Granted";

                Supervisor s = new Supervisor(Utilities.getUserKey(currentUser.getEmail()), currentUser.getEmail(), currentUser.getDisplayName(), FirebaseInstanceId.getInstance().getToken());

                if ((whitelistSupervisors != null && whitelistSupervisors.contains(s.getEmailId())) || !hasClicked) {
                    access = true;
                    application.getAppUser(s);
                    Log.d(TAG, s.getId() + " is present and " + application.getAppUser(null).getId() + " also present");
                }
            } else
                msg = "User: " + currentUser.getDisplayName() + "\nEmail: " + currentUser.getEmail()
                        + "\nAccess: Denied";

//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            if (access) {
                Intent intent = new Intent(SignInActivity.this, ScanQRActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Sign-in with Supervisor Account at IIITD", Toast.LENGTH_SHORT).show();
                signOut();
            }
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }


}