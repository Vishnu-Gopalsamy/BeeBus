package com.busbooking.app.models.api;

import java.util.List;

public class BookingData {
    private String _id;
    private String bookingId;
    private String userId;
    private String scheduleId;
    private List<Integer> seats;
    private List<PassengerData> passengers;
    private double totalAmount;
    private String bookingStatus;
    private String paymentStatus;
    private String pnr;
    private String createdAt;
    private ContactDetails contactDetails;
    private TicketDetails ticketDetails;
    
    // Additional fields for Admin/Detailed view
    private UserData user;
    private ScheduleData schedule;

    public BookingData() {}

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public List<Integer> getSeats() { return seats; }
    public void setSeats(List<Integer> seats) { this.seats = seats; }

    public List<PassengerData> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerData> passengers) { this.passengers = passengers; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public ContactDetails getContactDetails() { return contactDetails; }
    public void setContactDetails(ContactDetails contactDetails) { this.contactDetails = contactDetails; }

    public TicketDetails getTicketDetails() { return ticketDetails; }
    public void setTicketDetails(TicketDetails ticketDetails) { this.ticketDetails = ticketDetails; }

    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }

    public ScheduleData getSchedule() { return schedule; }
    public void setSchedule(ScheduleData schedule) { this.schedule = schedule; }
}
