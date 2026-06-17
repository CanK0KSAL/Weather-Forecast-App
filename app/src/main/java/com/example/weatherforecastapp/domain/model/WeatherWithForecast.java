package com.example.weatherforecastapp.domain.model;

import java.util.List;

/**
 * Combined domain model containing both current weather and forecast.
 * Useful for displaying complete weather information on one screen.
 */
public class WeatherWithForecast {
    private final CurrentWeather currentWeather;
    private final List<ForecastDay> forecast;
    private final String cityName;

    public WeatherWithForecast(CurrentWeather currentWeather, List<ForecastDay> forecast, String cityName) {
        this.currentWeather = currentWeather;
        this.forecast = forecast;
        this.cityName = cityName;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public List<ForecastDay> getForecast() {
        return forecast;
    }

    public String getCityName() {
        return cityName;
    }

    public boolean hasCurrentWeather() {
        return currentWeather != null;
    }

    public boolean hasForecast() {
        return forecast != null && !forecast.isEmpty();
    }
}
