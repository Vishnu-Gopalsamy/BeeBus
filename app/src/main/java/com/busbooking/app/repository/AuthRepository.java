package com.busbooking.app.repository;

import com.busbooking.app.api.ApiClient;
import com.busbooking.app.api.ApiService;
import com.busbooking.app.models.api.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private ApiService apiService;
    private static AuthRepository instance;

    private AuthRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    private void updateApiService() {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
    }

    // Register user
    public void register(RegisterRequest request, AuthCallback callback) {
        updateApiService();
        Call<ApiResponse<AuthData>> call = apiService.register(request);

        call.enqueue(new Callback<ApiResponse<AuthData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthData>> call, Response<ApiResponse<AuthData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Save token for future requests
                        ApiClient.setAuthToken(apiResponse.getData().getToken());
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Login user
    public void login(LoginRequest request, AuthCallback callback) {
        updateApiService();
        Call<ApiResponse<AuthData>> call = apiService.login(request);

        call.enqueue(new Callback<ApiResponse<AuthData>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthData>> call, Response<ApiResponse<AuthData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Save token for future requests
                        ApiClient.setAuthToken(apiResponse.getData().getToken());
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get user profile
    public void getProfile(ProfileCallback callback) {
        updateApiService();
        Call<ApiResponse<UserData>> call = apiService.getProfile();

        call.enqueue(new Callback<ApiResponse<UserData>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserData>> call, Response<ApiResponse<UserData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else if (response.code() == 401) {
                    callback.onError("Session expired. Please login again.");
                } else {
                    callback.onError("Failed to get profile: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Update profile
    public void updateProfile(UpdateProfileRequest request, ProfileCallback callback) {
        updateApiService();
        Call<ApiResponse<UserData>> call = apiService.updateProfile(request);

        call.enqueue(new Callback<ApiResponse<UserData>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserData>> call, Response<ApiResponse<UserData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to update profile: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Change password
    public void changePassword(ChangePasswordRequest request, SimpleCallback callback) {
        updateApiService();
        Call<ApiResponse<Void>> call = apiService.changePassword(request);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getMessage());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to change password: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Logout
    public void logout(SimpleCallback callback) {
        updateApiService();
        Call<ApiResponse<Void>> call = apiService.logout();

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Clear auth token regardless of response
                ApiClient.clearAuthToken();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    callback.onSuccess(apiResponse.getMessage());
                } else {
                    callback.onSuccess("Logged out successfully");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Clear auth token even on failure
                ApiClient.clearAuthToken();
                callback.onSuccess("Logged out successfully");
            }
        });
    }

    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(AuthData authData);
        void onError(String error);
    }

    public interface ProfileCallback {
        void onSuccess(UserData userData);
        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
