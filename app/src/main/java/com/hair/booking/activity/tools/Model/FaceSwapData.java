package com.hair.booking.activity.tools.Model;

public class FaceSwapData {
    private String swap_url;
    private String target_url;

    public FaceSwapData() {
        // Default constructor required for calls to DataSnapshot.getValue(FaceSwapData.class)
    }

    public FaceSwapData(String swap_url, String target_url) {
        this.swap_url = swap_url;
        this.target_url = target_url;
    }

    public String getSwapUrl() {
        return swap_url;
    }

    public void setSwapUrl(String swap_url) {
        this.swap_url = swap_url;
    }

    public String getTargetUrl() {
        return target_url;
    }

    public void setTargetUrl(String target_url) {
        this.target_url = target_url;
    }
}
