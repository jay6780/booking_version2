package com.hair.booking.activity.tools.Model;

import java.util.List;
import java.util.Map;

public class ChatRoom {
    private List<String> users;
    private String lastMessage;
    private long timestamp;
    private String chatRoomId;
    private Map<String, Message> messages; // Use a Map to hold messages

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Map<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Message> messages) {
        this.messages = messages;
    }
}
