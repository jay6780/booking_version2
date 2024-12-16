package com.hair.booking.activity.tools.Model;

public class Booking2 {
    public String providerName;
    public String serviceName;
    public String price;
    public String heads;
    public String phonenumber;
    public String date;
    public String time;
    public String image;
    public String address;
    public String email;
    public String age;
    public String key;
    public String lengthOfservice;
    private String paymentMethod;
    private String snapshotkey;

    // Default constructor
    public Booking2() {
    }

    // Parameterized constructor
    public Booking2(String providerName, String serviceName, String price, String heads,
                    String phonenumber, String date, String time, String image,
                    String address, String email, String age, String lengthOfservice, String paymentMethod, String key,String snapshotkey) {
        this.providerName = providerName;
        this.serviceName = serviceName;
        this.price = price;
        this.heads = heads;
        this.phonenumber = phonenumber;
        this.date = date;
        this.time = time;
        this.image = image;
        this.address = address;
        this.email = email;
        this.age = age;
        this.lengthOfservice = lengthOfservice;
        this.paymentMethod = paymentMethod;
        this.key = key;
        this.snapshotkey = snapshotkey;
    }

    // Setters


    public String getSnapshotkey() {
        return snapshotkey;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setHeads(String heads) {
        this.heads = heads;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setLengthOfservice(String lengthOfservice) {
        this.lengthOfservice = lengthOfservice;
    }


    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getters
    public String getProviderName() {
        return providerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPrice() {
        return price;
    }

    public String getHeads() {
        return heads;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getAge() {
        return age;
    }

    public String getLengthOfservice() {
        return lengthOfservice;
    }


    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getKey() {
        return key;
    }
}
