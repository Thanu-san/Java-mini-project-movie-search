package com.moviesearch.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static final String TOKEN_KEY = "TMDB_ACCESS_TOKEN";

    private final String accessToken;

    public ApiConfig() {
        this.accessToken = loadToken();
    }

    private String loadToken() {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new RuntimeException(
                        "❌ config.properties not found!\n" +
                                "   1. Copy config.properties.example to config.properties\n" +
                                "   2. Add your TMDB API token to config.properties\n" +
                                "   3. Do NOT commit config.properties to Git"
                );
            }

            properties.load(input);
            String token = properties.getProperty(TOKEN_KEY);

            if (token == null || token.isEmpty()) {
                throw new RuntimeException(
                        "❌ TMDB_ACCESS_TOKEN not found in config.properties!\n" +
                                "   Add: TMDB_ACCESS_TOKEN=your_token_here"
                );
            }

            return token;

        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to read config.properties: " + e.getMessage());
        }
    }

    public String getAccessToken() {
        return accessToken;
    }
}