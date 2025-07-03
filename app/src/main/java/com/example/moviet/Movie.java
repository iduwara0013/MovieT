package com.example.moviet;

import java.io.Serializable;

public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;  // optional but recommended

    private String documentId;  // Firestore document ID (not stored in Firestore)
    private String title;
    private String genre;
    private String duration;
    private String IMDb;
    private String imageurl;
    private String status;

    // Empty constructor required for Firestore
    public Movie() {
    }

    // Constructor (optional if you only use setters)
    public Movie(String title, String genre, String duration, String IMDb, String imageurl, String status) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.IMDb = IMDb;
        this.imageurl = imageurl;
        this.status = status;
    }

    // Getters and Setters

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIMDb() {
        return IMDb;
    }

    public void setIMDb(String IMDb) {
        this.IMDb = IMDb;
    }

    public String getImageurl() {
        return imageurl;
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
