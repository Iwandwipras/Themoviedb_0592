package com.cooervo.filmography.models;

import java.io.Serializable;

/**
 * Model class for actor must implement serializable to pass
 * object actor as an intent extra in MainActivity
 * to ActorFilmographyActivity
 */
public class Actor implements Serializable{

    private int id;
    private String name;
    private String picPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setProfilePicturePath(String picPath) {
        this.picPath = picPath;
    }
}
