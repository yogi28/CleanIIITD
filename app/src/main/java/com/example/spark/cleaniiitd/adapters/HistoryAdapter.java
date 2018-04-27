package com.example.spark.cleaniiitd.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spark.cleaniiitd.R;
import com.example.spark.cleaniiitd.pojo.Job;
import com.example.spark.cleaniiitd.utilities.Utilities;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryVH> {

    private ArrayList<Job> jobs;
    private Context mContext;
    private static final String TAG = HistoryAdapter.class.getSimpleName();

    public HistoryAdapter(Context mContext) {
        this.jobs = new ArrayList<>();
        this.mContext = mContext;
    }

    public HistoryAdapter(Context mContext, ArrayList<Job> jobs) {
        this.jobs = jobs;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HistoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.job_list_item, parent, false);
        return new HistoryVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryVH holder, int position) {
        Job j = jobs.get(position);
        holder.washroomIdView.setText(j.getWashroomId());
        holder.slotView.setText(Utilities.getTimeSlot(j.getSlot()));
        holder.dateView.setText(Utilities.getDateString(j.getTimestamp()));
        holder.setListener(j);
    }

    public void updateJobs(ArrayList<Job> newJobs) {
        jobs = newJobs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    static class HistoryVH extends RecyclerView.ViewHolder {
        TextView washroomIdView, slotView, dateView;
        Job job;

        HistoryVH(View itemView) {
            super(itemView);
            washroomIdView = itemView.findViewById(R.id.washroom_id);
            slotView = itemView.findViewById(R.id.slot);
            dateView = itemView.findViewById(R.id.date);
        }

        void setListener(Job j) {
            this.job = j;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Item Clicked " + job.getId());
                }
            });
        }
    }
}
