package com.busbooking.app.models.api;

import java.util.List;

public class BookingListData {
    private List<BookingData> bookings;
    private int count;

    public List<BookingData> getBookings() { return bookings; }
    public void setBookings(List<BookingData> bookings) { this.bookings = bookings; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
