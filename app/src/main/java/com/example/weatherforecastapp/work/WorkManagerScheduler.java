package com.example.weatherforecastapp.work;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Helper class for scheduling WorkManager periodic tasks.
 * Schedules weather sync to run every 6 hours when network is available.
 */
public class WorkManagerScheduler {

    private static final String TAG = "WorkManagerScheduler";
    private static final String WEATHER_SYNC_WORK_NAME = "weather_sync_periodic";

    // Minimum periodic interval is 15 minutes, we use 6 hours
    private static final long SYNC_INTERVAL_HOURS = 6;

    /**
     * Schedule periodic weather sync.
     * Uses KEEP policy to avoid rescheduling if already scheduled.
     */
    public static void scheduleWeatherSync(Context context) {
        Log.d(TAG, "Scheduling weather sync...");

        // Constraints: requires network connection
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Create periodic work request
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                WeatherSyncWorker.class,
                SYNC_INTERVAL_HOURS,
                TimeUnit.HOURS
        )
        .setConstraints(constraints)
        .addTag("weather_sync")
        .build();

        // Enqueue with KEEP policy - won't replace existing work
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        WEATHER_SYNC_WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        syncRequest
                );

        Log.d(TAG, "Weather sync scheduled to run every " + SYNC_INTERVAL_HOURS + " hours");
    }

    /**
     * Cancel scheduled weather sync.
     */
    public static void cancelWeatherSync(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WEATHER_SYNC_WORK_NAME);
        Log.d(TAG, "Weather sync cancelled");
    }

    /**
     * Trigger immediate sync (in addition to periodic).
     */
    public static void triggerImmediateSync(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        androidx.work.OneTimeWorkRequest syncRequest =
                new androidx.work.OneTimeWorkRequest.Builder(WeatherSyncWorker.class)
                        .setConstraints(constraints)
                        .addTag("weather_sync_immediate")
                        .build();

        WorkManager.getInstance(context).enqueue(syncRequest);
        Log.d(TAG, "Immediate weather sync triggered");
    }
}
