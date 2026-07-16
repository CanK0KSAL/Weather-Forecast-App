package com.example.weatherforecastapp.presentation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.weatherforecastapp.domain.model.CurrentWeather;
import com.example.weatherforecastapp.domain.model.ForecastDay;
import com.example.weatherforecastapp.presentation.state.UiState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies ViewModel empty-input validation and LiveData mediation of repository
 * loading/success/error states using a fake repository (no network/DB).
 */
public class WeatherViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FakeWeatherRepository repository;
    private WeatherViewModel viewModel;

    @Before
    public void setUp() {
        repository = new FakeWeatherRepository();
        viewModel = new WeatherViewModel(repository);
    }

    @Test
    public void loadWeatherForCity_emptyCity_postsValidationErrorWithoutCallingRepository() {
        AtomicReference<UiState<CurrentWeather>> weatherState = new AtomicReference<>();
        Observer<UiState<CurrentWeather>> observer = weatherState::set;
        viewModel.getCurrentWeatherState().observeForever(observer);

        viewModel.loadWeatherForCity("  ");

        assertTrue(weatherState.get().isError());
        assertEquals("Please enter a city name", weatherState.get().getErrorMessageOrNull());
        assertNull(repository.lastCurrentWeatherCity);
        assertNull(repository.lastForecastCity);

        viewModel.getCurrentWeatherState().removeObserver(observer);
    }

    @Test
    public void loadForecast_emptyCity_postsValidationError() {
        AtomicReference<UiState<List<ForecastDay>>> forecastState = new AtomicReference<>();
        Observer<UiState<List<ForecastDay>>> observer = forecastState::set;
        viewModel.getForecastState().observeForever(observer);

        viewModel.loadForecast("");

        assertTrue(forecastState.get().isError());
        assertEquals("Please enter a city name", forecastState.get().getErrorMessageOrNull());
        assertNull(repository.lastForecastCity);

        viewModel.getForecastState().removeObserver(observer);
    }

    @Test
    public void loadWeatherForCity_mediatesLoadingSuccessAndErrorFromRepository() {
        AtomicReference<UiState<CurrentWeather>> weatherState = new AtomicReference<>();
        AtomicReference<UiState<List<ForecastDay>>> forecastState = new AtomicReference<>();
        Observer<UiState<CurrentWeather>> weatherObserver = weatherState::set;
        Observer<UiState<List<ForecastDay>>> forecastObserver = forecastState::set;
        viewModel.getCurrentWeatherState().observeForever(weatherObserver);
        viewModel.getForecastState().observeForever(forecastObserver);

        viewModel.loadWeatherForCity("Berlin");
        assertEquals("Berlin", repository.lastCurrentWeatherCity);
        assertEquals("Berlin", repository.lastForecastCity);
        assertEquals("Berlin", viewModel.getCurrentCity().getValue());

        repository.currentWeather.setValue(UiState.loading());
        assertTrue(weatherState.get().isLoading());

        CurrentWeather weather = new CurrentWeather(
                "Berlin", "DE", 5.0, 3.0, "clear sky", 40, 2.0, "01d", 0L);
        repository.currentWeather.setValue(UiState.success(weather));
        assertTrue(weatherState.get().isSuccess());
        assertEquals("Berlin", weatherState.get().getDataOrNull().getCityName());

        repository.forecast.setValue(UiState.error("City not found"));
        assertTrue(forecastState.get().isError());
        assertEquals("City not found", forecastState.get().getErrorMessageOrNull());

        repository.forecast.setValue(UiState.success(Collections.emptyList()));
        assertTrue(forecastState.get().isSuccess());

        viewModel.getCurrentWeatherState().removeObserver(weatherObserver);
        viewModel.getForecastState().removeObserver(forecastObserver);
    }

    @Test
    public void addFavorite_delegatesToRepository() {
        viewModel.loadForecast("Oslo");
        viewModel.addFavorite("Oslo");
        assertEquals(Collections.singletonList("Oslo"), repository.addedFavorites);
    }
}
