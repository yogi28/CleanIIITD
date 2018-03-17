package com.example.spark.cleaniiitd.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.spark.cleaniiitd.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WashroomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washroom_screen);
        Intent intentFromMainActivity = getIntent();
        final String scannedContent = intentFromMainActivity.getStringExtra("washroomId");
        TextView washroomId = (TextView) findViewById(R.id.washroomId);
        washroomId.setText(scannedContent);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String onlyDate = dateFormat.format(date);

        final Button superviseMorningButton = (Button) findViewById(R.id.superviseMorningButton);
        superviseMorningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callCheckListIntent = new Intent(WashroomActivity.this, CheckListActivity.class);
                callCheckListIntent.putExtra("superviseTime", superviseMorningButton.getText());
                callCheckListIntent.putExtra("washroom_id", scannedContent);
                callCheckListIntent.putExtra("date", onlyDate);
                callCheckListIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(callCheckListIntent);
            }
        });
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
}
