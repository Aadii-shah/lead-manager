package com.example.leadmanager.models;

import java.io.Serializable;

public class DealItem implements Serializable {

    private String description;
    private long Date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }
}
