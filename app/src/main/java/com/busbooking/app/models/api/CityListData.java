package com.busbooking.app.models.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CityListData {
    @SerializedName("cities")
    private List<String> cities;

    @SerializedName("count")
    private int count;

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

