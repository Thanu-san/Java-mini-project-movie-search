package com.moviesearch.model;

import com.google.gson.annotations.SerializedName;

// Represents a single movie in search results to display

public class MovieSummary {

    private int id;
    private String title;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private double rating;

    private String trailerUrl;

    // constructors
    public MovieSummary() {}

    public MovieSummary(int id, String title, String releaseDate,
                        double rating, String trailerUrl) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.trailerUrl = trailerUrl;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }
}