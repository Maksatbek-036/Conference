package com.example.conference.Contracts;

import com.example.conference.Models.Participant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConferenceResponce {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("location")
    private String location;
    @SerializedName("code")
    private String code;

    public ConferenceResponce(String id, String titles, String description, String date, String startTime, String endTime, String location) {
        this.id = id;
        this.titles = titles;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}