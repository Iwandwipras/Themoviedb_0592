package com.cooervo.filmography.models;

import java.util.Date;

/**
 * Model class for films of the actor's filmography
 */
public class Film {

    private String title;
    private String posterPath;
    private double rating;
    private int votesAmount;
    private String date;

    public Film(){ }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getVotesAmount() {
        return votesAmount;
    }

    public void setVotesAmount(int votesAmount) {
        this.votesAmount = votesAmount;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
