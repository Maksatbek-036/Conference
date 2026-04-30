package com.example.conference.Contracts;

public class SendMessageRequest {
    private String conferenceId;
    private String text;

    public SendMessageRequest(String conferenceId, String text) {
        this.conferenceId = conferenceId;
        this.text = text;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
