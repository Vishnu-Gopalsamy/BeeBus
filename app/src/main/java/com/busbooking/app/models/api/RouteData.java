package com.busbooking.app.models.api;

public class RouteData {
    private String _id;
    private String source;
    private String destination;
    private double minPrice;
    private double distance;
    private double duration;
    private int count;
    private RouteInfo route;  // Nested route object from API

    // Inner class for nested route
    public static class RouteInfo {
        private String source;
        private String destination;

        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
    }

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getRouteId() { return _id; }

    // Get source - check nested route first
    public String getSource() {
        if (route != null && route.source != null) {
            return route.source;
        }
        return source;
    }
    public void setSource(String source) { this.source = source; }

    // Get destination - check nested route first
    public String getDestination() {
        if (route != null && route.destination != null) {
            return route.destination;
        }
        return destination;
    }
    public void setDestination(String destination) { this.destination = destination; }

    public double getMinPrice() { return minPrice; }
    public void setMinPrice(double minPrice) { this.minPrice = minPrice; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public RouteInfo getRoute() { return route; }
    public void setRoute(RouteInfo route) { this.route = route; }
}
