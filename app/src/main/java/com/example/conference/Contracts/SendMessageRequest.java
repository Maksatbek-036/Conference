package com.example.conference.Contracts;

import com.google.gson.annotations.SerializedName;

public class SendMessageRequest {
    @SerializedName("conferenceId")
    private String conferenceId;
    
    @SerializedName("text")
    private String text;

    @SerializedName("content")
    private String content;

    public SendMessageRequest(String conferenceId, String text) {
        this.conferenceId = conferenceId;
        this.text = text;
        this.content = text;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
