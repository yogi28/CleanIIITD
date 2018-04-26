package com.example.spark.cleaniiitd.pojo;

import java.util.ArrayList;
import java.util.HashMap;

public class Job {
    private String id;
    private String washroomId;
    private String supervisorId;
    private long timestamp;
    private int slot;
    private HashMap<String, Boolean> checklist;
    private ArrayList<String> images;

    public Job() {
    }

    public Job(String id, String washroomId, String supervisorId, long timestamp, int slot, HashMap<String, Boolean> checklist, ArrayList<String> images) {
        this.id = id;
        this.washroomId = washroomId;
        this.supervisorId = supervisorId;
        this.timestamp = timestamp;
        this.slot = slot;
        this.checklist = checklist;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWashroomId() {
        return washroomId;
    }

    public void setWashroomId(String washroomId) {
        this.washroomId = washroomId;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public HashMap<String, Boolean> getChecklist() {
        return checklist;
    }

    public void setChecklist(HashMap<String, Boolean> checklist) {
        this.checklist = checklist;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
