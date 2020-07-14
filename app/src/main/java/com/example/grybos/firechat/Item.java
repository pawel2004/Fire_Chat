package com.example.grybos.firechat;

public class Item {

    private int mImageResource;
    private String chat_name;
    private String last_message;

    public Item(int mImageResource, String chat_name, String last_message) {
        this.mImageResource = mImageResource;
        this.chat_name = chat_name;
        this.last_message = last_message;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public void setmImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
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
