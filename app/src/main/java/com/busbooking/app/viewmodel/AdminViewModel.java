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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminViewModel extends ViewModel {
    private final MutableLiveData<List<BusData>> busList = new MutableLiveData<>();
    private final MutableLiveData<List<RouteData>> routeList = new MutableLiveData<>();
    private final MutableLiveData<List<BookingData>> allBookings = new MutableLiveData<>();
    private final MutableLiveData<List<ScheduleData>> scheduleList = new MutableLiveData<>();
    private final MutableLiveData<DashboardStats> dashboardStats = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private ApiService apiService;

    public AdminViewModel() {
        updateApiService();
    }

    private void updateApiService() {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
    }

    // LiveData getters
    public LiveData<List<BusData>> getBusList() { return busList; }
    public LiveData<List<RouteData>> getRouteList() { return routeList; }
    public LiveData<List<BookingData>> getAllBookings() { return allBookings; }
    public LiveData<List<ScheduleData>> getSchedules() { return scheduleList; }
    public LiveData<DashboardStats> getDashboardStats() { return dashboardStats; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void clearError() { errorMessage.setValue(null); }
    public void clearSuccess() { successMessage.setValue(null); }

    // Load all buses
    public void loadAllBuses() {
        isLoading.setValue(true);
        updateApiService();
        apiService.getAllBuses().enqueue(new Callback<ApiResponse<BusListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusListData>> call, Response<ApiResponse<BusListData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    busList.postValue(response.body().getData().getBuses());
                } else {
                    errorMessage.postValue("Failed to load buses");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusListData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

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
                    errorMessage.postValue("Failed to load owner buses");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusListData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Load all routes
    public void loadAllRoutes() {
        isLoading.setValue(true);
        updateApiService();
        apiService.getAllRoutes().enqueue(new Callback<ApiResponse<RouteListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<RouteListData>> call, Response<ApiResponse<RouteListData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    routeList.postValue(response.body().getData().getRoutes());
                } else {
                    errorMessage.postValue("Failed to load routes");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RouteListData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Load all schedules
    public void loadAllSchedules() {
        isLoading.setValue(true);
        updateApiService();
        apiService.getAllSchedules().enqueue(new Callback<ApiResponse<ScheduleListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<ScheduleListData>> call, Response<ApiResponse<ScheduleListData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    scheduleList.postValue(response.body().getData().getSchedules());
                } else {
                    errorMessage.postValue("Failed to load schedules");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ScheduleListData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Load dashboard stats
    public void loadDashboardStats() {
        updateApiService();
        apiService.getDashboardStats().enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call, Response<ApiResponse<DashboardStats>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    dashboardStats.postValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                // Silent fail for stats
            }
        });
    }

    // Load all bookings
    public void loadAllBookings() {
        isLoading.setValue(true);
        updateApiService();
        apiService.getAllBookings().enqueue(new Callback<ApiResponse<BookingListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingListData>> call, Response<ApiResponse<BookingListData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allBookings.postValue(response.body().getData().getBookings());
                } else {
                    errorMessage.postValue("Failed to load bookings");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingListData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Add new bus
    public void addBus(String busName, String busNumber, String busType, String operatorName,
                       int totalSeats, List<String> amenities,
                       String seatLayout, int seaterSeats, int sleeperLowerSeats,
                       int sleeperUpperSeats, String berthType) {
        isLoading.setValue(true);
        updateApiService();

        Map<String, Object> busData = new HashMap<>();
        busData.put("busName", busName);
        busData.put("busNumber", busNumber);
        busData.put("busType", busType);
        busData.put("operatorName", operatorName);
        busData.put("totalSeats", totalSeats);
        busData.put("amenities", amenities);

        Map<String, Object> seatConfig = new HashMap<>();
        seatConfig.put("layout", seatLayout);
        seatConfig.put("seaterSeats", seaterSeats);
        seatConfig.put("sleeperLowerSeats", sleeperLowerSeats);
        seatConfig.put("sleeperUpperSeats", sleeperUpperSeats);
        seatConfig.put("berthType", berthType);
        busData.put("seatConfig", seatConfig);

        apiService.addBus(busData).enqueue(new Callback<ApiResponse<BusData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusData>> call, Response<ApiResponse<BusData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    successMessage.postValue("Bus added successfully!");
                } else {
                    errorMessage.postValue("Failed to add bus");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Update existing bus
    public void updateBus(String busId, String busName, String busNumber, String busType,
                          String operatorName, int totalSeats, List<String> amenities,
                          String seatLayout, int seaterSeats, int sleeperLowerSeats,
                          int sleeperUpperSeats, String berthType) {
        isLoading.setValue(true);
        updateApiService();

        Map<String, Object> busData = new HashMap<>();
        busData.put("busName", busName);
        busData.put("busNumber", busNumber);
        busData.put("busType", busType);
        busData.put("operatorName", operatorName);
        busData.put("totalSeats", totalSeats);
        busData.put("amenities", amenities);

        Map<String, Object> seatConfig = new HashMap<>();
        seatConfig.put("layout", seatLayout);
        seatConfig.put("seaterSeats", seaterSeats);
        seatConfig.put("sleeperLowerSeats", sleeperLowerSeats);
        seatConfig.put("sleeperUpperSeats", sleeperUpperSeats);
        seatConfig.put("berthType", berthType);
        busData.put("seatConfig", seatConfig);

        apiService.updateBus(busId, busData).enqueue(new Callback<ApiResponse<BusData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusData>> call, Response<ApiResponse<BusData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    successMessage.postValue("Bus updated successfully!");
                } else {
                    errorMessage.postValue("Failed to update bus");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Add new route
    public void addRoute(String source, String destination, double distance, double duration) {
        isLoading.setValue(true);
        updateApiService();

        Map<String, Object> routeData = new HashMap<>();
        routeData.put("source", source);
        routeData.put("destination", destination);
        routeData.put("distance", distance);
        routeData.put("duration", duration);

        apiService.addRoute(routeData).enqueue(new Callback<ApiResponse<RouteData>>() {
            @Override
            public void onResponse(Call<ApiResponse<RouteData>> call, Response<ApiResponse<RouteData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    successMessage.postValue("Route added successfully!");
                } else {
                    errorMessage.postValue("Failed to add route");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RouteData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Add new schedule
    public void addSchedule(String busId, String routeId, String travelDate, String departureTime,
                            String arrivalTime, double price, List<String> boardingPoints,
                            List<String> droppingPoints) {
        isLoading.setValue(true);
        updateApiService();

        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("busId", busId);
        scheduleData.put("routeId", routeId);
        scheduleData.put("travelDate", travelDate);
        scheduleData.put("departureTime", departureTime);
        scheduleData.put("arrivalTime", arrivalTime);
        scheduleData.put("price", price);
        scheduleData.put("boardingPoints", boardingPoints);
        scheduleData.put("droppingPoints", droppingPoints);

        apiService.addSchedule(scheduleData).enqueue(new Callback<ApiResponse<ScheduleData>>() {
            @Override
            public void onResponse(Call<ApiResponse<ScheduleData>> call, Response<ApiResponse<ScheduleData>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    successMessage.postValue("Schedule added successfully!");
                } else {
                    errorMessage.postValue("Failed to add schedule");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ScheduleData>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    // Delete schedule
    public void deleteSchedule(String scheduleId) {
        isLoading.setValue(true);
        updateApiService();
        apiService.deleteSchedule(scheduleId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    successMessage.postValue("Schedule deleted successfully!");
                } else {
                    errorMessage.postValue("Failed to delete schedule");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }
}
