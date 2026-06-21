package com.example.weatherforecastapp.data.remote;

import com.example.weatherforecastapp.data.remote.dto.CurrentWeatherResponse;
import com.example.weatherforecastapp.data.remote.model.ForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API interface for OpenWeatherMap API.
 * Base URL: https://api.openweathermap.org/data/2.5/
 */
public interface WeatherApiService {

    /**
     * Get current weather for a city
     * @param city City name (e.g., "London", "New York,US")
     * @param units Units of measurement: "metric" for Celsius
     * @param apiKey OpenWeatherMap API key
     */
    @GET("weather")
    Call<CurrentWeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    /**
     * Get 5-day/3-hour forecast for a city
     * @param city City name (e.g., "London", "New York,US")
     * @param units Units of measurement: "metric" for Celsius
     * @param apiKey OpenWeatherMap API key
     */
    @GET("forecast")
    Call<ForecastResponse> getFiveDayForecast(
            @Query("q") String city,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
}
