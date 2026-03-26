package com.busbooking.app.models.api;

public class SeatData {
    private String _id;
    private String scheduleId;
    private int seatNumber;
    private String seatType;
    private String status;

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
