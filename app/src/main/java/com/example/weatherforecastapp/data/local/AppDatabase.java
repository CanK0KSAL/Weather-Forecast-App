package com.example.weatherforecastapp.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Room Database for the Weather App.
 * Version 2: Added createdAt timestamp to FavoriteCityEntity
 */
@Database(
    entities = {FavoriteCityEntity.class},
    version = 2,
    exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteCityDao favoriteCityDao();
}
