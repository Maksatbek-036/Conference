package com.example.conference.Models;

import com.google.gson.annotations.SerializedName;

public class JWTPayload {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("exp")
    private long exp; // Время истечения в формате Unix Timestamp

    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAvatarUrl() { return avatarUrl; }
    public long getExp() { return exp; }

    // Удобная проверка: не протух ли токен
    public boolean isExpired() {
        return (System.currentTimeMillis() / 1000) > exp;
    }
}