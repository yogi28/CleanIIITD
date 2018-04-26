package com.example.spark.cleaniiitd.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.spark.cleaniiitd.CleanWiseApp;
import com.example.spark.cleaniiitd.R;
import com.example.spark.cleaniiitd.pojo.Record;
import com.example.spark.cleaniiitd.utilities.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WashroomActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = WashroomActivity.class.getSimpleName();
    private Button superviseMorningButton, superviseNoonButton, superviseEveningButton;
    private Button[] superviseButtons;
    private String washroomId;
    private CleanWiseApp application;
    private DatabaseReference mRecordRef;
    private String onlyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washroom_screen);
        Intent intentFromMainActivity = getIntent();
        washroomId = intentFromMainActivity.getStringExtra("washroomId");
        TextView washroomIdView = findViewById(R.id.washroomId);
        washroomIdView.setText(washroomId);

        application = CleanWiseApp.getInstance();
        mRecordRef = application.getFirebaseDatabaseInstance().getReference("records");

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        onlyDate = dateFormat.format(date);

        superviseMorningButton = findViewById(R.id.superviseMorningButton);
        superviseNoonButton = findViewById(R.id.superviseNoonButton);
        superviseEveningButton = findViewById(R.id.superviseEveningButton);

        superviseButtons = new Button[]{superviseMorningButton, superviseNoonButton, superviseEveningButton};

        mRecordRef.child(onlyDate).child(washroomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        try {
                            int slot = Integer.parseInt(ds.getKey());
                            switch (ds.getValue(Record.class).getStatus()) {
                                case DONE:
                                    superviseButtons[slot].setBackgroundResource(R.drawable.rounded_button_green);
                                    break;
                                case LATE:
                                    superviseButtons[slot].setBackgroundColor(Color.RED);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        superviseMorningButton.setOnClickListener(this);
        superviseNoonButton.setOnClickListener(this);
        superviseEveningButton.setOnClickListener(this);

        TextView setDate = findViewById(R.id.currentDate);
        setDate.setText(onlyDate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Intent callCheckListIntent = new Intent(WashroomActivity.this, CheckListActivity.class);
        callCheckListIntent.putExtra("superviseTime", superviseMorningButton.getText());
        callCheckListIntent.putExtra("washroom_id", washroomId);
        callCheckListIntent.putExtra("date", onlyDate);
        callCheckListIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        switch (view.getId()) {
            case R.id.superviseMorningButton:
                callCheckListIntent.putExtra("slot", 1);
                startActivity(callCheckListIntent);
                break;
            case R.id.superviseNoonButton:
                callCheckListIntent.putExtra("slot", 2);
                startActivity(callCheckListIntent);
                break;
            case R.id.superviseEveningButton:
                callCheckListIntent.putExtra("slot", 3);
                startActivity(callCheckListIntent);
                break;
        }
    }
}
