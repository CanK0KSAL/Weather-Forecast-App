package com.example.weatherforecastapp.domain.mapper;

import com.example.weatherforecastapp.data.local.FavoriteCityEntity;
import com.example.weatherforecastapp.data.remote.dto.CurrentWeatherResponse;
import com.example.weatherforecastapp.data.remote.model.ForecastResponse;
import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.FavoriteCity;
import com.example.weatherforecastapp.domain.model.ForecastDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Mapper class for converting DTOs and Entities to Domain models.
 * Keeps data layer separate from domain/presentation layers.
 */
@Singleton
public class WeatherMapper {

    @Inject
    public WeatherMapper() {
    }

    // ==================== Current Weather Mapping ====================

    /**
     * Maps CurrentWeatherResponse DTO to CurrentWeather domain model
     */
    public CurrentWeather mapToCurrentWeather(CurrentWeatherResponse response) {
        if (response == null) {
            return null;
        }

        String description = "";
        String iconCode = "";
        if (response.weather != null && !response.weather.isEmpty()) {
            description = response.weather.get(0).description;
            iconCode = response.weather.get(0).icon;
        }

        String country = "";
        if (response.sys != null) {
            country = response.sys.country != null ? response.sys.country : "";
        }

        double temp = 0;
        double feelsLike = 0;
        int humidity = 0;
        if (response.main != null) {
            temp = response.main.temp;
            feelsLike = response.main.feelsLike;
            humidity = response.main.humidity;
        }

        double windSpeed = 0;
        if (response.wind != null) {
            windSpeed = response.wind.speed;
        }

        return new CurrentWeather(
                response.cityName != null ? response.cityName : "",
                country,
                temp,
                feelsLike,
                description,
                humidity,
                windSpeed,
                iconCode,
                response.timestamp * 1000 // Convert to milliseconds
        );
    }

    // ==================== Forecast Mapping ====================

    /**
     * Maps ForecastResponse DTO to list of ForecastDay domain models.
     * Aggregates 3-hour forecasts into daily forecasts.
     */
    public List<ForecastDay> mapToForecastDays(ForecastResponse response) {
        if (response == null || response.list == null) {
            return new ArrayList<>();
        }

        // Aggregate by date
        Map<String, DailyAggregator> dailyData = new HashMap<>();

        for (ForecastResponse.ListItem item : response.list) {
            if (item == null || item.dtTxt == null || item.main == null) {
                continue;
            }

            String[] parts = item.dtTxt.split(" ");
            String date = parts.length > 0 ? parts[0] : item.dtTxt;

            DailyAggregator aggregator = dailyData.get(date);
            if (aggregator == null) {
                aggregator = new DailyAggregator(date);
                dailyData.put(date, aggregator);
            }

            double temp = item.main.temp;
            aggregator.addTemperature(temp);

            if (item.weather != null && !item.weather.isEmpty()) {
                aggregator.setDescription(item.weather.get(0).description);
                if (item.weather.get(0) instanceof ForecastResponse.Weather) {
                    // Check if icon exists (might need to add to DTO)
                }
            }
        }

        // Convert to domain models
        List<ForecastDay> result = new ArrayList<>();
        for (DailyAggregator aggregator : dailyData.values()) {
            result.add(aggregator.toForecastDay());
        }

        return result;
    }

    // ==================== Favorite City Mapping ====================

    /**
     * Maps FavoriteCityEntity to FavoriteCity domain model
     */
    public FavoriteCity mapToFavoriteCity(FavoriteCityEntity entity) {
        if (entity == null) {
            return null;
        }
        return new FavoriteCity(entity.id, entity.cityName, entity.createdAt);
    }

    /**
     * Maps list of FavoriteCityEntity to list of FavoriteCity domain models
     */
    public List<FavoriteCity> mapToFavoriteCities(List<FavoriteCityEntity> entities) {
        List<FavoriteCity> result = new ArrayList<>();
        if (entities != null) {
            for (FavoriteCityEntity entity : entities) {
                FavoriteCity city = mapToFavoriteCity(entity);
                if (city != null) {
                    result.add(city);
                }
            }
        }
        return result;
    }

    /**
     * Maps FavoriteCity domain model to FavoriteCityEntity
     */
    public FavoriteCityEntity mapToFavoriteCityEntity(FavoriteCity city) {
        if (city == null) {
            return null;
        }
        return new FavoriteCityEntity(city.getCityName());
    }

    /**
     * Maps city name string to FavoriteCityEntity
     */
    public FavoriteCityEntity mapToFavoriteCityEntity(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return null;
        }
        return new FavoriteCityEntity(cityName.trim());
    }

    // ==================== Helper Class ====================

    /**
     * Helper class to aggregate hourly forecasts into daily summary
     */
    private static class DailyAggregator {
        private final String date;
        private final List<Double> temperatures = new ArrayList<>();
        private String description = "";
        private String iconCode = "";

        DailyAggregator(String date) {
            this.date = date;
        }

        void addTemperature(double temp) {
            temperatures.add(temp);
        }

        void setDescription(String description) {
            if (description != null && !description.isEmpty()) {
                this.description = description;
            }
        }

        void setIconCode(String iconCode) {
            if (iconCode != null && !iconCode.isEmpty()) {
                this.iconCode = iconCode;
            }
        }

        ForecastDay toForecastDay() {
            if (temperatures.isEmpty()) {
                return new ForecastDay(date, 0, description);
            }

            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            for (double temp : temperatures) {
                sum += temp;
                if (temp < min) min = temp;
                if (temp > max) max = temp;
            }

            double avg = sum / temperatures.size();
            return new ForecastDay(date, avg, description, min, max, 0, iconCode);
        }
    }
}
