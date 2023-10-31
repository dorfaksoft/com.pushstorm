package com.pushstorm;

public class NotificationButtonData {
    private String text;
    private int id;

    public NotificationButtonData(String text, int id) {
        this.text = text;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

}

