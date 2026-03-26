package com.busbooking.app.models.api;

import java.util.List;

public class ScheduleListData {
    private List<ScheduleData> schedules;
    private int count;

    public List<ScheduleData> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleData> schedules) {
        this.schedules = schedules;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

