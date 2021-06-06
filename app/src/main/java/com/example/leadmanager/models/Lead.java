package com.example.leadmanager.models;

public class Lead {
    String status, source, description;

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLatestFollowup() {
        return latestFollowup;
    }

    public void setLatestFollowup(long latestFollowup) {
        this.latestFollowup = latestFollowup;
    }

    public String[] getNotes() {
        return notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }

    public String[] getDeals() {
        return deals;
    }

    public void setDeals(String[] deals) {
        this.deals = deals;
    }

    public String[] getHistory() {
        return history;
    }

    public void setHistory(String[] history) {
        this.history = history;
    }

    private long creationDate;
    private long latestFollowup;
    private String[] notes;
    private String[] deals;
    private String[] history;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
