package com.example.weatherforecastapp.work;

import com.example.weatherforecastapp.WeatherApp;

import org.junit.Test;

import androidx.work.Configuration;

import static org.junit.Assert.assertTrue;

/**
 * Static/compile-time oriented checks for WorkManager + Hilt wiring.
 * Does not claim that periodic scheduling or notifications were runtime-verified.
 */
public class WorkManagerConfigurationStaticTest {

    @Test
    public void weatherApp_implementsConfigurationProvider() {
        assertTrue(Configuration.Provider.class.isAssignableFrom(WeatherApp.class));
    }

    @Test
    public void weatherSyncWorker_isConcreteWorkerSubclass() {
        assertTrue(androidx.work.Worker.class.isAssignableFrom(WeatherSyncWorker.class));
    }
}
