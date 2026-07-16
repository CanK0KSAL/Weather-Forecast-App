package com.example.weatherforecastapp.di;

import org.junit.Test;

import okhttp3.logging.HttpLoggingInterceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Verifies that OkHttp log redaction masks OpenWeatherMap appid query values
 * and that release builds select NONE logging while debug selects BASIC.
 */
public class NetworkModuleRedactionTest {

    @Test
    public void redactAppId_masksQueryValue() {
        String input = "--> GET https://api.openweathermap.org/data/2.5/weather?q=London&units=metric&appid=secretvalue http/1.1";
        String expected = "--> GET https://api.openweathermap.org/data/2.5/weather?q=London&units=metric&appid=*** http/1.1";
        assertEquals(expected, NetworkModule.redactAppId(input));
    }

    @Test
    public void redactAppId_handlesNullAndUnaffectedMessages() {
        assertNull(NetworkModule.redactAppId(null));
        assertEquals("", NetworkModule.redactAppId(""));
        assertEquals("connection established", NetworkModule.redactAppId("connection established"));
    }

    @Test
    public void loggingLevel_debugUsesBasic_releaseUsesNone() {
        assertEquals(HttpLoggingInterceptor.Level.BASIC, NetworkModule.loggingLevel(true));
        assertEquals(HttpLoggingInterceptor.Level.NONE, NetworkModule.loggingLevel(false));
    }
}
