package com.busbooking.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.busbooking.app.models.api.*;
import com.busbooking.app.repository.BookingRepository;
import java.util.List;

public class BookingViewModel extends ViewModel {

    private final MutableLiveData<BookingData> currentBooking = new MutableLiveData<>();
    private final MutableLiveData<List<BookingData>> bookingHistory = new MutableLiveData<>();
    private final MutableLiveData<PaymentData> paymentData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> bookingSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> paymentSuccess = new MutableLiveData<>(false);

    private final BookingRepository bookingRepository;

    public BookingViewModel() {
        bookingRepository = BookingRepository.getInstance();
    }

    // LiveData getters
    public LiveData<BookingData> getCurrentBooking() { return currentBooking; }
    public LiveData<List<BookingData>> getBookingHistory() { return bookingHistory; }
    public LiveData<PaymentData> getPaymentData() { return paymentData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getBookingSuccess() { return bookingSuccess; }
    public LiveData<Boolean> getPaymentSuccess() { return paymentSuccess; }

    // Create booking
    public void createBooking(CreateBookingRequest request) {
        isLoading.setValue(true);
        bookingRepository.createBooking(request, new BookingRepository.BookingCallback() {
            @Override
            public void onSuccess(BookingData data) {
                isLoading.postValue(false);
                currentBooking.postValue(data);
                bookingSuccess.postValue(true);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Get booking by ID
    public void getBookingById(String bookingId) {
        isLoading.setValue(true);
        bookingRepository.getBookingById(bookingId, new BookingRepository.BookingCallback() {
            @Override
            public void onSuccess(BookingData data) {
                isLoading.postValue(false);
                currentBooking.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Load user bookings
    public void loadUserBookings() {
        isLoading.setValue(true);
        bookingRepository.getUserBookings(new BookingRepository.BookingListCallback() {
            @Override
            public void onSuccess(List<BookingData> data) {
                isLoading.postValue(false);
                bookingHistory.postValue(data);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Process payment
    public void processPayment(PaymentRequest request) {
        isLoading.setValue(true);
        bookingRepository.processPayment(request, new BookingRepository.PaymentCallback() {
            @Override
            public void onSuccess(PaymentData data) {
                isLoading.postValue(false);
                paymentData.postValue(data);
                paymentSuccess.postValue(true);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    // Cancel booking
    public void cancelBooking(String bookingId, String reason, CancelCallback callback) {
        isLoading.setValue(true);
        bookingRepository.cancelBooking(bookingId, reason, new BookingRepository.CancelBookingCallback() {
            @Override
            public void onSuccess(CancelBookingData data) {
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

    // Clear states
    public void clearError() {
        errorMessage.setValue(null);
    }

    public void resetBookingState() {
        bookingSuccess.setValue(false);
        paymentSuccess.setValue(false);
    }

    // Callback interface
    public interface CancelCallback {
        void onSuccess(CancelBookingData data);
        void onError(String error);
    }
}
