package com.hair.booking.activity.tools.Model;

import java.util.Date;

public class Schedule3 {
    private String time;
    private String name;
    private String imageUrl;
    private Date date;
    private String key;

    public Schedule3() {
    }

    public Schedule3(String name, String imageUrl, Date date, String key, String time) { // Include key in constructor
        this.name = name;
        this.imageUrl = imageUrl;
        this.date = date;
        this.key = key;
        this.time = time;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
