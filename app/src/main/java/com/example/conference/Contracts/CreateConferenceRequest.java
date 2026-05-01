package com.example.conference.Contracts;

import com.google.gson.annotations.SerializedName;

public class CreateConferenceRequest {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("date")
    private String date; // timestamp
    @SerializedName("startTime")
    private  String startTime;
    @SerializedName("endTime")
    private  String endTime;
    @SerializedName("location")
    private String location;
    @SerializedName("isOnline")
    private boolean isOnline;

    public CreateConferenceRequest(String title, String description,
                                   String date, String startTime, String endTime,
                                   String location, boolean isOnline) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.isOnline = isOnline;
    }

    public static CreateConferenceRequest Create(String title, String description, String date, String startTime, String endTime, String location, boolean isOnline) {
    return new CreateConferenceRequest(title, description, date, startTime, endTime, location, isOnline);
    }
}
