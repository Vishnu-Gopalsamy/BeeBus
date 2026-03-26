package com.busbooking.app.models.api;

public class RefundRequest {
    private double amount;
    private String reason;

    public RefundRequest(double amount, String reason) {
        this.amount = amount;
        this.reason = reason;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
