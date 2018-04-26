package com.example.spark.cleaniiitd.activities;

//import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spark.cleaniiitd.CleanWiseApp;
import com.example.spark.cleaniiitd.R;
import com.example.spark.cleaniiitd.ShowPoints;
import com.example.spark.cleaniiitd.pojo.Supervisor;
import com.example.spark.cleaniiitd.pojo.Washroom;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class ScanQRActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnQRCodeReadListener {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    private ViewGroup mainLayout;
    private Button logOutButton;
    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    private CheckBox flashlightCheckBox;
    protected Button historyButton;
    private ShowPoints pointsOverlayView;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    SignInActivity signInActivityActivity = new SignInActivity();
    FirebaseUser user;
    private CleanWiseApp application;

    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_qr);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mainLayout = (ViewGroup) findViewById(R.id.main_layout);

        resultTextView = (TextView) findViewById(R.id.result_text_view);

        historyButton = (Button) findViewById(R.id.scan_screen_button);

        application = CleanWiseApp.getInstance();

//        logOutButton = (Button) findViewById(R.id.log_out);


//        mAuth = FirebaseAuth.getInstance();


//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (firebaseAuth.getCurrentUser() != null) {
//                    Intent intent = new Intent(SignInActivity.this, ScanQRActivity.class);
//                    intent.putExtra("account_name", account_name);
//                    startActivity(intent);
//                }
//            }
//        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (firebaseAuth.getCurrentUser() == null){
//                    startActivity(new Intent(ScanQRActivity.this, SignInActivity.class));
//                }
//            }
//        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
//        super.onStart();
        if (user != null) {
//            userDetailsText.setText("User: " + user.getDisplayName() + "\nEmail: " + user.getEmail() + "\nAccess: Granted");
            //user.getPhotoUrl()
            resultTextView.setText(user.getDisplayName());
//            userNameText.setText);
//            userEmailText.setText(user.getEmail());
//            mAuth.addAuthStateListener(mAuthListener);

        } else {
//            userDetailsText.setText("Not Logged In!");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent toWashroomScreenIntent = new Intent(ScanQRActivity.this, SignInActivity.class);
                    startActivity(toWashroomScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        };

    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        pointsOverlayView.setPoints(points);
        Intent toWashroomScreenIntent = new Intent(ScanQRActivity.this, WashroomActivity.class);
        toWashroomScreenIntent.putExtra("washroomId", text);
        toWashroomScreenIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(toWashroomScreenIntent);

    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(ScanQRActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void initQRCodeReaderView() {

//        View content = getLayoutInflater().inflate(R.layout.activity_scan_qr, mainLayout, true);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        pointsOverlayView = (ShowPoints) findViewById(R.id.points_overlay_view);
        flashlightCheckBox = (CheckBox) findViewById(R.id.flashlight_checkbox);

        qrCodeReaderView.setAutofocusInterval(1000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                qrCodeReaderView.setTorchEnabled(isChecked);
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toHistoryActivityIntent = new Intent(ScanQRActivity.this, HistoryActivity.class);
                startActivity(toHistoryActivityIntent);
            }
        });

//        logOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signOut();
//            }
//        });


        qrCodeReaderView.startCamera();
    }

    public void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        signInActivityActivity.updateUI(null);
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                signOut();
                return true;
            case R.id.action_insert:
                ArrayList<Washroom> washrooms = new ArrayList<>();
                washrooms.add(new Washroom("OBH1-F0-W1", "Old Boys Hostel", 0));
                washrooms.add(new Washroom("OBH1-F0-W2", "Old Boys Hostel", 0));
                washrooms.add(new Washroom("OBH1-F0-W3", "Old Boys Hostel", 0));
                washrooms.add(new Washroom("OBH1-F1-W1", "Old Boys Hostel", 1));
                washrooms.add(new Washroom("OBH1-F1-W2", "Old Boys Hostel", 1));
                washrooms.add(new Washroom("OBH1-F1-W3", "Old Boys Hostel", 1));
                washrooms.add(new Washroom("OBH1-F2-W1", "Old Boys Hostel", 2));
                washrooms.add(new Washroom("OBH1-F2-W2", "Old Boys Hostel", 2));
                washrooms.add(new Washroom("OBH1-F2-W3", "Old Boys Hostel", 2));
                DatabaseReference washroomReference = application.getFirebaseDatabaseInstance().getReference("washrooms");
                for (Washroom w : washrooms) {
                    washroomReference.child(w.getId()).setValue(w);
                }

                ArrayList<Supervisor> supervisors = new ArrayList<>();
                supervisors.add(new Supervisor("vaibhav17065","vaibhav17065@iiitd.ac.in", "Vaibhav Varshney", FirebaseInstanceId.getInstance().getToken()));
                supervisors.add(new Supervisor("yogesh17071","yogesh17071@iiitd.ac.in", "Yogesh IIITD", FirebaseInstanceId.getInstance().getToken()));
                supervisors.add(new Supervisor("shubhi17057","shubhi17057@iiitd.ac.in", "Shubhi Tiwari", FirebaseInstanceId.getInstance().getToken()));
                supervisors.add(new Supervisor("rajshree17045","rajshree17045@iiitd.ac.in", "Rajshree Khare", FirebaseInstanceId.getInstance().getToken()));
                supervisors.add(new Supervisor("chirag17010","chirag17010@iiitd.ac.in", "Chirag Khurana", FirebaseInstanceId.getInstance().getToken()));
                DatabaseReference supervisorReference = application.getFirebaseDatabaseInstance().getReference("supervisors");
                for (Supervisor s: supervisors) {
                    supervisorReference.child(s.getId()).setValue(s);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}