package com.busbooking.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.busbooking.app.models.api.*;
import com.busbooking.app.repository.BusRepository;
import java.util.List;

public class BusViewModel extends ViewModel {

    private final MutableLiveData<List<ScheduleData>> schedules = new MutableLiveData<>();
    private final MutableLiveData<List<RouteData>> popularRoutes = new MutableLiveData<>();
    private final MutableLiveData<List<SeatData>> seats = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final BusRepository busRepository;

    public BusViewModel() {
        busRepository = BusRepository.getInstance();
    }

    // LiveData getters
    public LiveData<List<ScheduleData>> getSchedules() { return schedules; }
    public LiveData<List<RouteData>> getPopularRoutes() { return popularRoutes; }
    public LiveData<List<SeatData>> getSeats() { return seats; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // Search buses
    public void searchBuses(String source, String destination, String date) {
        isLoading.setValue(true);
        busRepository.searchBuses(source, destination, date, new BusRepository.BusSearchCallback() {
            @Override
            public void onSuccess(List<ScheduleData> data) {
                isLoading.postValue(false);
                schedules.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Load popular routes
    public void loadPopularRoutes() {
        isLoading.setValue(true);
        busRepository.getPopularRoutes(new BusRepository.PopularRoutesCallback() {
            @Override
            public void onSuccess(List<RouteData> data) {
                isLoading.postValue(false);
                popularRoutes.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Load seats for schedule
    public void loadSeats(String scheduleId) {
        isLoading.setValue(true);
        busRepository.getSeats(scheduleId, new BusRepository.SeatsCallback() {
            @Override
            public void onSuccess(List<SeatData> data) {
                isLoading.postValue(false);
                seats.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Block seats
    public void blockSeats(String scheduleId, List<Integer> seatNumbers, BlockSeatsCallback callback) {
        isLoading.setValue(true);
        busRepository.blockSeats(scheduleId, seatNumbers, new BusRepository.BlockSeatsCallback() {
            @Override
            public void onSuccess(BlockSeatsData data) {
                isLoading.postValue(false);
                if (callback != null) callback.onSuccess(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
                if (callback != null) callback.onError(error);
            }
        });
    }

    // Clear error
    public void clearError() {
        errorMessage.setValue(null);
    }

    // Callback interface
    public interface BlockSeatsCallback {
        void onSuccess(BlockSeatsData data);
        void onError(String error);
    }
}
