package com.busbooking.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.busbooking.app.api.ApiClient;
import com.busbooking.app.api.ApiService;
import com.busbooking.app.models.api.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class OwnerViewModel extends ViewModel {
    private final MutableLiveData<List<BusData>> busList = new MutableLiveData<>();
    private final MutableLiveData<List<BookingData>> ownerBookings = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private ApiService apiService;

    public OwnerViewModel() {
        updateApiService();
    }

    private void updateApiService() {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
    }

    // LiveData getters
    public LiveData<List<BusData>> getBusList() { return busList; }
    public LiveData<List<BookingData>> getOwnerBookings() { return ownerBookings; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void clearError() { errorMessage.setValue(null); }

    // Load owner's buses
    public void loadOwnerBuses() {
        isLoading.setValue(true);
        updateApiService();
        apiService.getOwnerBuses().enqueue(new Callback<ApiResponse<BusListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusListData>> call, Response<ApiResponse<BusListData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    busList.postValue(response.body().getData().getBuses());
                } else {
                    errorMessage.postValue("Failed to load your buses");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusListData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Load owner's bookings
    public void loadOwnerBookings() {
        isLoading.setValue(true);
        updateApiService();
        apiService.getOwnerBookings().enqueue(new Callback<ApiResponse<BookingListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingListData>> call, Response<ApiResponse<BookingListData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ownerBookings.postValue(response.body().getData().getBookings());
                } else {
                    errorMessage.postValue("Failed to load your bookings");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingListData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }
}
