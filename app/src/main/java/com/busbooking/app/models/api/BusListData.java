package com.busbooking.app.models.api;

import java.util.List;

public class BusListData {
    private List<BusData> buses;
    private int count;

    public List<BusData> getBuses() { return buses; }
    public void setBuses(List<BusData> buses) { this.buses = buses; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
