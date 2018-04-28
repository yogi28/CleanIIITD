package com.example.spark.cleaniiitd;

import android.app.Application;
import android.util.Log;

import com.example.spark.cleaniiitd.pojo.Supervisor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CleanWiseApp extends Application {

    private static final String TAG = CleanWiseApp.class.getSimpleName();
    public static CleanWiseApp instance;
    public static FirebaseDatabase firebaseDbInstance;
    public static Supervisor supervisor;


    synchronized public static CleanWiseApp getInstance() {
        if (instance == null) {
            instance = new CleanWiseApp();
        }
        return instance;
    }

    synchronized public FirebaseDatabase getFirebaseDatabaseInstance() {
        if (firebaseDbInstance == null) {
            firebaseDbInstance = FirebaseDatabase.getInstance();
            firebaseDbInstance.setPersistenceEnabled(true);
        }
        return firebaseDbInstance;
    }

    synchronized public Supervisor getAppUser(final Supervisor s) {
        if (s != null) {
            Log.d(TAG, s.getJobIds().toString());
            DatabaseReference ref = getFirebaseDatabaseInstance().getReference("supervisors");
            supervisor = s;
            ref.child(s.getId()).setValue(s);
        } else if (supervisor == null) {
            Log.d(TAG, "Turning out to be null for both objects");
            supervisor = new Supervisor();
        } else {
            Log.d(TAG, "Else here? " + supervisor.getEmailId() + supervisor.getId());
        }
        return supervisor;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        getFirebaseDatabaseInstance();
        instance = this;
        FirebaseApp.initializeApp(this);
//        FirebaseMessaging.getInstance().subscribeToTopic("events");
    }
}
