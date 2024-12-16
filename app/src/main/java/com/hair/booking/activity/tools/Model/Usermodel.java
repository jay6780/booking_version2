package com.hair.booking.activity.tools.Model;

public class Usermodel {
    private String age;
    private boolean online; // Indicates if the user is connected
    private boolean isSuperAdmin;
    private String lengthOfService;
    private String email;
    private String gender;
    private String image;
    private String name;
    private String username;
    private String phone;
    private String birthday;
    private String userId;
    private String address;
    private String key;
    private String timestamp;

    public Usermodel() {
    }

    public Usermodel(String age, String email, String gender, String image, String name, String username,
                     String phone, String birthday) {
        this.age = age;
        this.email = email;
        this.gender = gender;
        this.image = image;
        this.name = name;
        this.username = username;
        this.phone = phone;
        this.birthday = birthday;
    }


    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLengthOfService() {
        return lengthOfService;
    }

    public void setLengthOfService(String lengthOfService) {
        this.lengthOfService = lengthOfService;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getter and Setter for connected
    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
