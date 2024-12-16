package com.hair.booking.activity.tools.Model;

public class Message2 {
    private String senderEmail;
    private String message;
    private long timestamp;
    private String username;  // New field for the username
    private String imageUrl;

    public Message2() {
    }

    // Parameterized constructor
    public Message2(String senderEmail, String message, long timestamp, String username, String imageUrl) {
        this.senderEmail = senderEmail;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters (if necessary)
    public String getSenderEmail() {
        return senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
