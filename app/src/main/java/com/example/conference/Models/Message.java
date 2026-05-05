package com.example.conference.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;



public class Message  {

    public String id;

   // Важно: С большой буквы, как на сервере
    public String content;


    public String timestamp;


    public String userId;


    public String conferenceId;



    public static Message create(String messageContent, String userId, String currentRoomId) {
        Message message = new Message();
        message.content = messageContent;
        message.userId = userId;
        message.conferenceId = currentRoomId;
        return message;
    }

    // Геттеры и сеттеры обязательны для корректной работы GSON в некоторых конфигурациях
    public String getContent() { return content; }
    public String getUserId() { return userId; }
    public String getConferenceId() { return conferenceId; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }
}