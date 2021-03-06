package com.example.spark.cleaniiitd;

//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.auth.api.signin;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ScanQR extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnQRCodeReadListener {

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
    SignIn signInActivity = new SignIn();
    FirebaseUser user;


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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_qr);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mainLayout = (ViewGroup) findViewById(R.id.main_layout);

//        mAuth = FirebaseAuth.getInstance();



//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (firebaseAuth.getCurrentUser() != null) {
//                    Intent intent = new Intent(SignIn.this, ScanQR.class);
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
//                    startActivity(new Intent(ScanQR.this, SignIn.class));
//                }
//            }
//        };





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
    }

    @Override protected void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override protected void onPause() {
        super.onPause();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
            }mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent toWashroomScreenIntent = new Intent(ScanQR.this, SignIn.class);
                    startActivity(toWashroomScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }
        };

    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override public void onQRCodeRead(String text, PointF[] points) {
        pointsOverlayView.setPoints(points);
        Intent toWashroomScreenIntent = new Intent(ScanQR.this, WashroomScreen.class);
        toWashroomScreenIntent.putExtra("washroomId", text);
        startActivity(toWashroomScreenIntent);

    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override public void onClick(View view) {
                ActivityCompat.requestPermissions(ScanQR.this, new String[] {
                        Manifest.permission.CAMERA
                }, MY_PERMISSION_REQUEST_CAMERA);
            }
          }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void initQRCodeReaderView() {

        View content = getLayoutInflater().inflate(R.layout.scan_qr_screen, mainLayout, true);

        qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
        resultTextView = (TextView) content.findViewById(R.id.result_text_view);
        flashlightCheckBox = (CheckBox) content.findViewById(R.id.flashlight_checkbox);
        historyButton = (Button) findViewById(R.id.scan_screen_button);
//        logOutButton = (Button) findViewById(R.id.log_out);
        pointsOverlayView = (ShowPoints) content.findViewById(R.id.points_overlay_view);

        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            qrCodeReaderView.setTorchEnabled(isChecked);
        }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toHistoryActivityIntent = new Intent(ScanQR.this, History.class);
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

    public void signOut(){
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        signInActivity.updateUI(null);
                        finish();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }


//
//    private void updateUI(FirebaseUser user) {
////        hideProgressDialog();
//        Toast.makeText(this, "hahahahah", Toast.LENGTH_SHORT).show();
//    }

}