package com.eslamshawky.hp.chatfirebae.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;
    private double lat;
    private double lng;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", status='" + status + '\'' +
                ", search='" + search + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    public User(String id, String username, String imageURL, String status , String search , double lat, double lng) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.lat = lat;
        this.lng = lng;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
