package com.busbooking.app.models.api;

public class CancelBookingRequest {
    private String reason;

    public CancelBookingRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
