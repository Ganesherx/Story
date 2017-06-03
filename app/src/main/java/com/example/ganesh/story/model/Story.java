package com.example.ganesh.story.model;


public class Story {
    private String image;
    private String title;
    private String username;
    private long storyLikeCount;


    public Story() {

    }

    public Story(String image, String title, String username, long storyLikeCount) {
        this.image = image;
        this.title = title;
        this.username = username;
        this.storyLikeCount = storyLikeCount;


    }

    public long getStoryLikeCount() {
        return storyLikeCount;
    }

    public void setStoryLikeCount(long storyLikeCount) {
        this.storyLikeCount = storyLikeCount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
