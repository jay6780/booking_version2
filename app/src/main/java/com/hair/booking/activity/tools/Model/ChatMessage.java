package com.hair.booking.activity.tools.Model;

public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    }

    public ChatMessage(String senderId, String receiverId, String message, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
