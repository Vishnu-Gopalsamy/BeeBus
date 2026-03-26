package com.busbooking.app.models.api;

import java.util.List;

public class BlockSeatsRequest {
    private List<Integer> seatNumbers;

    public BlockSeatsRequest(List<Integer> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public List<Integer> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<Integer> seatNumbers) { this.seatNumbers = seatNumbers; }
}
