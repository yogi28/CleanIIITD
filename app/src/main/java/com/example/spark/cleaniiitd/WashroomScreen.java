package com.example.spark.cleaniiitd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WashroomScreen extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washroom_screen);
        Intent intentFromMainActivity = getIntent();
        String scannedContent = intentFromMainActivity.getStringExtra("washroomId");
        TextView washroomId = (TextView) findViewById(R.id.washroomId);
        washroomId.setText(scannedContent);

        final Button superviseMorningButton = (Button) findViewById(R.id.superviseMorningButton);
        superviseMorningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callCheckListIntent = new Intent(WashroomScreen.this, CheckList.class);
                callCheckListIntent.putExtra("superviseTime", superviseMorningButton.getText());
                startActivity(callCheckListIntent);
            }
        });

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String onlyDate = dateFormat.format(date);

        TextView setDate = findViewById(R.id.currentDate);
        setDate.setText("Date : " + onlyDate);

    }
}
