package com.busbooking.app.models.api;

public class DashboardStats {
    private int totalBuses;
    private int totalRoutes;
    private int totalBookings;
    private int totalUsers;
    private double totalRevenue;

    public int getTotalBuses() { return totalBuses; }
    public void setTotalBuses(int totalBuses) { this.totalBuses = totalBuses; }

    public int getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(int totalRoutes) { this.totalRoutes = totalRoutes; }

    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}

