package com.dalati.ui.models;

public class Chat {
    String id, userId, lastMessage, date;

    public Chat(String id, String userId, String lastMessage, String date) {
        this.id = id;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.date = date;
    }
    public Chat(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
