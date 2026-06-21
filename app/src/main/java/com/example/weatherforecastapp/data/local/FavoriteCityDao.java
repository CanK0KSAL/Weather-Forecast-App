package com.example.weatherforecastapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object for FavoriteCityEntity.
 * Provides all database operations for favorite cities.
 */
@Dao
public interface FavoriteCityDao {

    /**
     * Get all favorite cities as LiveData, ordered by creation date (newest first)
     */
    @Query("SELECT * FROM favorite_cities ORDER BY created_at DESC")
    LiveData<List<FavoriteCityEntity>> observeAllFavorites();

    /**
     * Get all favorite cities synchronously (for WorkManager)
     */
    @Query("SELECT * FROM favorite_cities ORDER BY created_at DESC")
    List<FavoriteCityEntity> getAllFavoritesSync();

    /**
     * Check if a city is in favorites
     * Returns 1 if exists, 0 otherwise
     */
    @Query("SELECT COUNT(*) FROM favorite_cities WHERE LOWER(city_name) = LOWER(:cityName)")
    LiveData<Integer> isFavorite(String cityName);

    /**
     * Check if a city is in favorites synchronously
     */
    @Query("SELECT COUNT(*) > 0 FROM favorite_cities WHERE LOWER(city_name) = LOWER(:cityName)")
    boolean isFavoriteSync(String cityName);

    /**
     * Insert a favorite city, replace if exists
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FavoriteCityEntity city);

    /**
     * Delete a specific favorite city
     */
    @Delete
    void delete(FavoriteCityEntity city);

    /**
     * Delete a favorite city by name
     */
    @Query("DELETE FROM favorite_cities WHERE LOWER(city_name) = LOWER(:cityName)")
    void deleteByCityName(String cityName);

    /**
     * Get count of favorite cities
     */
    @Query("SELECT COUNT(*) FROM favorite_cities")
    LiveData<Integer> getFavoritesCount();

    /**
     * Get the most recently added favorite city (for WorkManager sync)
     */
    @Query("SELECT * FROM favorite_cities ORDER BY created_at DESC LIMIT 1")
    FavoriteCityEntity getLatestFavorite();
}
