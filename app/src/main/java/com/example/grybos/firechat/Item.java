package com.example.grybos.firechat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;

public class Item {

    private String id;
    private String image_resource;
    private String chat_name;
    private Boolean isPrivate;
    private ArrayList<User> users;

    public Item(){

    }

    public Item(String id, String image_resource, String chat_name, Boolean isPrivate, ArrayList<User> users) {
        this.id = id;
        this.image_resource = image_resource;
        this.chat_name = chat_name;
        this.isPrivate = isPrivate;
        this.users = users;
    }

    public String getId(){

        return id;

    }

    public String getImage_resource(){

        return image_resource;

    }

    public String getChat_name() {
        return chat_name;
    }

    public Boolean getIsPrivate(){

        return isPrivate;

    }

    public ArrayList<User> getUsers(){

        return users;

    }

}
