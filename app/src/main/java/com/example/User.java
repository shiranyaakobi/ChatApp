package com.example;

public class User {

    private String image;
    private String name;


    public User(String image,String name) {
        this.image = image;
        this.name = name;
    }
    public User() {}
    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
