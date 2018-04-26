package com.example.spark.cleaniiitd.pojo;

import java.util.ArrayList;
import java.util.HashSet;

public class Supervisor {
    private String id;
    private String emailId;
    private String name;
    private ArrayList<String> deviceTokens;
    private ArrayList<String> jobIds;

    public Supervisor() {
        this.deviceTokens = new ArrayList<>();
        this.jobIds = new ArrayList<>();
    }

    public Supervisor(String id, String emailId, String name, ArrayList<String> deviceTokens) {
        this.id = id;
        this.emailId = emailId;
        this.name = name;
        this.deviceTokens = deviceTokens;
    }

    public Supervisor(String id, String emailId, String name, String deviceToken) {
        this.id = id;
        this.emailId = emailId;
        this.name = name;
        if (this.deviceTokens == null) {
            this.deviceTokens = new ArrayList<>();
        }
        this.deviceTokens.add(deviceToken);
    }

    public void addJob(String jobId) {
        if (jobIds == null) {
            jobIds = new ArrayList<>();
        }
        jobIds.add(jobId);
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDeviceTokens() {
        return deviceTokens;
    }

    public void setDeviceTokens(ArrayList<String> deviceTokens) {
        this.deviceTokens = deviceTokens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
