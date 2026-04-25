package com.example.conference;

import android.content.Context;
import android.content.SharedPreferences;

public class Cache {
    SharedPreferences sharedPreferences;
    public static final String TOKEN = "token";

    public Cache(Context context) {
        sharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
    }
    public void saveToken(String token) {
        sharedPreferences.edit().putString(TOKEN, token).apply();
    }
    public String getToken() {
        return sharedPreferences.getString(TOKEN, null);
    }
}
