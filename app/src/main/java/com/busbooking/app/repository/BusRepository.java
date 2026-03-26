package com.busbooking.app.repository;

import com.busbooking.app.api.ApiClient;
import com.busbooking.app.api.ApiService;
import com.busbooking.app.models.api.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class BusRepository {
    private ApiService apiService;
    private static BusRepository instance;

    private BusRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public static BusRepository getInstance() {
        if (instance == null) {
            instance = new BusRepository();
        }
        return instance;
    }

    private void updateApiService() {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
    }

    // Search buses
    public void searchBuses(String source, String destination, String date, BusSearchCallback callback) {
        updateApiService();
        Call<ApiResponse<ScheduleListData>> call = apiService.searchBuses(source, destination, date);

        call.enqueue(new Callback<ApiResponse<ScheduleListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<ScheduleListData>> call, Response<ApiResponse<ScheduleListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ScheduleListData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData().getSchedules());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to search buses: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ScheduleListData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get popular routes
    public void getPopularRoutes(PopularRoutesCallback callback) {
        updateApiService();
        Call<ApiResponse<RouteListData>> call = apiService.getPopularRoutes(5);

        call.enqueue(new Callback<ApiResponse<RouteListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<RouteListData>> call, Response<ApiResponse<RouteListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<RouteListData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData().getRoutes());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get popular routes: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RouteListData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get bus details by ID
    public void getBusDetails(String busId, BusDetailsCallback callback) {
        updateApiService();
        Call<ApiResponse<BusData>> call = apiService.getBusById(busId);

        call.enqueue(new Callback<ApiResponse<BusData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusData>> call, Response<ApiResponse<BusData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BusData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get bus details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get seats for schedule
    public void getSeats(String scheduleId, SeatsCallback callback) {
        updateApiService();
        Call<ApiResponse<SeatListData>> call = apiService.getSeats(scheduleId);

        call.enqueue(new Callback<ApiResponse<SeatListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<SeatListData>> call, Response<ApiResponse<SeatListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<SeatListData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData().getSeats());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else if (response.code() == 401) {
                    callback.onError("Session expired. Please login again.");
                } else {
                    callback.onError("Failed to get seats: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SeatListData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Block seats temporarily
    public void blockSeats(String scheduleId, List<Integer> seatNumbers, BlockSeatsCallback callback) {
        updateApiService();
        BlockSeatsRequest request = new BlockSeatsRequest(seatNumbers);
        Call<ApiResponse<BlockSeatsData>> call = apiService.blockSeats(scheduleId, request);

        call.enqueue(new Callback<ApiResponse<BlockSeatsData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BlockSeatsData>> call, Response<ApiResponse<BlockSeatsData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BlockSeatsData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to block seats: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BlockSeatsData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get all buses
    public void getAllBuses(AllBusesCallback callback) {
        updateApiService();
        Call<ApiResponse<BusListData>> call = apiService.getAllBuses();

        call.enqueue(new Callback<ApiResponse<BusListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusListData>> call, Response<ApiResponse<BusListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BusListData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData().getBuses());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get buses: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusListData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Callback interfaces
    public interface BusSearchCallback {
        void onSuccess(List<ScheduleData> schedules);
        void onError(String error);
    }

    public interface PopularRoutesCallback {
        void onSuccess(List<RouteData> routes);
        void onError(String error);
    }

    public interface BusDetailsCallback {
        void onSuccess(BusData busData);
        void onError(String error);
    }

    public interface SeatsCallback {
        void onSuccess(List<SeatData> seats);
        void onError(String error);
    }

    public interface BlockSeatsCallback {
        void onSuccess(BlockSeatsData blockSeatsData);
        void onError(String error);
    }

    public interface AllBusesCallback {
        void onSuccess(List<BusData> buses);
        void onError(String error);
    }
}
