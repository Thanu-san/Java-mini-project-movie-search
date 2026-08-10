package com.moviesearch.ui;

import com.moviesearch.model.MovieDetail;
import com.moviesearch.model.MovieSummary;
import com.moviesearch.model.SearchResponse;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;
import java.util.Scanner;

// console display and user interface
public class ConsoleUI {

    private static final String MENU_PROMPT = "Choose an option: ";
    private static final String PAGE_PROMPT_BASE = "Enter page number (1-";
    private static final String MOVIE_ID_PROMPT = "\nEnter movie ID: ";

    private final Scanner scanner;

   // console UI with a shared Scanner
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    // display search results
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

        // create table with 5 columns using ASCII box borders
        Table table = new Table(5, BorderStyle.UNICODE_BOX_WIDE);

        // add headers
        table.addCell("ID");
        table.addCell("Title");
        table.addCell("Release");
        table.addCell("Rating");
        table.addCell("Trailer");

        // add each movie row
        for (MovieSummary movie : movies) {
            table.addCell(String.valueOf(movie.getId()));
            table.addCell(truncate(movie.getTitle(), 30));
            table.addCell(movie.getReleaseDate() != null ? movie.getReleaseDate() : "N/A");
            table.addCell(String.format("%.1f", movie.getRating()));

            // show YouTube URL or "N/A"
            String trailer = "N/A";
            if (movie.getTrailerUrl() != null && !movie.getTrailerUrl().isEmpty()) {
                trailer = truncate(movie.getTrailerUrl(), 45);
            }
            table.addCell(trailer);
        }

        // print table
        System.out.println();
        System.out.println(table.render());

        // print pagination info
        printPaginationInfo(response, currentPage);
    }


    // pagination format
    private void printPaginationInfo(SearchResponse response, int currentPage) {
        System.out.printf("Page %d of %d | Total Results: %d\n\n",
                currentPage, response.getTotalPages(), response.getTotalResults());
    }


    // truncate text to maximum length and add ellipsis
    private String truncate(String text, int maxLength) {
        if (text == null) return "N/A";
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }


    public void showMessage(String message) {
        System.out.println(message);
    }


    public void showError(String message) {
        System.out.println("❌ " + message);
    }

    public void showSuccess(String message) {
        System.out.println("✅ " + message);
    }


    // display menu option
    public void displayMenu() {
        System.out.println("\n[n]  Next Page");
        System.out.println("[p]  Previous Page");
        System.out.println("[g]  Go To Page");
        System.out.println("[md] Movie Detail");
        System.out.println("[b]  Back");
        System.out.println("[e]  Exit\n");
    }


    public String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }


    // valid page number
    public int getPageNumber(int maxPages) {
        while (true) {
            String input = getUserInput("Enter page number (1-" + maxPages + "): ");

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


    // display detailed information about a movie in table format
    public void displayMovieDetail(MovieDetail detail) {
        System.out.println();

        // create a table for movie details
        Table detailTable = new Table(2, BorderStyle.UNICODE_BOX);

        // add detail rows
        detailTable.addCell("ID");
        detailTable.addCell(String.valueOf(detail.getId()));

        detailTable.addCell("Title");
        detailTable.addCell(detail.getTitle());

        detailTable.addCell("Release");
        detailTable.addCell(detail.getReleaseDate() != null ? detail.getReleaseDate() : "N/A");

        detailTable.addCell("Rating");
        detailTable.addCell(String.format("%.1f/10", detail.getRating()));

        detailTable.addCell("Runtime");
        detailTable.addCell(detail.getRuntime() > 0 ? detail.getRuntime() + " minutes" : "N/A");

        String genres = "N/A";
        if (detail.getGenres() != null && !detail.getGenres().isEmpty()) {
            genres = detail.getGenres().stream()
                    .map(MovieDetail.Genre::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");
        }

        detailTable.addCell("Genres");
        detailTable.addCell(genres);

        String director = getDirector(detail);
        detailTable.addCell("Director");
        detailTable.addCell(director);

        String cast = getTopCast(detail, 5);
        detailTable.addCell("Cast");
        detailTable.addCell(cast);

        String trailer = detail.getTrailerUrl() != null ? detail.getTrailerUrl() : "N/A";
        detailTable.addCell("Trailer");
        detailTable.addCell(truncate(trailer, 50));

        String overview = truncate(detail.getOverview(), 80);
        detailTable.addCell("Overview");
        detailTable.addCell(overview);

        System.out.println(detailTable.render());
        System.out.println();

        getUserInput("Press Enter to return.");
    }


    // extract the director name from movie credits
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


    // get the top N cast members as a comma-separated string
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

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}