package com.busbooking.app.models.api;

import java.util.List;

public class BlockSeatsData {
    private String scheduleId;
    private List<Integer> blockedSeats;
    private int expiresIn;

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public List<Integer> getBlockedSeats() { return blockedSeats; }
    public void setBlockedSeats(List<Integer> blockedSeats) { this.blockedSeats = blockedSeats; }

    public int getExpiresIn() { return expiresIn; }
    public void setExpiresIn(int expiresIn) { this.expiresIn = expiresIn; }
}
