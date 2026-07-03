package com.example.weatherforecastapp.di;

import android.content.Context;

import androidx.room.Room;

import com.example.weatherforecastapp.data.local.AppDatabase;
import com.example.weatherforecastapp.data.local.FavoriteCityDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module providing database-related dependencies.
 * Provides Room database and DAO instances.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    private static final String DATABASE_NAME = "weather_app_db";

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build();
    }

    @Provides
    @Singleton
    public FavoriteCityDao provideFavoriteCityDao(AppDatabase database) {
        return database.favoriteCityDao();
    }
}
