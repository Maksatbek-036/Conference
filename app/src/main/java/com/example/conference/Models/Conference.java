package com.example.conference.Models;

import com.google.gson.annotations.SerializedName;
public class Conference {
    @SerializedName("id") // Проверьте, Id или id на бэкенде
    private String id;

    @SerializedName("title") // Скорее всего на бэкенде 'title', а не 'titles'
    private String titles;

    @SerializedName("description")
    private String description;

    @SerializedName("date")
    private String date; // Мы уже договорились, что это String
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("endTime")
    private String endTime;

    @SerializedName("location")
    private String location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @SerializedName("isOnline")
    private boolean isOnline;

    // ... геттеры и сеттеры
}
