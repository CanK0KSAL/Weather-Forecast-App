package com.example.weatherforecastapp.domain.model;

/**
 * Domain model representing current weather conditions for a city.
 */
public class CurrentWeather {
    private final String cityName;
    private final String country;
    private final double temperature;
    private final double feelsLike;
    private final String description;
    private final int humidity;
    private final double windSpeed;
    private final String iconCode;
    private final long timestamp;

    public CurrentWeather(String cityName, String country, double temperature,
                          double feelsLike, String description, int humidity,
                          double windSpeed, String iconCode, long timestamp) {
        this.cityName = cityName;
        this.country = country;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.iconCode = iconCode;
        this.timestamp = timestamp;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public String getFullLocation() {
        if (country != null && !country.isEmpty()) {
            return cityName + ", " + country;
        }
        return cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getFormattedTemperature() {
        return String.format("%.1f°C", temperature);
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public String getFormattedFeelsLike() {
        return String.format("Feels like %.1f°C", feelsLike);
    }

    public String getDescription() {
        return description;
    }

    public String getCapitalizedDescription() {
        if (description == null || description.isEmpty()) {
            return "";
        }
        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    public int getHumidity() {
        return humidity;
    }

    public String getFormattedHumidity() {
        return humidity + "%";
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getFormattedWindSpeed() {
        return String.format("%.1f m/s", windSpeed);
    }

    public String getIconCode() {
        return iconCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "cityName='" + cityName + '\'' +
                ", temperature=" + temperature +
                ", description='" + description + '\'' +
                '}';
    }
}
