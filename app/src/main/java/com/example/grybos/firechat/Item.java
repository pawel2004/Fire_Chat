package com.example.grybos.firechat;

import java.util.Map;

public class Item {

    private String id;
    private String image_resource;
    private String chat_name;

    public Item(){

    }

    public Item(String id, String image_resource, String chat_name) {
        this.id = id;
        this.image_resource = image_resource;
        this.chat_name = chat_name;
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

}
