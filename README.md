# 🎬 TMDB Movie Search System

A Java console application for searching movies using The Movie Database (TMDB) API with pagination, detailed movie information, and YouTube trailers.

## ✨ Features

- 🔍 **Movie Search**: Search for movies by title with real-time API integration
- 📄 **Pagination**: Navigate through search results (up to 10 pages)
- 🎥 **YouTube Trailers**: Display trailer URLs for each movie
- 📋 **Movie Details**: View comprehensive movie information including:
    - Title, Release Date, Rating, Runtime
    - Genres, Director, Cast (top 5)
    - Overview/Synopsis
    - YouTube Trailer Link
- 🛡️ **Error Handling**: Graceful error handling for all user inputs and API failures
- 📊 **Formatted Tables**: Beautiful console output using Unicode box borders
- 🏗️ **Clean Architecture**: Separation of concerns with organized package structure

## 🛠️ Technology Stack

- **Language**: Java 25
- **Build Tool**: Gradle with Gradle Wrapper
- **HTTP Client**: Java's built-in `HttpClient`
- **JSON Parsing**: Gson 2.10.1
- **Table Formatting**: Text Table Formatter 1.2.4
- **API**: TMDB (The Movie Database)

## 🚀 Getting Started

### Prerequisites

- Java 25 or higher
- TMDB API account with Read Access Token

### Installation & Setup

1. **Clone or download the project**
```bash
   cd movie-search-tmdb
```

2. **Create config.properties**
```bash
   cp config.properties.example config.properties
```
(Or manually create it)

3. **Add your TMDB API token**
  - Get your Read Access Token from: https://www.themoviedb.org/settings/api
  - Edit `config.properties`:
```properties
   TMDB_ACCESS_TOKEN=your_actual_token_here
```

4. **Run the application**
```bash
   ./gradlew run
```
(On Windows: `gradlew.bat run`)

5. **Or build and run**
```bash
   ./gradlew build
   java -cp build/classes/java/main com.moviesearch.Main
```
