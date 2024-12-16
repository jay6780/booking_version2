package com.hair.booking.activity.tools.Model;

public class ApkDetails {
    public String apkUrl;
    public String versionName;
    public String apkName;

    public ApkDetails() {
    }

    public ApkDetails(String apkUrl, String versionName, String apkName) {
        this.apkUrl = apkUrl;
        this.versionName = versionName;
        this.apkName = apkName;
    }
}