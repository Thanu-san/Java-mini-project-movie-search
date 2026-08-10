package com.moviesearch.ui;

import com.moviesearch.model.MovieDetail;
import com.moviesearch.model.MovieSummary;
import com.moviesearch.model.SearchResponse;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console display and user interface.
 * Responsible for: tables, menus, messages, input handling.
 */
public class ConsoleUI {

    private static final int TABLE_WIDTH = 150;
    private static final String SEPARATOR = "-";
    private static final String MENU_PROMPT = "Choose an option: ";
    private static final String PAGE_PROMPT_BASE = "Enter page number (1-";
    private static final String MOVIE_ID_PROMPT = "\nEnter movie ID: ";

    private final Scanner scanner;

    /**
     * Constructor: Initialize console UI with a shared Scanner
     */
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Display search results as a formatted table
     */
    public void displayMovieTable(SearchResponse response, int currentPage) {
        if (response == null) {
            showError("Response is null");
            return;
        }

        if (response.getResults() == null || response.getResults().isEmpty()) {
            showError("No movies found.");
            return;
        }

        List<MovieSummary> movies = response.getResults();

        // Print header
        printTableHeader();

        // Print each movie
        for (MovieSummary movie : movies) {
            printTableRow(movie);
        }

        // Print separator
        printSeparator();

        // Print pagination info
        printPaginationInfo(response, currentPage);
    }

    /**
     * Print the table header row
     */
    private void printTableHeader() {
        printSeparator();
        System.out.printf("%-8s %-30s %-14s %-9s %-50s%n",
                "ID", "Title", "Release", "Rating", "Trailer");
        printSeparator();
    }

    /**
     * Print one movie row
     */
    private void printTableRow(MovieSummary movie) {
        String title = truncate(movie.getTitle(), 30);
        String release = movie.getReleaseDate() != null ? movie.getReleaseDate() : "N/A";
        String rating = String.format("%.1f", movie.getRating());

        // Show "YouTube" link or "N/A"
        String trailer = "N/A";
        if (movie.getTrailerUrl() != null && !movie.getTrailerUrl().isEmpty()) {
            trailer = movie.getTrailerUrl();
        }

        System.out.printf("%-8d %-30s %-14s %-9s %-50s%n",
                movie.getId(), title, release, rating, trailer);
    }

    /**
     * Print a line separator
     */
    private void printSeparator() {
        System.out.println(SEPARATOR.repeat(TABLE_WIDTH));
    }

    /**
     * Print pagination information
     */
    private void printPaginationInfo(SearchResponse response, int currentPage) {
        System.out.printf("\nPage %d of %d | Total Results: %d\n\n",
                currentPage, response.getTotalPages(), response.getTotalResults());
    }

    /**
     * Truncate text to maximum length and add ellipsis
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "N/A";
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Display a simple message
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Display an error message with prefix
     */
    public void showError(String message) {
        System.out.println( message);
    }

    /**
     * Display a success message with prefix
     */
    public void showSuccess(String message) {
        System.out.println( message);
    }

    /**
     * Display the navigation menu options
     */
    public void displayMenu() {
        System.out.println("\n[n]  Next Page");
        System.out.println("[p]  Previous Page");
        System.out.println("[g]  Go To Page");
        System.out.println("[md] Movie Detail");
        System.out.println("[b]  Back");
        System.out.println("[e]  Exit\n");
    }

    /**
     * Read user input from console with a prompt
     */
    public String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Ask user for a valid page number within the specified range
     */
    public int getPageNumber(int maxPages) {
        while (true) {
            String input = getUserInput(PAGE_PROMPT_BASE + maxPages + "): ");

            try {
                int page = Integer.parseInt(input);

                if (page < 1 || page > maxPages) {
                    showError("Page number must be between 1 and " + maxPages);
                    continue;
                }

                return page;

            } catch (NumberFormatException e) {
                showError("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Display detailed information about a movie
     */
    public void displayMovieDetail(MovieDetail detail) {
        System.out.println("\n========== MOVIE DETAIL ==========\n");

        System.out.printf("ID          : %d%n", detail.getId());
        System.out.printf("Title       : %s%n", detail.getTitle());
        System.out.printf("Release     : %s%n",
                detail.getReleaseDate() != null ? detail.getReleaseDate() : "N/A");
        System.out.printf("Rating      : %.1f%n", detail.getRating());
        System.out.printf("Runtime     : %s%n",
                detail.getRuntime() > 0 ? detail.getRuntime() + " minutes" : "N/A");

        // Genres
        if (detail.getGenres() != null && !detail.getGenres().isEmpty()) {
            String genres = detail.getGenres().stream()
                    .map(MovieDetail.Genre::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");
            System.out.printf("Genres      : %s%n", genres);
        } else {
            System.out.println("Genres      : N/A");
        }

        // Director
        String director = getDirector(detail);
        System.out.printf("Director    : %s%n", director);

        // Cast
        String cast = getTopCast(detail, 5);
        System.out.printf("Cast        : %s%n", cast);

        // Overview
        String overview = truncate(detail.getOverview(), 100);
        System.out.printf("Overview    : %s%n", overview);

        // Trailer
        String trailer = detail.getTrailerUrl() != null ? detail.getTrailerUrl() : "N/A";
        System.out.printf("Trailer     : %s%n", trailer);

        System.out.println("\n==================================\n");

        getUserInput("Press Enter to return.");
    }

    /**
     * Extract the director name from movie credits
     */
    private String getDirector(MovieDetail detail) {
        if (detail.getCredits() == null || detail.getCredits().getCrew() == null) {
            return "N/A";
        }

        return detail.getCredits().getCrew().stream()
                .filter(crew -> "Director".equals(crew.getJob()))
                .map(MovieDetail.Crew::getName)
                .findFirst()
                .orElse("N/A");
    }

    /**
     * Get the top N cast members as a comma-separated string
     */
    private String getTopCast(MovieDetail detail, int count) {
        if (detail.getCredits() == null || detail.getCredits().getCast() == null) {
            return "N/A";
        }

        return detail.getCredits().getCast().stream()
                .limit(count)
                .map(MovieDetail.Cast::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");
    }

    /**
     * Close the scanner resource
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}