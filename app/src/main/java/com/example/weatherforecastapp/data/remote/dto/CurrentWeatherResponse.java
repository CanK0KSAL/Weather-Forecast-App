package com.example.weatherforecastapp.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * DTO for current weather API response from OpenWeatherMap.
 * Endpoint: /weather?q={city}&units=metric&appid={key}
 */
public class CurrentWeatherResponse {

    @SerializedName("name")
    public String cityName;

    @SerializedName("sys")
    public Sys sys;

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public List<Weather> weather;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("dt")
    public long timestamp;

    @SerializedName("cod")
    public int cod;

    @SerializedName("message")
    public String message;

    public static class Sys {
        @SerializedName("country")
        public String country;

        @SerializedName("sunrise")
        public long sunrise;

        @SerializedName("sunset")
        public long sunset;
    }

    public static class Main {
        @SerializedName("temp")
        public double temp;

        @SerializedName("feels_like")
        public double feelsLike;

        @SerializedName("temp_min")
        public double tempMin;

        @SerializedName("temp_max")
        public double tempMax;

        @SerializedName("humidity")
        public int humidity;

        @SerializedName("pressure")
        public int pressure;
    }

    public static class Weather {
        @SerializedName("id")
        public int id;

        @SerializedName("main")
        public String main;

        @SerializedName("description")
        public String description;

        @SerializedName("icon")
        public String icon;
    }

    public static class Wind {
        @SerializedName("speed")
        public double speed;

        @SerializedName("deg")
        public int deg;
    }
}
