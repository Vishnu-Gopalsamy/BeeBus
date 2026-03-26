package com.busbooking.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.busbooking.app.models.api.*;
import com.busbooking.app.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<AuthData> authData = new MutableLiveData<>();
    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);

    private final AuthRepository authRepository;

    public AuthViewModel() {
        authRepository = AuthRepository.getInstance();
    }

    // LiveData getters
    public LiveData<AuthData> getAuthData() { return authData; }
    public LiveData<UserData> getUserData() { return userData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsLoggedIn() { return isLoggedIn; }

    // Login
    public void login(String email, String password) {
        isLoading.setValue(true);
        LoginRequest request = new LoginRequest(email, password);
        
        authRepository.login(request, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthData data) {
                isLoading.postValue(false);
                authData.postValue(data);
                isLoggedIn.postValue(true);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Register
    public void register(String name, String email, String phone, String password) {
        isLoading.setValue(true);
        RegisterRequest request = new RegisterRequest(name, email, phone, password);
        
        authRepository.register(request, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthData data) {
                isLoading.postValue(false);
                authData.postValue(data);
                isLoggedIn.postValue(true);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Get Profile
    public void getProfile() {
        isLoading.setValue(true);
        authRepository.getProfile(new AuthRepository.ProfileCallback() {
            @Override
            public void onSuccess(UserData data) {
                isLoading.postValue(false);
                userData.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Logout
    public void logout() {
        authRepository.logout(new AuthRepository.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                isLoggedIn.setValue(false);
                authData.setValue(null);
                userData.setValue(null);
            }

            @Override
            public void onError(String error) {
                // Still clear locally
                isLoggedIn.setValue(false);
                authData.setValue(null);
                userData.setValue(null);
            }
        });
    }

    // Clear error
    public void clearError() {
        errorMessage.setValue(null);
    }
}
