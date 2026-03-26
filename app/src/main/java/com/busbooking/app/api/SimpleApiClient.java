package com.busbooking.app.api;

// Temporary simplified API client for testing
public class SimpleApiClient {
    private static final String BASE_URL = "http://10.0.2.2:5000/api/";
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void clearAuthToken() {
        authToken = null;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
