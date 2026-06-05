package com.example.conference.Api;

import com.example.conference.Cache; // Убедитесь, что импорт верный
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
    private static final String BASE_URL = "http://185.255.132.217:5000/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // BODY покажет и куки, и данные

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        // 1. Получаем токен из Cache (нужен контекст или статический доступ)
                        // Если Cache требует Context, его нужно передать или инициализировать заранее
                        String token = Cache.getInstance().getToken();

                        if (token != null && !token.isEmpty()) {
                            // 2. Устанавливаем стандартный заголовок Authorization
                            builder.addHeader("Authorization", "Bearer " + token);

                            // 3. Устанавливаем ТОКЕН В КУКИ
                            // Судя по вашим логам сервера, кука называется "nigger"
                            builder.addHeader("Cookie", "nigger=" + token);


                        }

                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static <T> T getApi(Class<T> serviceClass) {
        return getClient().create(serviceClass);
    }
}