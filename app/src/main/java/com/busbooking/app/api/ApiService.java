package com.busbooking.app.api;

import com.busbooking.app.models.api.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {

    // Authentication Endpoints
    @POST("auth/register")
    Call<ApiResponse<AuthData>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<AuthData>> login(@Body LoginRequest request);

    @GET("auth/profile")
    Call<ApiResponse<UserData>> getProfile();

    @PUT("auth/profile")
    Call<ApiResponse<UserData>> updateProfile(@Body UpdateProfileRequest request);

    @POST("auth/change-password")
    Call<ApiResponse<Void>> changePassword(@Body ChangePasswordRequest request);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();

    // Bus Endpoints
    @GET("buses")
    Call<ApiResponse<BusListData>> getAllBuses();

    @GET("buses/search")
    Call<ApiResponse<ScheduleListData>> searchBuses(
            @Query("source") String source,
            @Query("destination") String destination,
            @Query("date") String date
    );

    @GET("buses/popular-routes")
    Call<ApiResponse<RouteListData>> getPopularRoutes(@Query("limit") Integer limit);

    @GET("buses/{id}")
    Call<ApiResponse<BusData>> getBusById(@Path("id") String busId);

    @GET("buses/{scheduleId}/seats")
    Call<ApiResponse<SeatListData>> getSeats(@Path("scheduleId") String scheduleId);

    @POST("buses/{scheduleId}/block-seats")
    Call<ApiResponse<BlockSeatsData>> blockSeats(
            @Path("scheduleId") String scheduleId,
            @Body BlockSeatsRequest request
    );

    // Booking Endpoints
    @POST("bookings")
    Call<ApiResponse<BookingData>> createBooking(@Body CreateBookingRequest request);

    @GET("bookings/user/{userId}")
    Call<ApiResponse<BookingListData>> getUserBookings(@Path("userId") String userId);

    @GET("bookings/user")
    Call<ApiResponse<BookingListData>> getCurrentUserBookings();

    @GET("bookings/{bookingId}")
    Call<ApiResponse<BookingData>> getBookingById(@Path("bookingId") String bookingId);

    @GET("bookings/{bookingId}/details")
    Call<ApiResponse<BookingData>> getBookingDetails(@Path("bookingId") String bookingId);

    @GET("bookings/pnr/{pnr}")
    Call<ApiResponse<BookingData>> getBookingByPNR(@Path("pnr") String pnr);

    @POST("bookings/{bookingId}/cancel")
    Call<ApiResponse<CancelBookingData>> cancelBooking(
            @Path("bookingId") String bookingId,
            @Body CancelBookingRequest request
    );

    // Payment Endpoints
    @POST("payments")
    Call<ApiResponse<PaymentData>> processPayment(@Body PaymentRequest request);

    @GET("payments/{transactionId}")
    Call<ApiResponse<PaymentData>> getPaymentStatus(@Path("transactionId") String transactionId);

    @GET("payments/user/history")
    Call<ApiResponse<PaymentListData>> getPaymentHistory();

    @POST("payments/{transactionId}/refund")
    Call<ApiResponse<RefundData>> processRefund(
            @Path("transactionId") String transactionId,
            @Body RefundRequest request
    );

    // Admin Endpoints
    @POST("buses")
    Call<ApiResponse<BusData>> addBus(@Body java.util.Map<String, Object> busData);

    @PUT("buses/{busId}")
    Call<ApiResponse<BusData>> updateBus(@Path("busId") String busId, @Body java.util.Map<String, Object> busData);

    @DELETE("buses/{busId}")
    Call<ApiResponse<Void>> deleteBus(@Path("busId") String busId);

    @GET("routes")
    Call<ApiResponse<RouteListData>> getAllRoutes();

    @GET("routes/cities")
    Call<ApiResponse<CityListData>> getAllCities();

    @GET("routes/cities/search")
    Call<ApiResponse<CityListData>> searchCities(@Query("q") String query);

    @POST("routes")
    Call<ApiResponse<RouteData>> addRoute(@Body java.util.Map<String, Object> routeData);

    @DELETE("routes/{routeId}")
    Call<ApiResponse<Void>> deleteRoute(@Path("routeId") String routeId);

    @POST("buses/schedules")
    Call<ApiResponse<ScheduleData>> addSchedule(@Body java.util.Map<String, Object> scheduleData);

    @GET("buses/schedules")
    Call<ApiResponse<ScheduleListData>> getAllSchedules();

    @DELETE("buses/schedules/{scheduleId}")
    Call<ApiResponse<Void>> deleteSchedule(@Path("scheduleId") String scheduleId);

    @GET("admin/dashboard")
    Call<ApiResponse<DashboardStats>> getDashboardStats();

    // Owner Endpoints
    @GET("buses/owner/all")
    Call<ApiResponse<BusListData>> getOwnerBuses();

    @GET("bookings/owner/all")
    Call<ApiResponse<BookingListData>> getOwnerBookings();

    @GET("bookings")
    Call<ApiResponse<BookingListData>> getAllBookings();

    // Health Check (uses absolute URL path, not relative to /api/)
    @GET("/health")
    Call<HealthResponse> getHealthStatus();
}
