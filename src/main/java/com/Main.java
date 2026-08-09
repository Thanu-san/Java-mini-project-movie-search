package com;

import com.moviesearch.config.ApiConfig;
import com.moviesearch.config.ApiException;
import com.moviesearch.model.MovieDetail;
import com.moviesearch.model.SearchResponse;
import com.moviesearch.model.SearchMovie;
import com.moviesearch.service.TmdbService;
import com.moviesearch.ui.ConsoleUI;

public class Main {

    private static final TmdbService tmdbService;
    private static final ConsoleUI ui = new ConsoleUI();
    private static SearchMovie session;

    // Static initialization
    static {
        try {
            ApiConfig config = new ApiConfig();
            tmdbService = new TmdbService(config);
        } catch (Exception e) {
            System.err.println(" FATAL ERROR: Failed to initialize application");
            System.err.println("   " + e.getMessage());
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(" TMDB Movie Search System\n");
            mainMenu();
        } catch (RuntimeException e) {
            ui.showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ui.close();  // Close the Scanner resource
        }
    }

    /**
     * Main menu: Ask user for movie title
     */
    private static void mainMenu() {
        while (true) {
            String query = ui.getUserInput("[-] Enter movie title (or 'exit' to quit): ");

            if (query.equalsIgnoreCase("exit")) {
                ui.showMessage("Goodbye!");
                System.exit(0);
            }

            if (query.trim().isEmpty()) {
                ui.showError("Please enter a movie title.");
                continue;
            }

            // Search and show results
            searchAndDisplay(query);
        }
    }

    /**
     * Search for movies and display results
     */
    private static void searchAndDisplay(String query) {
        try {
            ui.showMessage("Searching for '" + query + "'...\n");

            // Fetch first page
            String jsonResponse = tmdbService.searchMovies(query, 1);
            SearchResponse response = tmdbService.parseSearchResponse(jsonResponse);

            if (response.getResults().isEmpty()) {
                ui.showError("No movies found for '" + query + "'");
                return;
            }

            // Fetch trailers for all movies on this page
            tmdbService.attachTrailersToMovies(response);

            // Create session
            session = new SearchMovie(query, response);

            // Show results and pagination menu
            resultsMenu();

        } catch (ApiException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Results menu: Navigation and options
     */
    private static void resultsMenu() {
        while (true) {
            try {
                // Display current page
                ui.displayMovieTable(session.getResponse(), session.getCurrentPage());

                // Display menu
                ui.displayMenu();

                String option = ui.getUserInput("Choose an option: ").toLowerCase().trim();

                switch (option) {
                    case "n" -> handleNextPage();
                    case "p" -> handlePreviousPage();
                    case "g" -> handleGoToPage();
                    case "md" -> handleMovieDetail();
                    case "b" -> {
                        return; // Back to main menu
                    }
                    case "e" -> {
                        ui.showMessage("Goodbye! 👋");
                        System.exit(0);
                    }
                    default -> ui.showError("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                ui.showError("An error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Handle next page
     */
    private static void handleNextPage() {
        if (!session.canGoNext()) {
            ui.showMessage("You are already on the last page.\n");
            return;
        }

        session.goNext();
        fetchAndUpdatePage();
    }

    /**
     * Handle previous page
     */
    private static void handlePreviousPage() {
        if (!session.canGoPrevious()) {
            ui.showMessage("You are already on the first page.\n");
            return;
        }

        session.goPrevious();
        fetchAndUpdatePage();
    }

    /**
     * Handle go to page
     */
    private static void handleGoToPage() {
        int maxPages = session.getMaxPages();
        int pageNumber = ui.getPageNumber(maxPages);

        session.setCurrentPage(pageNumber);
        fetchAndUpdatePage();
    }

    /**
     * Fetch and update page results
     */
    private static void fetchAndUpdatePage() {
        try {
            ui.showMessage("Fetching page " + session.getCurrentPage() + "...\n");

            String jsonResponse = tmdbService.searchMovies(
                    session.getQuery(),
                    session.getCurrentPage()
            );
            SearchResponse response = tmdbService.parseSearchResponse(jsonResponse);

            // Fetch trailers for all movies on this page
            tmdbService.attachTrailersToMovies(response);

            session.setResponse(response);

        } catch (ApiException e) {
            ui.showError(e.getMessage());
            // Reset to previous page on error
            session.goPrevious();
        }
    }

    /**
     * Handle movie detail request
     */
    private static void handleMovieDetail() {
        String input = ui.getUserInput("\nEnter movie ID: ");

        try {
            int movieId = Integer.parseInt(input);

            ui.showMessage("Fetching movie details...\n");

            String jsonResponse = tmdbService.getMovieDetail(movieId);
            MovieDetail detail = tmdbService.parseMovieDetail(jsonResponse);

            ui.displayMovieDetail(detail);

        } catch (NumberFormatException e) {
            ui.showError("Invalid movie ID. Please enter a number.");
        } catch (ApiException e) {
            ui.showError(e.getMessage());
        }
    }
}

