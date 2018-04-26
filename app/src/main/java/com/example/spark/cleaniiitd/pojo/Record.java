package com.example.spark.cleaniiitd.pojo;


import com.example.spark.cleaniiitd.utilities.Utilities;

public class Record {
    private String jobId;
    private String supervisorId;
    private Utilities.Status status;

    public Record(String jobId, String supervisorId, Utilities.Status status) {
        this.jobId = jobId;
        this.supervisorId = supervisorId;
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public Utilities.Status getStatus() {
        return status;
    }

    public void setStatus(Utilities.Status status) {
        this.status = status;
    }

    public Record() {

    }
}
