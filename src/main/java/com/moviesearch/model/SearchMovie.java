package com.moviesearch.model;


/**
 * Represents the current search session
 * Tracks: query, current page, search results
 */
public class SearchMovie {

    private String query;
    private int currentPage;
    private SearchResponse response;
    private static final int MAX_PAGES = 10;  // TMDB limit

    /**
     * Constructor
     */
    public SearchMovie(String query, SearchResponse response) {
        this.query = query;
        this.currentPage = 1;
        this.response = response;
    }

    /**
     * Get the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Get current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Set current page number
     */
    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    /**
     * Get search response
     */
    public SearchResponse getResponse() {
        return response;
    }

    /**
     * Update response (after fetching new page)
     */
    public void setResponse(SearchResponse response) {
        this.response = response;
    }

    /**
     * Get max pages (capped at 10)
     */
    public int getMaxPages() {
        int totalPages = response.getTotalPages();
        return Math.min(totalPages, MAX_PAGES);
    }

    /**
     * Check if we can go to next page
     */
    public boolean canGoNext() {
        return currentPage < getMaxPages();
    }

    /**
     * Check if we can go to previous page
     */
    public boolean canGoPrevious() {
        return currentPage > 1;
    }

    /**
     * Go to next page
     */
    public void goNext() {
        if (canGoNext()) {
            currentPage++;
        }
    }

    /**
     * Go to previous page
     */
    public void goPrevious() {
        if (canGoPrevious()) {
            currentPage--;
        }
    }
}
