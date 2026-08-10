package com.moviesearch.model;


// represents the current search session (query , page , search result)

public class SearchMovie {

    private String query;
    private int currentPage;
    private SearchResponse response;
    private static final int MAX_PAGES = 10;


     // constructor
    public SearchMovie(String query, SearchResponse response) {
        this.query = query;
        this.currentPage = 1;
        this.response = response;
    }

    public String getQuery() {
        return query;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public SearchResponse getResponse() {
        return response;
    }

    public void setResponse(SearchResponse response) {
        this.response = response;
    }

    public int getMaxPages() {
        int totalPages = response.getTotalPages();
        return Math.min(totalPages, MAX_PAGES);
    }

    public boolean canGoNext() {
        return currentPage < getMaxPages();
    }

    public boolean canGoPrevious() {
        return currentPage > 1;
    }

    public void goNext() {
        if (canGoNext()) {
            currentPage++;
        }
    }

    public void goPrevious() {
        if (canGoPrevious()) {
            currentPage--;
        }
    }
}
