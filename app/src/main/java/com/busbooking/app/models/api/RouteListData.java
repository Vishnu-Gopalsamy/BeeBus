package com.busbooking.app.models.api;

import java.util.List;

public class RouteListData {
    private List<RouteData> routes;
    private int count;

    public List<RouteData> getRoutes() { return routes; }
    public void setRoutes(List<RouteData> routes) { this.routes = routes; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
