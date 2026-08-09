package com.moviesearch.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a single movie in search results
 * Contains the basic info displayed in the search table
 */
public class MovieSummary {

    private int id;
    private String title;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private double rating;

    // We'll add trailerUrl later when we fetch videos
    private String trailerUrl;

    // Constructors
    public MovieSummary() {}

    public MovieSummary(int id, String title, String releaseDate,
                        double rating, String trailerUrl) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.trailerUrl = trailerUrl;
    }

    // Getters
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

    // Setters (for Gson)
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