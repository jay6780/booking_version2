package com.hair.booking.activity.tools.Model;

public class MychatId {
    private String chatId;
    private String uid;

    public MychatId() {
    }

    public MychatId(String uid, String chatId) {
        this.uid = uid;
        this.chatId = chatId;
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
