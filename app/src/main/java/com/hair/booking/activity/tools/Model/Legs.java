package com.hair.booking.activity.tools.Model;

public class Legs {
    private int distance;
    private int duration;
    private LatLng start_point;
    private LatLng end_point;
    private Step[] steps;

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public Step[] getSteps() {
        return steps;
    }
}
