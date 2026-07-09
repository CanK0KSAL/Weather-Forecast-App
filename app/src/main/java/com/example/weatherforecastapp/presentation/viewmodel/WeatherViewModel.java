package com.example.weatherforecastapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.weatherforecastapp.data.repository.WeatherRepository;
import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.FavoriteCity;
import com.example.weatherforecastapp.domain.model.ForecastDay;
import com.example.weatherforecastapp.presentation.state.UiState;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for weather-related screens.
 * Exposes UI state via LiveData following MVVM pattern.
 * All data flows through this ViewModel - UI never accesses Repository directly.
 */
@HiltViewModel
public class WeatherViewModel extends ViewModel {

    private final WeatherRepository repository;

    // Current search/display city
    private final MutableLiveData<String> currentCity = new MutableLiveData<>();

    // Weather state - uses MediatorLiveData to switch sources when city changes
    private final MediatorLiveData<UiState<CurrentWeather>> currentWeatherState = new MediatorLiveData<>();
    private LiveData<UiState<CurrentWeather>> currentWeatherSource;

    // Forecast state
    private final MediatorLiveData<UiState<List<ForecastDay>>> forecastState = new MediatorLiveData<>();
    private LiveData<UiState<List<ForecastDay>>> forecastSource;

    // Favorites from Room - automatically updates
    private final LiveData<List<FavoriteCity>> favorites;

    // Current city favorite status
    private final MediatorLiveData<Boolean> isFavorite = new MediatorLiveData<>();
    private LiveData<Boolean> isFavoriteSource;

    @Inject
    public WeatherViewModel(WeatherRepository repository) {
        this.repository = repository;
        this.favorites = repository.observeFavorites();

        // Initialize with empty state
        currentWeatherState.setValue(null);
        forecastState.setValue(null);
        isFavorite.setValue(false);
    }

    // ==================== Weather Data ====================

    /**
     * Get current weather state as LiveData.
     * Observe this to react to loading/success/error states.
     */
    public LiveData<UiState<CurrentWeather>> getCurrentWeatherState() {
        return currentWeatherState;
    }

    /**
     * Get forecast state as LiveData.
     */
    public LiveData<UiState<List<ForecastDay>>> getForecastState() {
        return forecastState;
    }

    /**
     * Get the current city being displayed.
     */
    public LiveData<String> getCurrentCity() {
        return currentCity;
    }

    /**
     * Load weather data for a city.
     * This will update both currentWeatherState and forecastState.
     */
    public void loadWeatherForCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            currentWeatherState.setValue(UiState.error("Please enter a city name"));
            return;
        }

        currentCity.setValue(city);

        // Remove old sources if they exist
        if (currentWeatherSource != null) {
            currentWeatherState.removeSource(currentWeatherSource);
        }
        if (forecastSource != null) {
            forecastState.removeSource(forecastSource);
        }
        if (isFavoriteSource != null) {
            isFavorite.removeSource(isFavoriteSource);
        }

        // Add new sources
        currentWeatherSource = repository.getCurrentWeather(city);
        currentWeatherState.addSource(currentWeatherSource, currentWeatherState::setValue);

        forecastSource = repository.getForecast(city);
        forecastState.addSource(forecastSource, forecastState::setValue);

        // Update favorite status
        isFavoriteSource = repository.isFavorite(city);
        isFavorite.addSource(isFavoriteSource, isFavorite::setValue);
    }

    /**
     * Load only forecast for a city (used by search screen).
     */
    public void loadForecast(String city) {
        if (city == null || city.trim().isEmpty()) {
            forecastState.setValue(UiState.error("Please enter a city name"));
            return;
        }

        currentCity.setValue(city);

        if (forecastSource != null) {
            forecastState.removeSource(forecastSource);
        }
        if (isFavoriteSource != null) {
            isFavorite.removeSource(isFavoriteSource);
        }

        forecastSource = repository.getForecast(city);
        forecastState.addSource(forecastSource, forecastState::setValue);

        isFavoriteSource = repository.isFavorite(city);
        isFavorite.addSource(isFavoriteSource, isFavorite::setValue);
    }

    /**
     * Refresh current data.
     */
    public void refresh() {
        String city = currentCity.getValue();
        if (city != null && !city.isEmpty()) {
            loadWeatherForCity(city);
        }
    }

    // ==================== Favorites ====================

    /**
     * Get all favorite cities as LiveData.
     */
    public LiveData<List<FavoriteCity>> getFavorites() {
        return favorites;
    }

    /**
     * Check if current city is a favorite.
     */
    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    /**
     * Add the current city to favorites.
     */
    public void addCurrentCityToFavorites() {
        String city = currentCity.getValue();
        if (city != null && !city.isEmpty()) {
            repository.addFavorite(city);
        }
    }

    /**
     * Add a specific city to favorites.
     */
    public void addFavorite(String city) {
        if (city != null && !city.isEmpty()) {
            repository.addFavorite(city);
        }
    }

    /**
     * Remove a city from favorites.
     */
    public void removeFavorite(String cityName) {
        repository.removeFavorite(cityName);
    }

    /**
     * Remove a city from favorites.
     */
    public void removeFavorite(FavoriteCity city) {
        if (city != null) {
            repository.removeFavorite(city);
        }
    }

    /**
     * Toggle favorite status for current city.
     */
    public void toggleFavorite() {
        String city = currentCity.getValue();
        Boolean favorite = isFavorite.getValue();

        if (city != null && !city.isEmpty()) {
            if (favorite != null && favorite) {
                repository.removeFavorite(city);
            } else {
                repository.addFavorite(city);
            }
        }
    }
}
