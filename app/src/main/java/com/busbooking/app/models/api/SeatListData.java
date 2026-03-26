package com.busbooking.app.models.api;

import java.util.List;

public class SeatListData {
    private List<SeatData> seats;
    private int totalSeats;
    private int availableSeats;

    public List<SeatData> getSeats() { return seats; }
    public void setSeats(List<SeatData> seats) { this.seats = seats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
