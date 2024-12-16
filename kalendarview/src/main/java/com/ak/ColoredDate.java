package com.ak;

import java.util.Date;

public class ColoredDate {
    private Date date;  // It's a good practice to make fields private
    private int color;

    public ColoredDate(Date mDate, int color) {
        this.color = color;
        this.date = mDate;
    }

    // Getter for date
    public Date getDate() {
        return date; // Return the date field
    }

    // Getter for color (optional, in case you need to access the color later)
    public int getColor() {
        return color; // Return the color field
    }
}
