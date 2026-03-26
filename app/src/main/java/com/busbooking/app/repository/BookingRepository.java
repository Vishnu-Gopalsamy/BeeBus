package com.busbooking.app.repository;

import com.busbooking.app.api.ApiClient;
import com.busbooking.app.api.ApiService;
import com.busbooking.app.models.api.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class BookingRepository {
    private ApiService apiService;
    private static BookingRepository instance;

    private BookingRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public static BookingRepository getInstance() {
        if (instance == null) {
            instance = new BookingRepository();
        }
        return instance;
    }

    // Create new booking
    public void createBooking(CreateBookingRequest request, BookingCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<BookingData>> call = apiService.createBooking(request);

        call.enqueue(new Callback<ApiResponse<BookingData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingData>> call, Response<ApiResponse<BookingData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BookingData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else if (response.code() == 401) {
                    callback.onError("Session expired. Please login again.");
                } else {
                    callback.onError("Failed to create booking: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get user bookings
    public void getUserBookings(BookingListCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<BookingListData>> call = apiService.getCurrentUserBookings();

        call.enqueue(new Callback<ApiResponse<BookingListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingListData>> call, Response<ApiResponse<BookingListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BookingListData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData().getBookings());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else if (response.code() == 401) {
                    callback.onError("Session expired. Please login again.");
                } else {
                    callback.onError("Failed to get bookings: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingListData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get booking by ID
    public void getBookingById(String bookingId, BookingCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<BookingData>> call = apiService.getBookingById(bookingId);

        call.enqueue(new Callback<ApiResponse<BookingData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingData>> call, Response<ApiResponse<BookingData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BookingData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get booking: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get booking details
    public void getBookingDetails(String bookingId, BookingCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<BookingData>> call = apiService.getBookingDetails(bookingId);

        call.enqueue(new Callback<ApiResponse<BookingData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingData>> call, Response<ApiResponse<BookingData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BookingData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get booking details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get booking by PNR
    public void getBookingByPNR(String pnr, BookingCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<BookingData>> call = apiService.getBookingByPNR(pnr);

        call.enqueue(new Callback<ApiResponse<BookingData>>() {
            @Override
            public void onResponse(Call<ApiResponse<BookingData>> call, Response<ApiResponse<BookingData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BookingData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get booking: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BookingData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Cancel booking
    public void cancelBooking(String bookingId, String reason, CancelBookingCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        CancelBookingRequest request = new CancelBookingRequest(reason);
        Call<ApiResponse<CancelBookingData>> call = apiService.cancelBooking(bookingId, request);

        call.enqueue(new Callback<ApiResponse<CancelBookingData>>() {
            @Override
            public void onResponse(Call<ApiResponse<CancelBookingData>> call, Response<ApiResponse<CancelBookingData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CancelBookingData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to cancel booking: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CancelBookingData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Process payment
    public void processPayment(PaymentRequest request, PaymentCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<PaymentData>> call = apiService.processPayment(request);

        call.enqueue(new Callback<ApiResponse<PaymentData>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaymentData>> call, Response<ApiResponse<PaymentData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaymentData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to process payment: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaymentData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get payment status
    public void getPaymentStatus(String transactionId, PaymentCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<PaymentData>> call = apiService.getPaymentStatus(transactionId);

        call.enqueue(new Callback<ApiResponse<PaymentData>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaymentData>> call, Response<ApiResponse<PaymentData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaymentData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get payment status: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaymentData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Get payment history
    public void getPaymentHistory(PaymentHistoryCallback callback) {
        apiService = ApiClient.getClient(ApiClient.getAuthToken()).create(ApiService.class);
        Call<ApiResponse<PaymentListData>> call = apiService.getPaymentHistory();

        call.enqueue(new Callback<ApiResponse<PaymentListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaymentListData>> call, Response<ApiResponse<PaymentListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaymentListData> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData().getPayments());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("Failed to get payment history: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaymentListData>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Callback interfaces
    public interface BookingCallback {
        void onSuccess(BookingData bookingData);
        void onError(String error);
    }

    public interface BookingListCallback {
        void onSuccess(List<BookingData> bookings);
        void onError(String error);
    }

    public interface CancelBookingCallback {
        void onSuccess(CancelBookingData cancelBookingData);
        void onError(String error);
    }

    public interface PaymentCallback {
        void onSuccess(PaymentData paymentData);
        void onError(String error);
    }

    public interface PaymentHistoryCallback {
        void onSuccess(List<PaymentData> payments);
        void onError(String error);
    }
}
