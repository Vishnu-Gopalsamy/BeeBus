package com.busbooking.app.models.api;

import java.util.List;

public class CreateBookingRequest {
    private String scheduleId;
    private List<Integer> seats;
    private List<PassengerData> passengers;
    private double totalAmount;

    public CreateBookingRequest(String scheduleId, List<Integer> seats, List<PassengerData> passengers, double totalAmount) {
        this.scheduleId = scheduleId;
        this.seats = seats;
        this.passengers = passengers;
        this.totalAmount = totalAmount;
    }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public List<Integer> getSeats() { return seats; }
    public void setSeats(List<Integer> seats) { this.seats = seats; }

    public List<PassengerData> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerData> passengers) { this.passengers = passengers; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
