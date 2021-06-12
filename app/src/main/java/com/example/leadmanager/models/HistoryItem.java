package com.example.leadmanager.models;

import java.io.Serializable;

public class HistoryItem implements Serializable {
    private String description;
    private Long date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
