package com.example.conference.Models;

import java.text.SimpleDateFormat;

public class Conference {
    private String id;
    private String titles;
    private String description;
    private long date; // timestamp
    private String location;
    private boolean isOnline;

    public String getId() {
        return id;
    }

    public String getTitles() {
        return titles;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(date);
    }

    public String getLocation() {
        return location;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public Conference(String id, String titles, String description, long date, String location, boolean isOnline) {
        this.id = id;
        this.titles = titles;
        this.description = description;
        this.date = date;
        this.location = location;
        this.isOnline = isOnline;
    }
}