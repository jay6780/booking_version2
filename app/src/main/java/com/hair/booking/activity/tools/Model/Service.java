package com.hair.booking.activity.tools.Model;

public class Service {
    private String name;
    private int price;
    private String id;
    private String imageUrl;
    private String gender;
    private boolean isChecked;  // New field to track checkbox state
    private String key;
    // Default constructor
    public Service() {
    }

    // Parameterized constructor
    public Service(String name, int price, String imageUrl, String gender,String key) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.gender = gender;
        this.isChecked = false;
        this.key =  key;
    }

    // Getter and setter for 'name'
    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for 'price'
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Getter and setter for 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and setter for 'imageUrl'
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter and setter for 'gender'
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Getter and setter for 'isChecked' (checkbox state)
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
