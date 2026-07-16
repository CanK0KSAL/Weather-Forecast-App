package com.example.weatherforecastapp.domain.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies domain model helpers: location formatting, description capitalization,
 * and FavoriteCity case-insensitive equality.
 */
public class DomainModelTest {

    @Test
    public void currentWeather_fullLocationAndCapitalizedDescription() {
        CurrentWeather withCountry = new CurrentWeather(
                "London", "GB", 12.0, 11.0, "light rain", 70, 2.5, "10d", 0L);
        assertEquals("London, GB", withCountry.getFullLocation());
        assertEquals("Light rain", withCountry.getCapitalizedDescription());

        CurrentWeather withoutCountry = new CurrentWeather(
                "London", "", 12.0, 11.0, "", 70, 2.5, "10d", 0L);
        assertEquals("London", withoutCountry.getFullLocation());
        assertEquals("", withoutCountry.getCapitalizedDescription());
    }

    @Test
    public void forecastDay_simplifiedConstructorUsesTempForMinMax() {
        ForecastDay day = new ForecastDay("2026-03-01", 15.5, "sunny");
        assertEquals(15.5, day.getMinTemp(), 0.001);
        assertEquals(15.5, day.getMaxTemp(), 0.001);
        assertEquals(0, day.getHumidity());
        assertEquals("", day.getIconCode());
    }

    @Test
    public void favoriteCity_equalsIgnoresCase() {
        FavoriteCity a = new FavoriteCity(1, "Oslo", 100L);
        FavoriteCity b = new FavoriteCity(2, "oslo", 200L);
        FavoriteCity c = new FavoriteCity(3, "Bergen", 300L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertFalse(a.equals(null));
        assertTrue(a.equals(a));
    }
}
