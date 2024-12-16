package com.hair.booking.activity.tools.Model;

public class Message {
    private String senderEmail;
    private String message;
    private long timestamp;
    private String username;
    private String userImageUrl;
    private String imageUrl;
    private String key;
    private String chatRoomId;
    private String messageId; // Added messageId field

    // Default constructor
    public Message() {
    }

    // Parameterized constructor
    public Message(String senderEmail, String message, long timestamp, String username, String userImageUrl, String imageUrl, String messageId,String key) {
        this.senderEmail = senderEmail;
        this.message = message;
        this.timestamp = timestamp;
        this.username = username;
        this.userImageUrl = userImageUrl;
        this.imageUrl = imageUrl;
        this.messageId = messageId;
        this.key = key;// Initialize messageId
    }

    // Getters and Setters
    public String getSenderEmail() {
        return senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserImageUrl() {
        return userImageUrl; // Corrected method name
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getMessageId() {
        return messageId; // Getter for messageId
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId; // Setter for messageId
    }
}
