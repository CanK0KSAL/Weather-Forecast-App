package com.example.weatherforecastapp;

import android.app.Application;
import android.util.Log;

import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;

import com.example.weatherforecastapp.work.WorkManagerScheduler;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Application class annotated with @HiltAndroidApp.
 * This is required for Hilt dependency injection to work.
 * Also configures WorkManager with HiltWorkerFactory.
 */
@HiltAndroidApp
public class WeatherApp extends Application implements Configuration.Provider {

    private static final String TAG = "WeatherApp";

    @Inject
    HiltWorkerFactory workerFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "WeatherApp initialized");

        // Schedule periodic weather sync
        scheduleBackgroundSync();
    }

    private void scheduleBackgroundSync() {
        try {
            WorkManagerScheduler.scheduleWeatherSync(this);
            Log.d(TAG, "Background sync scheduled");
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule background sync", e);
        }
    }

    /**
     * Provide WorkManager configuration with Hilt worker factory.
     * This allows WorkManager to use Hilt for dependency injection in workers.
     */
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(Log.INFO)
                .build();
    }
}
