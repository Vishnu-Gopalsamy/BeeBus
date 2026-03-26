package com.busbooking.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Bus implements Parcelable {
    private String id;
    private String name;
    private String busType;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private double price;
    private int availableSeats;
    private int totalSeats;
    private String fromCity;
    private String toCity;
    private String departureDate;
    private List<String> amenities;
    private List<String> boardingPoints;
    private List<String> droppingPoints;
    private double rating;
    private String operatorName;

    public Bus() {
        this.amenities = new ArrayList<>();
        this.boardingPoints = new ArrayList<>();
        this.droppingPoints = new ArrayList<>();
    }

    public Bus(String id, String name, String busType, String departureTime,
               String arrivalTime, String duration, double price, int availableSeats) {
        this();
        this.id = id;
        this.name = name;
        this.busType = busType;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.price = price;
        this.availableSeats = availableSeats;
    }

    protected Bus(Parcel in) {
        id = in.readString();
        name = in.readString();
        busType = in.readString();
        departureTime = in.readString();
        arrivalTime = in.readString();
        duration = in.readString();
        price = in.readDouble();
        availableSeats = in.readInt();
        totalSeats = in.readInt();
        fromCity = in.readString();
        toCity = in.readString();
        departureDate = in.readString();
        amenities = in.createStringArrayList();
        boardingPoints = in.createStringArrayList();
        droppingPoints = in.createStringArrayList();
        rating = in.readDouble();
        operatorName = in.readString();
    }

    public static final Creator<Bus> CREATOR = new Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getFromCity() { return fromCity; }
    public void setFromCity(String fromCity) { this.fromCity = fromCity; }

    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }

    public String getDepartureDate() { return departureDate; }
    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public List<String> getBoardingPoints() { return boardingPoints; }
    public void setBoardingPoints(List<String> boardingPoints) { this.boardingPoints = boardingPoints; }

    public List<String> getDroppingPoints() { return droppingPoints; }
    public void setDroppingPoints(List<String> droppingPoints) { this.droppingPoints = droppingPoints; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(busType);
        dest.writeString(departureTime);
        dest.writeString(arrivalTime);
        dest.writeString(duration);
        dest.writeDouble(price);
        dest.writeInt(availableSeats);
        dest.writeInt(totalSeats);
        dest.writeString(fromCity);
        dest.writeString(toCity);
        dest.writeString(departureDate);
        dest.writeStringList(amenities);
        dest.writeStringList(boardingPoints);
        dest.writeStringList(droppingPoints);
        dest.writeDouble(rating);
        dest.writeString(operatorName);
    }
}
