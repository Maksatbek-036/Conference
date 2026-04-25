package com.example.conference.Contracts;

import com.example.conference.Models.Participant;

import java.util.List;

public class ConferenceResponce {
    private String id;
    private String titles;
    private String description;
    private long date; // timestamp
    private String location;
    private boolean isOnline;

    public ConferenceResponce(String id, String titles, String description, long date, String location, boolean isOnline, List<Participant> participants) {
        this.id = id;
        this.titles = titles;
        this.description = description;
        this.date = date;
        this.location = location;
        this.isOnline = isOnline;

        this.participants = participants;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    private List<Participant> participants;

}
