package com.moviesearch.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// represents the complete response from TMDB search API

public class SearchResponse {

    private int page;
    private List<MovieSummary> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    // constructors
    public SearchResponse() {}

    public SearchResponse(int page, List<MovieSummary> results,
                          int totalPages, int totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public int getPage() {
        return page;
    }

    public List<MovieSummary> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setResults(List<MovieSummary> results) {
        this.results = results;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}