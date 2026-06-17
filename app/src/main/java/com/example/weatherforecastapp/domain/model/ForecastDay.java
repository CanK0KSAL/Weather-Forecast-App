package com.example.weatherforecastapp.domain.model;

/**
 * Domain model representing a single day's weather forecast.
 * This is a clean domain model used by the UI layer.
 */
public class ForecastDay {
    private final String date;
    private final double temperature;
    private final String description;
    private final double minTemp;
    private final double maxTemp;
    private final int humidity;
    private final String iconCode;

    public ForecastDay(String date, double temperature, String description,
                       double minTemp, double maxTemp, int humidity, String iconCode) {
        this.date = date;
        this.temperature = temperature;
        this.description = description;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.iconCode = iconCode;
    }

    // Simplified constructor for backward compatibility
    public ForecastDay(String date, double temperature, String description) {
        this(date, temperature, description, temperature, temperature, 0, "");
    }

    public String getDate() {
        return date;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getFormattedTemperature() {
        return String.format("%.1f°C", temperature);
    }

    public String getDescription() {
        return description;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getFormattedTempRange() {
        return String.format("%.0f° / %.0f°", minTemp, maxTemp);
    }

    public int getHumidity() {
        return humidity;
    }

    public String getIconCode() {
        return iconCode;
    }

    @Override
    public String toString() {
        return "ForecastDay{" +
                "date='" + date + '\'' +
                ", temperature=" + temperature +
                ", description='" + description + '\'' +
                '}';
    }
}
