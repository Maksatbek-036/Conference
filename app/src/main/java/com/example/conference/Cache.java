package com.example.conference;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.conference.Models.JWTPayload;

public class Cache {
    private final SharedPreferences sharedPreferences;

    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String AVATAR_URL = "avatar_url";

    public Cache(Context context) {
        sharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN, null);
    }

    // Сохраняем данные пользователя из Payload
    public void saveUserInfo(JWTPayload payload) {
        sharedPreferences.edit()
                .putString(USER_ID, payload.getId())
                .putString(USER_NAME, payload.getName())
                .putString(USER_EMAIL, payload.getEmail())
                .putString(AVATAR_URL, payload.getAvatarUrl())
                .apply();
    }

    // Методы для получения отдельных полей
    public String getUserId() {
        return sharedPreferences.getString(USER_ID, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(USER_EMAIL, null);
    }
    public String getAvatarUrl() {
        return sharedPreferences.getString(AVATAR_URL, null);
    }


    // Очистка кэша при выходе (Logout)
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}