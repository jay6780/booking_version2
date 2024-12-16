package com.hair.booking.activity.tools.Model;

public class Step {
    private int distance;
    private int duration;
    private LatLng start_point;
    private LatLng end_point;
    private String maneuver;

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public String getManeuver() {
        return maneuver;
    }
}
