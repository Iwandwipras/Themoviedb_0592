package com.cooervo.filmography.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Model class for films of the actor's filmography
 */
public class Film {

    private String title;
    private String posterPath;
    private Date date;

    private String formattedDate;

    public Film() {
    }

    public String getFormattedDate() {

        if (formattedDate == null || formattedDate.equals("null")){
            return "null";
        }

        return formattedDate;
    }

    public void setDate(Date d) {
        date = d;

    }

    /**
     * This method receives a string representation of yyyy-MM-dd for date
     * then it converts it to date and sets date to such date type and
     * at the same time sets formatted date (which is a string).
     * <p/>
     * This method is called in onResponse() of OkHTTP and will be useful once we
     * try to sort List<Film> filmography by Date.
     *
     * @param formattedDate string representation of yyyy-MM-dd for date
     */

    public void setFormattedDate(String formattedDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date unformatedDate = formatter.parse(formattedDate);

            date = unformatedDate;

            formattedDate = formatter.format(unformatedDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public Date getDate() {
        return date;
    }

    public void setDate(String dateInString) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date date = formatter.parse(dateInString);

            this.date = date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
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


}
