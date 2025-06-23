package com.example.moviet;

public class Movie {

    private String title;
    private String genre;
    private String duration;
    private String IMDb;
    private String imageurl;
    private String status; // renamed from imagePath

    public Movie() {
    }

    public Movie(String title, String genre, String duration, String IMDb, String imageurl , String status) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.IMDb = IMDb;
        this.imageurl = imageurl;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDuration() {
        return duration;
    }

    public String getImdb() {
        return IMDb;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setImdb(String IMDb) {
        this.IMDb = IMDb;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
