package com.hair.booking.activity.tools.Model;

public class BookingId {
    private String chatId;
    private String uid;
    private String time;

    public BookingId() {
    }

    public BookingId(String time, String chatId) {
        this.chatId = chatId;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }


}
