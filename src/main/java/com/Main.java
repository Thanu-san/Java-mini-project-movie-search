package com;

import com.moviesearch.config.ApiConfig;
import com.moviesearch.config.ApiException;
import com.moviesearch.model.MovieDetail;
import com.moviesearch.model.SearchResponse;
import com.moviesearch.model.SearchMovie;
import com.moviesearch.service.TmdbService;
import com.moviesearch.ui.ConsoleUI;

import java.nio.charset.StandardCharsets;

public class Main {

    private static final TmdbService tmdbService;
    private static final ConsoleUI ui = new ConsoleUI();
    private static SearchMovie session;

    // static initialization
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
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            // Ignore
        }
        try {
            System.out.println("""
                     ____    ____                  _           ______                                __      \s
                    |_   \\  /   _|                (_)        .' ____ \\                              [  |     \s
                      |   \\/   |   .--.   _   __  __  .---.  | (___ \\_| .---.  ,--.   _ .--.  .---.  | |--.  \s
                      | |\\  /| | / .'`\\ \\[ \\ [  ][  |/ /__\\\\  _.____`. / /__\\\\`'_\\ : [ `/'`\\]/ /'`\\] | .-. | \s
                     _| |_\\/_| |_| \\__. | \\ \\/ /  | || \\__., | \\____) || \\__.,// | |, | |    | \\__.  | | | | \s
                    |_____||_____|'.__.'   \\__/  [___]'.__.'  \\______.' '.__.'\\'-;__/[___]   '.___.'[___]|__]\s
                   \s
       \s""");

            mainMenu();

        } catch (RuntimeException e) {
            ui.showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // main menu
    private static void mainMenu() {
        while (true) {
            String query = ui.getUserInput("[<+>] Enter movie title (or input 'exit' to quit): ");

            if (query.equalsIgnoreCase("exit")) {
                ui.showMessage("Goodbye !! I love you 3000 ");
                System.exit(0);
            }

            if (query.trim().isEmpty()) {
                ui.showError("Please enter a movie title.");
                continue;
            }
            searchAndDisplay(query);
        }
    }

    // search for movies and display results
    private static void searchAndDisplay(String query) {
        try {
            ui.showMessage("Searching for '" + query + "'...\n");

            // fetch for the first page
            String jsonResponse = tmdbService.searchMovies(query, 1);
            SearchResponse response = tmdbService.parseSearchResponse(jsonResponse);

            if (response.getResults().isEmpty()) {
                ui.showError("No movies found for '" + query + "'");
                return;
            }

            // fetch trailers for all movies on this page
            tmdbService.attachTrailersToMovies(response);
            session = new SearchMovie(query, response);

            // show the results and pagination menu
            resultsMenu();
        } catch (ApiException e) {
            ui.showError(e.getMessage());
        }
    }

    private static void resultsMenu() {
        while (true) {
            try {
                ui.displayMovieTable(session.getResponse(), session.getCurrentPage());

                ui.displayMenu();

                String option = ui.getUserInput("Choose an option: ").toLowerCase().trim();

                switch (option) {
                    case "n" -> handleNextPage();
                    case "p" -> handlePreviousPage();
                    case "g" -> handleGoToPage();
                    case "md" -> handleMovieDetail();
                    case "b" -> {
                        return;
                    }
                    case "e" -> {
                        ui.showMessage("Goodbye! May the force be with you !");
                        System.exit(0);
                    }
                    default -> ui.showError("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                ui.showError("An error occurred !!!! : " + e.getMessage());
            }
        }
    }

    // handle next page error
    private static void handleNextPage() {
        if (!session.canGoNext()) {
            ui.showMessage("You are already on the last page.\n");
            return;
        }

        session.goNext();
        fetchAndUpdatePage();
    }

    // handle previous page error
    private static void handlePreviousPage() {
        if (!session.canGoPrevious()) {
            ui.showMessage("You are already on the first page.\n");
            return;
        }

        session.goPrevious();
        fetchAndUpdatePage();
    }

    // handle for go to the page
    private static void handleGoToPage() {
        int maxPages = session.getMaxPages();
        int pageNumber = ui.getPageNumber(maxPages);

        session.setCurrentPage(pageNumber);
        fetchAndUpdatePage();
    }

    // fetching Api to the page
    private static void fetchAndUpdatePage() {
        try {
            ui.showMessage("Fetching page " + session.getCurrentPage() + "...\n");

            String jsonResponse = tmdbService.searchMovies(
                    session.getQuery(),
                    session.getCurrentPage()
            );
            SearchResponse response = tmdbService.parseSearchResponse(jsonResponse);

            // fetch trailers for all the movies
            tmdbService.attachTrailersToMovies(response);

            session.setResponse(response);

        } catch (ApiException e) {
            ui.showError(e.getMessage());
            session.goPrevious();
        }
    }

    // handle for movie detail
    private static void handleMovieDetail() {
        String input = ui.getUserInput("\nEnter movie ID: ");

        try {
            int movieId = Integer.parseInt(input);

            ui.showMessage("Fetching movie details...\n");

            // fetch detail info
            String jsonResponse = tmdbService.getMovieDetail(movieId);
            MovieDetail detail = tmdbService.parseMovieDetail(jsonResponse);

            // fetch and attach trailer
            String trailerUrl = tmdbService.getMovieTrailer(movieId);
            detail.setTrailerUrl(trailerUrl);

            // manually parse and attach credits from JSON
            tmdbService.attachCreditsToMovieDetail(detail, jsonResponse);

            ui.displayMovieDetail(detail);

        } catch (NumberFormatException e) {
            ui.showError("Invalid movie ID. Please enter a number.");
        } catch (ApiException e) {
            ui.showError(e.getMessage());
        }
    }
}

