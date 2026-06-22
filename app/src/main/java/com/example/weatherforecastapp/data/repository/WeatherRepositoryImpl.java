package com.example.weatherforecastapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.weatherforecastapp.BuildConfig;
import com.example.weatherforecastapp.data.local.FavoriteCityDao;
import com.example.weatherforecastapp.data.local.FavoriteCityEntity;
import com.example.weatherforecastapp.data.remote.WeatherApiService;
import com.example.weatherforecastapp.data.remote.dto.CurrentWeatherResponse;
import com.example.weatherforecastapp.data.remote.model.ForecastResponse;
import com.example.weatherforecastapp.domain.mapper.WeatherMapper;
import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.FavoriteCity;
import com.example.weatherforecastapp.domain.model.ForecastDay;
import com.example.weatherforecastapp.presentation.state.UiState;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of WeatherRepository.
 * Coordinates data fetching from network and local database.
 */
@Singleton
public class WeatherRepositoryImpl implements WeatherRepository {

    private static final String TAG = "WeatherRepository";
    private static final String UNITS = "metric";

    private final WeatherApiService apiService;
    private final FavoriteCityDao favoriteCityDao;
    private final WeatherMapper mapper;
    private final ExecutorService ioExecutor;

    // LiveData for current weather state
    private final MutableLiveData<UiState<CurrentWeather>> currentWeatherState = new MutableLiveData<>();
    
    // LiveData for forecast state
    private final MutableLiveData<UiState<List<ForecastDay>>> forecastState = new MutableLiveData<>();

    // Cached favorites LiveData with transformation
    private LiveData<List<FavoriteCity>> favoritesLiveData;

    @Inject
    public WeatherRepositoryImpl(
            WeatherApiService apiService,
            FavoriteCityDao favoriteCityDao,
            WeatherMapper mapper
    ) {
        this.apiService = apiService;
        this.favoriteCityDao = favoriteCityDao;
        this.mapper = mapper;
        this.ioExecutor = Executors.newFixedThreadPool(2);
    }

    // ==================== Weather Data ====================

    @Override
    public LiveData<UiState<CurrentWeather>> getCurrentWeather(String city) {
        refreshWeather(city);
        return currentWeatherState;
    }

    @Override
    public LiveData<UiState<List<ForecastDay>>> getForecast(String city) {
        refreshForecast(city);
        return forecastState;
    }

    private String getApiKey() {
        return BuildConfig.OPENWEATHER_API_KEY;
    }

    private boolean isApiKeyConfigured() {
        String apiKey = getApiKey();
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    @Override
    public void refreshWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            currentWeatherState.postValue(UiState.error("City name cannot be empty"));
            return;
        }

        if (!isApiKeyConfigured()) {
            currentWeatherState.postValue(UiState.error(
                    "API key not configured. Add OPENWEATHER_API_KEY to local.properties."));
            return;
        }

        currentWeatherState.postValue(UiState.loading());

        apiService.getCurrentWeather(city, UNITS, getApiKey()).enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CurrentWeather weather = mapper.mapToCurrentWeather(response.body());
                    currentWeatherState.postValue(UiState.success(weather));
                } else {
                    String errorMsg = "City not found";
                    if (response.code() == 404) {
                        errorMsg = "City not found. Please check the spelling.";
                    } else if (response.code() == 401) {
                        errorMsg = "API key error. Please try again later.";
                    }
                    currentWeatherState.postValue(UiState.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch current weather", t);
                String errorMsg = "Network error. Please check your connection.";
                if (t instanceof IOException) {
                    errorMsg = "No internet connection";
                }
                currentWeatherState.postValue(UiState.error(errorMsg, t));
            }
        });
    }

    @Override
    public void refreshForecast(String city) {
        if (city == null || city.trim().isEmpty()) {
            forecastState.postValue(UiState.error("City name cannot be empty"));
            return;
        }

        if (!isApiKeyConfigured()) {
            forecastState.postValue(UiState.error(
                    "API key not configured. Add OPENWEATHER_API_KEY to local.properties."));
            return;
        }

        forecastState.postValue(UiState.loading());

        apiService.getFiveDayForecast(city, UNITS, getApiKey()).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastDay> forecast = mapper.mapToForecastDays(response.body());
                    forecastState.postValue(UiState.success(forecast));
                } else {
                    String errorMsg = "Failed to load forecast";
                    if (response.code() == 404) {
                        errorMsg = "City not found. Please check the spelling.";
                    }
                    forecastState.postValue(UiState.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch forecast", t);
                String errorMsg = "Network error. Please check your connection.";
                if (t instanceof IOException) {
                    errorMsg = "No internet connection";
                }
                forecastState.postValue(UiState.error(errorMsg, t));
            }
        });
    }

    // ==================== Favorite Cities ====================

    @Override
    public LiveData<List<FavoriteCity>> observeFavorites() {
        if (favoritesLiveData == null) {
            favoritesLiveData = Transformations.map(
                    favoriteCityDao.observeAllFavorites(),
                    entities -> mapper.mapToFavoriteCities(entities)
            );
        }
        return favoritesLiveData;
    }

    @Override
    public LiveData<Boolean> isFavorite(String cityName) {
        return Transformations.map(
                favoriteCityDao.isFavorite(cityName),
                count -> count != null && count > 0
        );
    }

    @Override
    public void addFavorite(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return;
        }
        ioExecutor.execute(() -> {
            FavoriteCityEntity entity = mapper.mapToFavoriteCityEntity(cityName);
            if (entity != null) {
                favoriteCityDao.insert(entity);
                Log.d(TAG, "Added favorite: " + cityName);
            }
        });
    }

    @Override
    public void removeFavorite(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return;
        }
        ioExecutor.execute(() -> {
            favoriteCityDao.deleteByCityName(cityName);
            Log.d(TAG, "Removed favorite: " + cityName);
        });
    }

    @Override
    public void removeFavorite(FavoriteCity city) {
        if (city == null) {
            return;
        }
        removeFavorite(city.getCityName());
    }

    // ==================== Sync Operations (for WorkManager) ====================

    @Override
    public List<FavoriteCity> getFavoritesSync() {
        List<FavoriteCityEntity> entities = favoriteCityDao.getAllFavoritesSync();
        return mapper.mapToFavoriteCities(entities);
    }

    @Override
    public CurrentWeather fetchCurrentWeatherSync(String city) {
        if (city == null || city.trim().isEmpty() || !isApiKeyConfigured()) {
            return null;
        }
        try {
            Response<CurrentWeatherResponse> response =
                    apiService.getCurrentWeather(city, UNITS, getApiKey()).execute();
            if (response.isSuccessful() && response.body() != null) {
                return mapper.mapToCurrentWeather(response.body());
            }
        } catch (IOException e) {
            Log.e(TAG, "Sync fetch failed for " + city, e);
        }
        return null;
    }
}
