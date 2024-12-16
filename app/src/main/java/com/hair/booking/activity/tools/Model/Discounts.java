package com.hair.booking.activity.tools.Model;

public class Discounts {

    private String discount;
    private String imageUrl;
    private String serviceName;
    private String userId;
    private String gender;

    public Discounts() {
        // Default constructor required for Firebase
    }

    public Discounts(String discount, String imageUrl, String serviceName, String userId,String gender) {
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.serviceName = serviceName;
        this.userId = userId;
        this.gender = gender;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
