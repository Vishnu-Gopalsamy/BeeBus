package com.busbooking.app.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // -----------------------------------------------------------------------------------
    // NETWORK CONFIGURATION
    // -----------------------------------------------------------------------------------
    // 1. For Physical Device: Change the IP below to your computer's CURRENT WiFi IP
    // 2. For Emulator: Use http://10.0.2.2:3000/api/
    // 3. For USB Debugging: Use http://127.0.0.1:3000/api/ AND run 'adb reverse tcp:3000 tcp:3000'
    // -----------------------------------------------------------------------------------
    
    // Use 10.0.2.2 for Android Emulator (loopback to host machine)
    // For physical device: change to your computer's WiFi IP or use ADB reverse
    private static final String BASE_URL = "http://10.42.130.158:3000/api/";

    private static Retrofit retrofit = null;
    private static String authToken = null;

    public static Retrofit getClient() {
        return getClient(authToken);
    }

    public static Retrofit getClient(String token) {
        if (retrofit == null || (token != null && !token.equals(authToken))) {
            authToken = token;

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json");

                    if (authToken != null && !authToken.isEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer " + authToken);
                    }

                    return chain.proceed(requestBuilder.build());
                }
            };

            // Retry interceptor for transient failures (timeouts, connection resets)
            Interceptor retryInterceptor = new Interceptor() {
                private static final int MAX_RETRIES = 3;
                private static final long RETRY_DELAY_MS = 1000;

                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    IOException lastException = null;

                    for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
                        try {
                            return chain.proceed(request);
                        } catch (IOException e) {
                            lastException = e;
                            if (attempt < MAX_RETRIES - 1) {
                                try {
                                    Thread.sleep(RETRY_DELAY_MS * (attempt + 1));
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    throw e;
                                }
                            }
                        }
                    }
                    throw lastException;
                }
            };

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(retryInterceptor)
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void setAuthToken(String token) {
        authToken = token;
        retrofit = null;
    }

    public static void clearAuthToken() {
        authToken = null;
        retrofit = null;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
