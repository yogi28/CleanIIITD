package com.example.spark.cleaniiitd;

import android.app.Application;
import android.util.Log;

import com.example.spark.cleaniiitd.pojo.Supervisor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class CleanWiseApp extends Application {

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
            getFirebaseDatabaseInstance().getReference("supervisors").child(s.getId()).setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    supervisor = s;
                }
            });
        } else if (supervisor == null)
            supervisor = new Supervisor();
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
