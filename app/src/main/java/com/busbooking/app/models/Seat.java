package com.busbooking.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Seat implements Parcelable {
    private int seatNumber;
    private String seatId;
    private SeatType seatType;
    private SeatStatus status;
    private double price;
    private boolean isSelected;
    private String deck; // "upper" or "lower"

    public enum SeatType {
        REGULAR, LADIES_ONLY
    }

    public enum SeatStatus {
        AVAILABLE, BOOKED, SELECTED
    }

    public Seat() {}

    public Seat(int seatNumber, String seatId, SeatType seatType, SeatStatus status, double price) {
        this.seatNumber = seatNumber;
        this.seatId = seatId;
        this.seatType = seatType;
        this.status = status;
        this.price = price;
        this.isSelected = false;
        this.deck = "lower";
    }

    protected Seat(Parcel in) {
        seatNumber = in.readInt();
        seatId = in.readString();
        seatType = SeatType.valueOf(in.readString());
        status = SeatStatus.valueOf(in.readString());
        price = in.readDouble();
        isSelected = in.readByte() != 0;
        deck = in.readString();
    }

    public static final Creator<Seat> CREATOR = new Creator<Seat>() {
        @Override
        public Seat createFromParcel(Parcel in) {
            return new Seat(in);
        }

        @Override
        public Seat[] newArray(int size) {
            return new Seat[size];
        }
    };

    // Getters and Setters
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public String getDeck() { return deck; }
    public void setDeck(String deck) { this.deck = deck; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(seatNumber);
        dest.writeString(seatId);
        dest.writeString(seatType.name());
        dest.writeString(status.name());
        dest.writeDouble(price);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(deck);
    }
}
