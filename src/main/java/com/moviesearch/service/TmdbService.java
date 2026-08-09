package com.moviesearch.service;

import com.moviesearch.config.ApiConfig;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.moviesearch.config.ApiException;
import com.moviesearch.model.MovieSummary;
import com.moviesearch.model.SearchResponse;
import com.moviesearch.model.MovieDetail;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class TmdbService {

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String SEARCH_MOVIE_ENDPOINT = "/search/movie";

    private final HttpClient httpClient;
    private final ApiConfig apiConfig;

    public TmdbService(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Search for movies by title
     *
     * @param query Movie title to search (e.g., "batman")
     * @param page Page number (1-10)
     * @return Raw JSON response from TMDB API
     * @throws ApiException if API call fails
     */
    public String searchMovies(String query, int page) throws ApiException {
        try {
            // Validate input
            if (query == null || query.trim().isEmpty()) {
                throw new ApiException("Movie title cannot be empty");
            }

            if (page < 1 || page > 10) {
                throw new ApiException("Page number must be between 1 and 10");
            }

            // Build the URL
            String url = BASE_URL + SEARCH_MOVIE_ENDPOINT
                    + "?query=" + encodeUrl(query)
                    + "&page=" + page;

            // Create HTTP request with authorization header
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiConfig.getAccessToken())
                    .GET()
                    .build();

            // Send request and get response
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // Handle different status codes
            int statusCode = response.statusCode();

            switch (statusCode) {
                case 200:
                    // Success
                    return response.body();

                case 401:
                    throw new ApiException(
                            "Authentication failed: Invalid or expired API token",
                            401
                    );

                case 404:
                    throw new ApiException(
                            "TMDB API endpoint not found",
                            404
                    );

                case 429:
                    throw new ApiException(
                            "Too many requests: Rate limit exceeded. Wait a moment and try again.",
                            429
                    );

                case 500:
                case 502:
                case 503:
                    throw new ApiException(
                            "TMDB server error: Please try again later",
                            statusCode
                    );

                default:
                    throw new ApiException(
                            "API Error " + statusCode + ": " + response.body(),
                            statusCode
                    );
            }

        } catch (java.net.ConnectException e) {
            throw new ApiException(
                    "Network error: Cannot connect to TMDB. Check your internet connection.",
                    e
            );
        } catch (java.net.SocketTimeoutException e) {
            throw new ApiException(
                    "Connection timeout: TMDB server is slow or unreachable.",
                    e
            );
        } catch (ApiException e) {
            throw e;  // Re-throw our custom exceptions
        } catch (Exception e) {
            throw new ApiException(
                    "Unexpected error: " + e.getMessage(),
                    e
            );
        }
    }

    private String encodeUrl(String text) {
        return text.replace(" ", "%20");
    }

    /**
     * Parse JSON response into SearchResponse object
     *
     * @param jsonResponse Raw JSON string from TMDB
     * @return SearchResponse object with parsed data
     * @throws ApiException if parsing fails
     */
    public SearchResponse parseSearchResponse(String jsonResponse) throws ApiException {
        try {
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new ApiException("Empty response from API");
            }

            Gson gson = new Gson();
            SearchResponse response = gson.fromJson(jsonResponse, SearchResponse.class);

            if (response == null) {
                throw new ApiException("Failed to parse search response");
            }

            // Validate parsed data
            if (response.getResults() == null) {
                throw new ApiException("Invalid response structure: missing results");
            }

            return response;

        } catch (com.google.gson.JsonSyntaxException e) {
            throw new ApiException("Invalid JSON format in response: " + e.getMessage(), e);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Error parsing JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Get detailed movie information by ID
     *
     * @param movieId Movie ID
     * @return Raw JSON response
     * @throws ApiException if API call fails
     */
    public String getMovieDetail(int movieId) throws ApiException {
        try {
            if (movieId <= 0) {
                throw new ApiException("Invalid movie ID. Must be greater than 0.");
            }

            String url = BASE_URL + "/movie/" + movieId
                    + "?append_to_response=videos,credits";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiConfig.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int statusCode = response.statusCode();

            if (statusCode == 200) {
                return response.body();
            } else if (statusCode == 404) {
                throw new ApiException("Movie not found. Invalid movie ID: " + movieId, 404);
            } else if (statusCode == 401) {
                throw new ApiException("Authentication failed: Invalid API token", 401);
            } else {
                throw new ApiException("API Error " + statusCode, statusCode);
            }

        } catch (java.net.ConnectException e) {
            throw new ApiException("Network error: Cannot connect to TMDB", e);
        } catch (java.net.SocketTimeoutException e) {
            throw new ApiException("Connection timeout: TMDB server is unreachable", e);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Failed to get movie details: " + e.getMessage(), e);
        }
    }

    /**
     * Parse movie detail JSON response
     *
     * @param jsonResponse Raw JSON from API
     * @return MovieDetail object
     * @throws ApiException if parsing fails
     */
    public MovieDetail parseMovieDetail(String jsonResponse) throws ApiException {
        try {
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new ApiException("Empty response from API");
            }

            Gson gson = new Gson();
            MovieDetail detail = gson.fromJson(jsonResponse, MovieDetail.class);

            if (detail == null || detail.getId() == 0) {
                throw new ApiException("Failed to parse movie details");
            }

            // Extract trailer URL from videos
            detail.setTrailerUrl(extractTrailerUrl(jsonResponse));

            return detail;

        } catch (com.google.gson.JsonSyntaxException e) {
            throw new ApiException("Invalid JSON format: " + e.getMessage(), e);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Error parsing movie details: " + e.getMessage(), e);
        }
    }

    /**
     * Extract trailer URL from videos JSON
     */
    private String extractTrailerUrl(String jsonResponse) {
        try {
            Gson gson = new Gson();
            com.google.gson.JsonObject json = gson.fromJson(jsonResponse, com.google.gson.JsonObject.class);

            if (!json.has("videos")) {
                return null;
            }

            com.google.gson.JsonObject videos = json.getAsJsonObject("videos");
            com.google.gson.JsonArray results = videos.getAsJsonArray("results");

            if (results == null || results.size() == 0) {
                return null;
            }

            // Look for YouTube trailer
            for (int i = 0; i < results.size(); i++) {
                com.google.gson.JsonObject video = results.get(i).getAsJsonObject();

                String site = video.has("site") ? video.get("site").getAsString() : "";
                String type = video.has("type") ? video.get("type").getAsString() : "";
                String key = video.has("key") ? video.get("key").getAsString() : "";

                if ("YouTube".equals(site) && "Trailer".equals(type) && !key.isEmpty()) {
                    return "https://www.youtube.com/watch?v=" + key;
                }
            }

            return null;

        } catch (Exception e) {
            return null;  // If extraction fails, just return null
        }
    }

    /**
     * Get the trailer URL for a specific movie
     * Fetches video data and extracts YouTube trailer link
     *
     * @param movieId Movie ID
     * @return YouTube trailer URL, or null if not found
     */
    public String getMovieTrailer(int movieId) {
        try {
            String url = BASE_URL + "/movie/" + movieId + "/videos";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiConfig.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                return extractTrailerFromVideos(response.body());
            }

            return null;

        } catch (Exception e) {
            // If trailer fetch fails, just return null (not critical)
            return null;
        }
    }

    /**
     * Extract YouTube trailer URL from videos JSON response
     */
    private String extractTrailerFromVideos(String jsonResponse) {
        try {
            Gson gson = new Gson();
            com.google.gson.JsonObject json = gson.fromJson(jsonResponse, com.google.gson.JsonObject.class);

            com.google.gson.JsonArray results = json.getAsJsonArray("results");

            if (results == null || results.size() == 0) {
                return null;
            }

            // Look for YouTube trailer
            for (int i = 0; i < results.size(); i++) {
                com.google.gson.JsonObject video = results.get(i).getAsJsonObject();

                String site = video.has("site") ? video.get("site").getAsString() : "";
                String type = video.has("type") ? video.get("type").getAsString() : "";
                String key = video.has("key") ? video.get("key").getAsString() : "";

                if ("YouTube".equals(site) && "Trailer".equals(type) && !key.isEmpty()) {
                    return "https://www.youtube.com/watch?v=" + key;
                }
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch and attach trailer URLs to all movies in search results
     * Note: This makes additional API calls (one per movie)
     *
     * @param response SearchResponse with movies
     */
    public void attachTrailersToMovies(SearchResponse response) {
        if (response == null || response.getResults() == null) {
            return;
        }

        System.out.print("⏳ Fetching trailers");

        for (MovieSummary movie : response.getResults()) {
            System.out.print(".");  // Progress indicator
            String trailerUrl = getMovieTrailer(movie.getId());
            movie.setTrailerUrl(trailerUrl);
        }

        System.out.println(" Done!\n");
    }

}