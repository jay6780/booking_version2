package com.hair.booking.activity.tools.Model;

public class Review {
    private String content;
    private String email;
    private String image;
    private float rating;
    private String userimage;
    private String username;
    private String userId;
    private String reviewId;
    private int bookCount; // New field for book count
    private long timemilli;
    // Default constructor
    public Review() {
    }

    // Parameterized constructor
    public Review(String content, String email, String image, float rating, String userimage, String username) {
        this.content = content;
        this.email = email;
        this.image = image;
        this.rating = rating;
        this.userimage = userimage;
        this.username = username;
    }

    // New constructor including bookCount
    public Review(String content, String email, String image, float rating, String userimage, String username, int bookCount) {
        this.content = content;
        this.email = email;
        this.image = image;
        this.rating = rating;
        this.userimage = userimage;
        this.username = username;
        this.bookCount = bookCount;
    }

    // Getters and Setters

    public long getTimemilli() {
        return timemilli;
    }

    public String getContent() {
        return content;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public float getRating() {
        return rating;
    }

    public String getUserImage() {
        return userimage;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public float setRating(float rating) {
        this.rating = rating;
        return rating;
    }

    // New getter and setter for bookCount
    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }
}
