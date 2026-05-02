package com.example.conference;

import android.util.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;

public class JWTDecoder {

    private static final String TAG = "JWTDecoder";

    /**
     * Декодирует Payload (полезную нагрузку) JWT токена.
     * @param jwtToken Полный JWT токен (header.payload.signature)
     * @return JSON строка с данными или null при ошибке
     */
    public static String decodedPayload(String jwtToken) {
        try {
            // JWT состоит из 3 частей, разделенных точками. Нам нужна вторая (индекс 1)
            String[] parts = jwtToken.split("\\.");
            if (parts.length < 2) {
                Log.e(TAG, "Некорректный JWT токен");
                return null;
            }

            String payload = parts[1];
            // Используем URL_SAFE, так как JWT может содержать спецсимволы
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            return new String(decodedBytes, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка декодирования: " + e.getMessage());
            return null;
        }
    }
}
