package com.example.weatherforecastapp.data.local;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room Entity for storing favorite cities.
 * Uses unique constraint on cityName to prevent duplicates.
 */
@Entity(
    tableName = "favorite_cities",
    indices = {@Index(value = "city_name", unique = true)}
)
public class FavoriteCityEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "city_name")
    public String cityName;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public FavoriteCityEntity(@NonNull String cityName) {
        this.cityName = cityName;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters for Room and other uses
    public long getId() {
        return id;
    }

    @NonNull
    public String getCityName() {
        return cityName;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
