package com.pushstorm;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationData {
    //    clickAction:
    //  open app : com.example.app
    //  open activity : com.example.app.activity
    //
    //
    //
    private String title, body, imageUrl, iconUrl, clickAction;

    private List<NotificationButtonData> buttons;

    public NotificationData(String title, String body, String imageUrl, String iconUrl, String clickAction) {
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.iconUrl = iconUrl;
//        this.buttons = buttons;
        this.clickAction = clickAction;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getClickAction() {
        return clickAction;
    }


//    public JSONObject getCustomContent() {
//        try {
//            return new JSONObject(customContent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public List<NotificationButtonData> getButtons() {
        return buttons;
    }


}
