package com.example.chatscreen;

public class ChatMessage {
    private long date;
    private String senderName;
    private String uid;
    private String messageContent;
    private String senderImage;


    public ChatMessage(long date,String uid, String senderName, String messageContent, String senderImage) {
        this.date = date;
        this.uid = uid;
        this.senderName = senderName;
        this.messageContent = messageContent;
        this.senderImage = senderImage;
    }

    public ChatMessage() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }
}
