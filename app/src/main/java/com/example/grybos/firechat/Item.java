package com.example.grybos.firechat;

import java.util.Map;

public class Item {

    private String chat_name;
    private String last_message;

    public Item(){

    }

    public Item(String chat_name, String last_message) {
        this.chat_name = chat_name;
        this.last_message = last_message;
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
}
