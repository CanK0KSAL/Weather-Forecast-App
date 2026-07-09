package com.example.weatherforecastapp.work;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.weatherforecastapp.data.repository.WeatherRepository;
import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.FavoriteCity;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class WeatherSyncWorker extends Worker {

    private static final String TAG = "WeatherSyncWorker";
    private static final String CHANNEL_ID = "weather_updates";
    private static final int NOTIFICATION_ID = 1001;

    private final WeatherRepository repository;

    @AssistedInject
    public WeatherSyncWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            WeatherRepository repository
    ) {
        super(context, workerParams);
        this.repository = repository;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting weather sync...");

        try {
            List<FavoriteCity> favorites = repository.getFavoritesSync();

            if (favorites == null || favorites.isEmpty()) {
                Log.d(TAG, "No favorite cities to sync");
                return Result.success();
            }

            Log.d(TAG, "Syncing weather for " + favorites.size() + " cities");

            CurrentWeather latestWeather = null;
            String latestCity = null;
            int successCount = 0;

            for (FavoriteCity city : favorites) {
                try {
                    CurrentWeather weather = repository.fetchCurrentWeatherSync(city.getCityName());
                    if (weather != null) {
                        successCount++;
                        latestWeather = weather;
                        latestCity = city.getCityName();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to sync " + city.getCityName(), e);
                }
            }

            if (latestWeather != null && successCount > 0) {
                showNotification(latestCity, latestWeather, successCount, favorites.size());
            }

            Log.d(TAG, "Sync completed: " + successCount + "/" + favorites.size());
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Weather sync failed", e);
            return Result.retry();
        }
    }

    private void showNotification(String cityName, CurrentWeather weather, int updated, int total) {
        Context context = getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Weather Updates",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background weather sync notifications");
            notificationManager.createNotificationChannel(channel);
        }

        String title = "Weather Updated";
        String content = cityName + ": " + weather.getFormattedTemperature() +
                ", " + weather.getCapitalizedDescription();

        if (total > 1) {
            content += " (+" + (total - 1) + " more)";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
