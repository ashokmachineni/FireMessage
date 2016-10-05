package com.ashok.firemessage;

/**
 * Created by ashok on 10/4/16.
 */

public class Movie {
    private String title,image,link;

    public Movie(){}

    public Movie(String title, String image, String link) {
        this.title = title;
        this.image = image;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
