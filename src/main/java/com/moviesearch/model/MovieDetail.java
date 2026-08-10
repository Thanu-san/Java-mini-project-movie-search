package com.moviesearch.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// represents detailed information about a single movie

public class MovieDetail {

    private int id;
    private String title;
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private double rating;

    private int runtime;  // in minutes
    private List<Genre> genres;
    private String trailerUrl;
    private Credits credits;


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

    // inner class for Genre
    public static class Genre {
        private int id;
        private String name;

        public Genre() {}

        public Genre(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    // inner class for Credits
    public static class Credits {
        private List<Cast> cast;
        private List<Crew> crew;

        // Constructor 1
        public Credits() {
            this.cast = new java.util.ArrayList<>();
            this.crew = new java.util.ArrayList<>();
        }

        // Constructor 2
        public Credits(List<Cast> cast, List<Crew> crew) {
            this.cast = cast != null ? cast : new java.util.ArrayList<>();
            this.crew = crew != null ? crew : new java.util.ArrayList<>();
        }

        public List<Cast> getCast() {
            return cast;
        }

        public List<Crew> getCrew() {
            return crew;
        }

        public void setCast(List<Cast> cast) {
            this.cast = cast;
        }

        public void setCrew(List<Crew> crew) {
            this.crew = crew;
        }
    }


    // inner class for Cast member
    public static class Cast {
        private int id;
        private String name;
        private String character;

        public Cast() {}

        public Cast(int id, String name, String character) {
            this.id = id;
            this.name = name;
            this.character = character;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCharacter() {
            return character;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCharacter(String character) {
            this.character = character;
        }
    }

    // niner class for crew member
    public static class Crew {
        private int id;
        private String name;
        private String job;

        public Crew() {}

        public Crew(int id, String name, String job) {
            this.id = id;
            this.name = name;
            this.job = job;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getJob() {
            return job;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setJob(String job) {
            this.job = job;
        }
    }


    // inner class for videos/trailer
    public static class Video {
        private String key;
        private String type;
        private String site;

        public Video() {}

        public Video(String key, String type, String site) {
            this.key = key;
            this.type = type;
            this.site = site;
        }

        public String getKey() {
            return key;
        }

        public String getType() {
            return type;
        }

        public String getSite() {
            return site;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setSite(String site) {
            this.site = site;
        }
    }


    // inner class for Videos response
    public static class VideosResponse {
        private List<Video> results;

        public VideosResponse() {}

        public VideosResponse(List<Video> results) {
            this.results = results;
        }

        public List<Video> getResults() {
            return results;
        }

        public void setResults(List<Video> results) {
            this.results = results;
        }
    }
}