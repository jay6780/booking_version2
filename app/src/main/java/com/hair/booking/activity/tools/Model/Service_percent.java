package com.hair.booking.activity.tools.Model;

public class Service_percent {
    private String name;
    private int percent;
    public Service_percent(String name, int percent) {
        this.name = name;
        this.percent = percent;
    }
    public int getPercent() {
        return percent;
    }

    public String getName() {
        return name;
    }
}
