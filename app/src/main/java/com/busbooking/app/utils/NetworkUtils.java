package com.busbooking.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import retrofit2.Response;
import java.io.IOException;

/**
 * Network utility class for handling network operations and errors
 */
public class NetworkUtils {

    /**
     * Check if device is connected to the internet
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * Show network error toast
     */
    public static void showNetworkError(Context context) {
        if (context != null) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show general error toast
     */
    public static void showError(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get error message from response
     */
    public static String getErrorMessage(Response<?> response) {
        if (response == null) {
            return "Unknown error occurred";
        }

        switch (response.code()) {
            case 400:
                return "Bad request. Please check your input.";
            case 401:
                return "Session expired. Please login again.";
            case 403:
                return "Access denied.";
            case 404:
                return "Resource not found.";
            case 409:
                return "Conflict. The resource already exists.";
            case 422:
                return "Validation error. Please check your input.";
            case 500:
                return "Server error. Please try again later.";
            case 503:
                return "Service unavailable. Please try again later.";
            default:
                return "Error: " + response.message();
        }
    }

    /**
     * Get error message from throwable
     */
    public static String getErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error occurred";
        }

        if (throwable instanceof IOException) {
            return "Network error. Please check your connection.";
        } else if (throwable instanceof SecurityException) {
            return "Security error. Please check permissions.";
        } else {
            String message = throwable.getMessage();
            return message != null ? message : "Unknown error occurred";
        }
    }

    /**
     * Check if error is network-related
     */
    public static boolean isNetworkError(Throwable throwable) {
        return throwable instanceof IOException;
    }

    /**
     * Check if error is authentication-related
     */
    public static boolean isAuthError(Response<?> response) {
        return response != null && (response.code() == 401 || response.code() == 403);
    }
}
