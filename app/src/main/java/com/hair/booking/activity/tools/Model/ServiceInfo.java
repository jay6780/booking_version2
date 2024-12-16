package com.hair.booking.activity.tools.Model;

public class ServiceInfo {
    private String information;
    private String price;
    private String imgInfoUrl;
    private String serviceimgUrl;
    private String servicename;
    private String key;
    private boolean isChecked;
    public ServiceInfo() {}

    public ServiceInfo(String information, String price, String imgInfoUrl, String servicename,String key) {
        this.information = information;
        this.price = price;
        this.imgInfoUrl = imgInfoUrl;
        this.servicename = servicename;
        this.key = key;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getKey() {
        return key;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgInfoUrl() {
        return imgInfoUrl;
    }

    public void setImgInfoUrl(String imgInfoUrl) {
        this.imgInfoUrl = imgInfoUrl;
    }

    public String getServiceimgUrl() {
        return serviceimgUrl;
    }

    public void setServiceimgUrl(String serviceimgUrl) {
        this.serviceimgUrl = serviceimgUrl;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
