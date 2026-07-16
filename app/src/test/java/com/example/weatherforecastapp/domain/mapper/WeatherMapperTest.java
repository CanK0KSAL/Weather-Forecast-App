package com.example.weatherforecastapp.domain.mapper;

import com.example.weatherforecastapp.data.local.FavoriteCityEntity;
import com.example.weatherforecastapp.data.remote.dto.CurrentWeatherResponse;
import com.example.weatherforecastapp.data.remote.model.ForecastResponse;
import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.FavoriteCity;
import com.example.weatherforecastapp.domain.model.ForecastDay;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies WeatherMapper conversions from API DTOs and Room entities to domain models,
 * including null-safety and daily forecast aggregation from 3-hour slots.
 */
public class WeatherMapperTest {

    private WeatherMapper mapper;

    @Before
    public void setUp() {
        mapper = new WeatherMapper();
    }

    @Test
    public void mapToCurrentWeather_nullResponse_returnsNull() {
        assertNull(mapper.mapToCurrentWeather(null));
    }

    @Test
    public void mapToCurrentWeather_mapsFieldsAndConvertsTimestampToMillis() {
        CurrentWeatherResponse response = new CurrentWeatherResponse();
        response.cityName = "Paris";
        response.timestamp = 1_700_000_000L;

        CurrentWeatherResponse.Sys sys = new CurrentWeatherResponse.Sys();
        sys.country = "FR";
        response.sys = sys;

        CurrentWeatherResponse.Main main = new CurrentWeatherResponse.Main();
        main.temp = 18.5;
        main.feelsLike = 17.0;
        main.humidity = 55;
        response.main = main;

        CurrentWeatherResponse.Weather weather = new CurrentWeatherResponse.Weather();
        weather.description = "clear sky";
        weather.icon = "01d";
        response.weather = Collections.singletonList(weather);

        CurrentWeatherResponse.Wind wind = new CurrentWeatherResponse.Wind();
        wind.speed = 3.2;
        response.wind = wind;

        CurrentWeather result = mapper.mapToCurrentWeather(response);

        assertEquals("Paris", result.getCityName());
        assertEquals("FR", result.getCountry());
        assertEquals(18.5, result.getTemperature(), 0.001);
        assertEquals(17.0, result.getFeelsLike(), 0.001);
        assertEquals("clear sky", result.getDescription());
        assertEquals(55, result.getHumidity());
        assertEquals(3.2, result.getWindSpeed(), 0.001);
        assertEquals("01d", result.getIconCode());
        assertEquals(1_700_000_000_000L, result.getTimestamp());
    }

    @Test
    public void mapToForecastDays_nullOrEmpty_returnsEmptyList() {
        assertTrue(mapper.mapToForecastDays(null).isEmpty());

        ForecastResponse empty = new ForecastResponse();
        empty.list = null;
        assertTrue(mapper.mapToForecastDays(empty).isEmpty());
    }

    @Test
    public void mapToForecastDays_aggregatesSameDayTemperatures() {
        ForecastResponse response = new ForecastResponse();
        response.list = Arrays.asList(
                forecastItem("2026-03-01 09:00:00", 10.0, "clouds"),
                forecastItem("2026-03-01 12:00:00", 14.0, "clear sky"),
                forecastItem("2026-03-02 09:00:00", 8.0, "rain")
        );

        List<ForecastDay> days = mapper.mapToForecastDays(response);

        assertEquals(2, days.size());

        ForecastDay day1 = findByDate(days, "2026-03-01");
        assertEquals(12.0, day1.getTemperature(), 0.001);
        assertEquals(10.0, day1.getMinTemp(), 0.001);
        assertEquals(14.0, day1.getMaxTemp(), 0.001);
        assertEquals("clear sky", day1.getDescription());

        ForecastDay day2 = findByDate(days, "2026-03-02");
        assertEquals(8.0, day2.getTemperature(), 0.001);
        assertEquals("rain", day2.getDescription());
    }

    @Test
    public void mapToFavoriteCity_andEntityRoundTrip() {
        assertNull(mapper.mapToFavoriteCity(null));
        assertNull(mapper.mapToFavoriteCityEntity((String) null));
        assertNull(mapper.mapToFavoriteCityEntity("   "));

        FavoriteCityEntity entity = mapper.mapToFavoriteCityEntity("  Berlin  ");
        assertEquals("Berlin", entity.cityName);

        entity.id = 7L;
        FavoriteCity city = mapper.mapToFavoriteCity(entity);
        assertEquals(7L, city.getId());
        assertEquals("Berlin", city.getCityName());
        assertEquals(entity.createdAt, city.getCreatedAt());

        List<FavoriteCity> cities = mapper.mapToFavoriteCities(Collections.singletonList(entity));
        assertEquals(1, cities.size());
        assertEquals("Berlin", cities.get(0).getCityName());
    }

    private static ForecastResponse.ListItem forecastItem(String dtTxt, double temp, String description) {
        ForecastResponse.ListItem item = new ForecastResponse.ListItem();
        item.dtTxt = dtTxt;
        ForecastResponse.Main main = new ForecastResponse.Main();
        main.temp = temp;
        item.main = main;
        ForecastResponse.Weather weather = new ForecastResponse.Weather();
        weather.description = description;
        item.weather = Collections.singletonList(weather);
        return item;
    }

    private static ForecastDay findByDate(List<ForecastDay> days, String date) {
        for (ForecastDay day : days) {
            if (date.equals(day.getDate())) {
                return day;
            }
        }
        throw new AssertionError("No ForecastDay for date: " + date);
    }
}
