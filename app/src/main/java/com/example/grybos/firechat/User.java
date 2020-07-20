package com.example.grybos.firechat;

public class User {

    private String id;
    private String displayName;
    private String imageResource;

    public User(){



    }

    public User(String id, String displayName, String imageResource) {
        this.id = id;
        this.displayName = displayName;
        this.imageResource = imageResource;
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
}
