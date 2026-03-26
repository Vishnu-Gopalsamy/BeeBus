package com.busbooking.app.models.api;

import java.util.List;

public class ScheduleData {
    private String _id;
    private RouteData route;
    private String source;
    private String destination;
    private String travelDate;
    private String departureTime;
    private String arrivalTime;
    private double price;
    private int availableSeats;
    private List<String> boardingPoints;
    private List<String> droppingPoints;
    private BusData bus;

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public RouteData getRoute() { return route; }
    public void setRoute(RouteData route) { this.route = route; }

    // Convenience getters for source/destination (from route or direct fields)
    public String getSource() {
        if (source != null) return source;
        return route != null ? route.getSource() : "N/A";
    }
    public void setSource(String source) { this.source = source; }

    public String getDestination() {
        if (destination != null) return destination;
        return route != null ? route.getDestination() : "N/A";
    }
    public void setDestination(String destination) { this.destination = destination; }

    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public List<String> getBoardingPoints() { return boardingPoints; }
    public void setBoardingPoints(List<String> boardingPoints) { this.boardingPoints = boardingPoints; }

    public List<String> getDroppingPoints() { return droppingPoints; }
    public void setDroppingPoints(List<String> droppingPoints) { this.droppingPoints = droppingPoints; }

    public BusData getBus() { return bus; }
    public void setBus(BusData bus) { this.bus = bus; }
}
