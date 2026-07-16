package com.example.weatherforecastapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherforecastapp.data.repository.WeatherRepository;
import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.FavoriteCity;
import com.example.weatherforecastapp.domain.model.ForecastDay;
import com.example.weatherforecastapp.presentation.state.UiState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory WeatherRepository for ViewModel unit tests.
 * Does not perform network or database I/O.
 */
final class FakeWeatherRepository implements WeatherRepository {

    final MutableLiveData<UiState<CurrentWeather>> currentWeather = new MutableLiveData<>();
    final MutableLiveData<UiState<List<ForecastDay>>> forecast = new MutableLiveData<>();
    final MutableLiveData<List<FavoriteCity>> favorites =
            new MutableLiveData<>(Collections.emptyList());
    final MutableLiveData<Boolean> favoriteStatus = new MutableLiveData<>(false);

    String lastCurrentWeatherCity;
    String lastForecastCity;
    final List<String> addedFavorites = new ArrayList<>();
    final List<String> removedFavorites = new ArrayList<>();

    @Override
    public LiveData<UiState<CurrentWeather>> getCurrentWeather(String city) {
        lastCurrentWeatherCity = city;
        return currentWeather;
    }

    @Override
    public LiveData<UiState<List<ForecastDay>>> getForecast(String city) {
        lastForecastCity = city;
        return forecast;
    }

    @Override
    public void refreshWeather(String city) {
        lastCurrentWeatherCity = city;
    }

    @Override
    public void refreshForecast(String city) {
        lastForecastCity = city;
    }

    @Override
    public LiveData<List<FavoriteCity>> observeFavorites() {
        return favorites;
    }

    @Override
    public LiveData<Boolean> isFavorite(String cityName) {
        return favoriteStatus;
    }

    @Override
    public void addFavorite(String cityName) {
        addedFavorites.add(cityName);
    }

    @Override
    public void removeFavorite(String cityName) {
        removedFavorites.add(cityName);
    }

    @Override
    public void removeFavorite(FavoriteCity city) {
        if (city != null) {
            removedFavorites.add(city.getCityName());
        }
    }

    @Override
    public List<FavoriteCity> getFavoritesSync() {
        List<FavoriteCity> value = favorites.getValue();
        return value != null ? value : Collections.emptyList();
    }

    @Override
    public CurrentWeather fetchCurrentWeatherSync(String city) {
        return null;
    }
}
