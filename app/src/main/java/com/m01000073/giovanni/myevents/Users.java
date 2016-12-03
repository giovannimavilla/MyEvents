package com.m01000073.giovanni.myevents;

public class Users {

    private String id;
    private String image;
    private String name;

    public Users(){

    }

    public Users(String id, String image, String name) {
        this.id = id;
        this.image = image;
        this.name  = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}