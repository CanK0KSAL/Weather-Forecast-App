package com.example.weatherforecastapp.di;

import com.example.weatherforecastapp.data.local.FavoriteCityDao;
import com.example.weatherforecastapp.data.remote.WeatherApiService;
import com.example.weatherforecastapp.data.repository.WeatherRepository;
import com.example.weatherforecastapp.data.repository.WeatherRepositoryImpl;
import com.example.weatherforecastapp.domain.mapper.WeatherMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module providing repository dependencies.
 * Binds repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    @Provides
    @Singleton
    public WeatherMapper provideWeatherMapper() {
        return new WeatherMapper();
    }

    @Provides
    @Singleton
    public WeatherRepository provideWeatherRepository(
            WeatherApiService apiService,
            FavoriteCityDao favoriteCityDao,
            WeatherMapper mapper
    ) {
        return new WeatherRepositoryImpl(apiService, favoriteCityDao, mapper);
    }
}
