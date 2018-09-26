package com.example.remon.firebaseproject.model;

public class Model {
    String title, image, description;


    //constructor
    public Model() {

    }
    //Getter

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
    // Setter

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
