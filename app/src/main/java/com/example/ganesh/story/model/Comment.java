package com.example.ganesh.story.model;


public class Comment {
    private String username;
    private String title;
    private String comment;
    private String storyUsername;
    private String storyTitle;


    public Comment() {

    }

    public Comment(String username, String title, String comment, String storyUsername, String storyTitle) {
        this.username = username;
        this.comment = comment;
        this.title = title;
        this.storyTitle = storyTitle;
        this.storyUsername = storyUsername;
    }


    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
