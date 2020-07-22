package com.example.grybos.firechat;

public class User {

    private String id;
    private String displayName;
    private String imageResource;
    private String emailAdress;

    public User(){



    }

    public User(String id, String displayName, String imageResource, String emailAdress) {
        this.id = id;
        this.displayName = displayName;
        this.imageResource = imageResource;
        this.emailAdress = emailAdress;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getEmailAdress(){return emailAdress;}
}
