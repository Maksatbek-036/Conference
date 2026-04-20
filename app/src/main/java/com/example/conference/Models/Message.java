package com.example.conference.Models;

public class Message {
    private String id;
    private String content;
    private String timestamp;
    private String userId;
    private String conferenceId;

    public Message(String id, String content, String timestamp, String userId, String conferenceId) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
        this.conferenceId = conferenceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }
}

