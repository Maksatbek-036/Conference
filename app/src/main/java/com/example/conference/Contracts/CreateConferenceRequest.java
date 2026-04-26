package com.example.conference.Contracts;

public class CreateConferenceRequest {
    private  String startTime;
    private  String endTime;
    private String title;
    private String description;
    private long date; // timestamp
    private String location;
    private boolean isOnline;

    public CreateConferenceRequest(String title, String description,
                                   long date, String startTime, String endTime,
                                   String location, boolean isOnline) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.isOnline = isOnline;
    }

}
