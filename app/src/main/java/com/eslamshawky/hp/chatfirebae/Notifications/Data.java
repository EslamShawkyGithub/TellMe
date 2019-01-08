package com.eslamshawky.hp.chatfirebae.Notifications;

public class Data {
    private int icon;
    private String user;
    private String title;
    private String body;
    private String sented;

    public Data(int icon, String user, String title, String body, String sented) {
        this.icon = icon;
        this.user = user;
        this.title = title;
        this.body = body;
        this.sented = sented;
    }

    public Data(String uid, int ic_launcher, String title, String new_message, String userid) {
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}
