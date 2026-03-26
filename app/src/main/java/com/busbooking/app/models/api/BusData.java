package com.busbooking.app.models.api;

import java.util.List;

public class BusData {
    private String _id;
    private String busName;
    private String busType;
    private int totalSeats;
    private List<String> amenities;
    private String operatorName;
    private String busNumber;
    private double rating;
    private SeatConfigData seatConfig;
    private String ownerId;

    // Getters and Setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public SeatConfigData getSeatConfig() { return seatConfig; }
    public void setSeatConfig(SeatConfigData seatConfig) { this.seatConfig = seatConfig; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
}
