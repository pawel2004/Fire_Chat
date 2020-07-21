package com.example.grybos.firechat;

import java.util.Date;

public class Message{

    private String id;
    private String userName;
    private long messageDate;
    private String userImage;
    private String messageText;

    public Message(){}

    public Message(String id, String userName, Long messageDate, String userImage, String messageText) {
        this.id = id;
        this.userName = userName;
        this.messageDate = messageDate;
        this.userImage = userImage;
        this.messageText = messageText;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public long getMessageDate() {
        return messageDate;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getMessageText(){

        return messageText;

    }
}
