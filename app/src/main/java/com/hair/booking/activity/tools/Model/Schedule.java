package com.hair.booking.activity.tools.Model;

import java.util.Date;

public class Schedule {
    public String time;
    private String name;
    private String imageUrl;
    private Date date;
    public Schedule() {
    }

    public Schedule(String name, String imageUrl, Date date) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.date = date;
    }

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


    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
