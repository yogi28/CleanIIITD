package com.example.spark.cleaniiitd.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.spark.cleaniiitd.CleanWiseApp;
import com.example.spark.cleaniiitd.R;
import com.example.spark.cleaniiitd.adapters.HistoryAdapter;
import com.example.spark.cleaniiitd.pojo.Job;
import com.example.spark.cleaniiitd.pojo.Supervisor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = HistoryActivity.class.getSimpleName();
    private TextView mEmptyTextView;
    private RecyclerView mHistoryList;

    private HistoryAdapter mAdapter;

    private CleanWiseApp application;
    private DatabaseReference mJobRef;
    private Supervisor supervisor;
    private ArrayList<Job> jobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = CleanWiseApp.getInstance();
        mJobRef = application.getFirebaseDatabaseInstance().getReference("jobs");
        supervisor = application.getAppUser(null);
        jobs = new ArrayList<>();

        mHistoryList = findViewById(R.id.history_list);
        mEmptyTextView = findViewById(R.id.history_text);
        mAdapter = new HistoryAdapter(this, jobs);
        mHistoryList.setLayoutManager(new LinearLayoutManager(this));
        mHistoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mHistoryList.setAdapter(mAdapter);

        mJobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Job job = ds.getValue(Job.class);

                        if (job != null && job.getSupervisorId() != null && supervisor != null) {
                            Log.d(TAG, supervisor.getId() + " : " + job.getSupervisorId());
                            if (job.getSupervisorId().equals(supervisor.getId()))
                                jobs.add(job);
                        }
                        if (mAdapter != null && jobs.size() > 0) {
                            mEmptyTextView.setVisibility(View.GONE);
                            mHistoryList.setVisibility(View.VISIBLE);
                            mAdapter.updateJobs(jobs);
                        } else {
                            mEmptyTextView.setVisibility(View.VISIBLE);
                            mHistoryList.setVisibility(View.GONE);
                        }
                    }
                    if (jobs.size() == 0) {
                        mHistoryList.setVisibility(View.GONE);
                        mEmptyTextView.setVisibility(View.VISIBLE);
                    } else {
                        mHistoryList.setVisibility(View.VISIBLE);
                        mEmptyTextView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
