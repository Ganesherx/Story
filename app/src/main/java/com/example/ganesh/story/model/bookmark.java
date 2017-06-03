package com.example.ganesh.story.model;


public class bookmark {
    private String username;
    private String title;
    private String image;


    public bookmark() {
    }

    public bookmark(String username, String title, String image) {
        this.username = username;
        this.title = title;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
