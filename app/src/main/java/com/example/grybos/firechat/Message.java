package com.example.grybos.firechat;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Date;

public class Message{

    private String id;
    private String userName;
    private long messageDate;
    private String userImage;
    private String messageText;
    private String emailAdress;
    private String userId;

    public Message(){}

    public Message(String id, String userName, Long messageDate, String userImage, String messageText, String emailAdress, String userId) {
        this.id = id;
        this.userName = userName;
        this.messageDate = messageDate;
        this.userImage = userImage;
        this.messageText = messageText;
        this.emailAdress = emailAdress;
        this.userId = userId;
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

    public String getEmailAdress(){return emailAdress;}

    public String getUserId(){return userId;}

}
