package com.example.conference.Api;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Используем ваш IP (убедитесь, что сервер доступен по нему)
    private static final String BASE_URL = "http://192.168.0.106:5000/";
    private static Retrofit retrofit;
    private static String authToken; // Статическое поле для хранения токена

    // Метод для обновления токена после логина
    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Настройка управления куками (автоматически сохраняет и отправляет 'nice')
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            // 2. Логирование (полезно для отладки, покажет отправляется ли кука и заголовок)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            // 3. Создание OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager)) // Обработка кук
                    .addInterceptor(chain -> {
                        Request.Builder builder = chain.request().newBuilder();

                        // Если токен установлен, добавляем его в каждый запрос
                        if (authToken != null && !authToken.isEmpty()) {
                            builder.addHeader("Authorization", "Bearer " + authToken);
                        }

                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();

            // 4. Сборка Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Привязываем наш настроенный клиент
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static <T> T getApi(Class<T> serviceClass) {
        return getClient().create(serviceClass);
    }
}