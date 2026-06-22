package com.example.weatherforecastapp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.FavoriteCity;
import com.example.weatherforecastapp.domain.model.ForecastDay;
import com.example.weatherforecastapp.presentation.state.UiState;

import java.util.List;

/**
 * Repository interface for weather data operations.
 * Abstracts data sources (network, local database) from the rest of the app.
 * Part of the Repository pattern for clean architecture.
 */
public interface WeatherRepository {

    // ==================== Weather Data ====================

    /**
     * Get current weather for a city as LiveData with UiState wrapper.
     * Handles loading/success/error states automatically.
     */
    LiveData<UiState<CurrentWeather>> getCurrentWeather(String city);

    /**
     * Get forecast for a city as LiveData with UiState wrapper.
     */
    LiveData<UiState<List<ForecastDay>>> getForecast(String city);

    /**
     * Trigger a refresh of weather data for a city.
     * Results will be posted to the LiveData returned by getCurrentWeather/getForecast.
     */
    void refreshWeather(String city);

    /**
     * Trigger a refresh of forecast data for a city.
     */
    void refreshForecast(String city);

    // ==================== Favorite Cities ====================

    /**
     * Observe all favorite cities as LiveData.
     * Updates automatically when favorites change.
     */
    LiveData<List<FavoriteCity>> observeFavorites();

    /**
     * Check if a city is in favorites.
     * Returns LiveData<Boolean> that updates when the favorite status changes.
     */
    LiveData<Boolean> isFavorite(String cityName);

    /**
     * Add a city to favorites.
     * @param cityName The name of the city to add
     */
    void addFavorite(String cityName);

    /**
     * Remove a city from favorites.
     * @param cityName The name of the city to remove
     */
    void removeFavorite(String cityName);

    /**
     * Remove a city from favorites by domain model.
     */
    void removeFavorite(FavoriteCity city);

    // ==================== Sync Operations (for WorkManager) ====================

    /**
     * Get all favorite cities synchronously (for background workers).
     */
    List<FavoriteCity> getFavoritesSync();

    /**
     * Fetch current weather synchronously (for background workers).
     * @return CurrentWeather or null if failed
     */
    CurrentWeather fetchCurrentWeatherSync(String city);
}
