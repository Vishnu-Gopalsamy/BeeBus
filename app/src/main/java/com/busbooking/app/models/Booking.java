package com.busbooking.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Booking implements Parcelable {
    private String bookingId;
    private String pnr;
    private Bus bus;
    private List<Passenger> passengers;
    private List<String> seatNumbers;
    private double totalAmount;
    private String status;
    private String bookingDate;
    private String travelDate;
    private String boardingPoint;
    private String droppingPoint;
    private String paymentStatus;
    private String paymentId;

    public Booking() {
        this.passengers = new ArrayList<>();
        this.seatNumbers = new ArrayList<>();
    }

    public Booking(String bookingId, Bus bus, List<Passenger> passengers,
                   List<String> seatNumbers, double totalAmount) {
        this();
        this.bookingId = bookingId;
        this.bus = bus;
        this.passengers = passengers;
        this.seatNumbers = seatNumbers;
        this.totalAmount = totalAmount;
        this.status = "Confirmed";
        this.paymentStatus = "Paid";
    }

    protected Booking(Parcel in) {
        bookingId = in.readString();
        pnr = in.readString();
        bus = in.readParcelable(Bus.class.getClassLoader());
        passengers = in.createTypedArrayList(Passenger.CREATOR);
        seatNumbers = in.createStringArrayList();
        totalAmount = in.readDouble();
        status = in.readString();
        bookingDate = in.readString();
        travelDate = in.readString();
        boardingPoint = in.readString();
        droppingPoint = in.readString();
        paymentStatus = in.readString();
        paymentId = in.readString();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public List<Passenger> getPassengers() { return passengers; }
    public void setPassengers(List<Passenger> passengers) { this.passengers = passengers; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }

    public String getBoardingPoint() { return boardingPoint; }
    public void setBoardingPoint(String boardingPoint) { this.boardingPoint = boardingPoint; }

    public String getDroppingPoint() { return droppingPoint; }
    public void setDroppingPoint(String droppingPoint) { this.droppingPoint = droppingPoint; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookingId);
        dest.writeString(pnr);
        dest.writeParcelable(bus, flags);
        dest.writeTypedList(passengers);
        dest.writeStringList(seatNumbers);
        dest.writeDouble(totalAmount);
        dest.writeString(status);
        dest.writeString(bookingDate);
        dest.writeString(travelDate);
        dest.writeString(boardingPoint);
        dest.writeString(droppingPoint);
        dest.writeString(paymentStatus);
        dest.writeString(paymentId);
    }
}
