package com.example.weatherforecastapp.presentation.ui.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.weatherforecastapp.R;
import com.example.weatherforecastapp.domain.model.ForecastDay;
import com.example.weatherforecastapp.presentation.adapter.ForecastAdapter;
import com.example.weatherforecastapp.presentation.state.UiState;
import com.example.weatherforecastapp.presentation.viewmodel.WeatherViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for searching cities and displaying weather forecast.
 * Demonstrates MVVM pattern with UiState handling.
 */
@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private EditText editCity;
    private Button btnGetWeather;
    private Button btnSaveCity;
    private ListView listView;
    private TextView txtCityName;
    private ProgressBar progressBar;
    private TextView txtError;
    private View cardError;

    private ForecastAdapter adapter;
    private final ArrayList<ForecastDay> forecastList = new ArrayList<>();

    private WeatherViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupAdapter();
        setupViewModel();
        setupClickListeners();
        observeViewModel();

        // Check if city name was passed from Favorites
        checkForCityArgument();
    }

    /**
     * Check if a city name was passed via SafeArgs (from Favorites).
     * If so, auto-fill and load weather for that city.
     */
    private void checkForCityArgument() {
        if (getArguments() != null) {
            SearchFragmentArgs args = SearchFragmentArgs.fromBundle(getArguments());
            String cityName = args.getCityName();

            if (cityName != null && !cityName.isEmpty()) {
                // Auto-fill the city name
                editCity.setText(cityName);

                // Auto-load weather for this city
                viewModel.loadForecast(cityName);

                // Clear the argument to prevent reloading on configuration change
                getArguments().putString("cityName", "");
            }
        }
    }

    private void initViews(View view) {
        editCity = view.findViewById(R.id.editCity);
        btnGetWeather = view.findViewById(R.id.btnGetWeather);
        btnSaveCity = view.findViewById(R.id.btnSaveCity);
        listView = view.findViewById(R.id.listView);
        txtCityName = view.findViewById(R.id.txtCityName);
        progressBar = view.findViewById(R.id.progressBar);
        txtError = view.findViewById(R.id.txtError);
        cardError = view.findViewById(R.id.cardError);
    }

    private void setupAdapter() {
        adapter = new ForecastAdapter(requireContext(), forecastList);
        listView.setAdapter(adapter);
    }

    private void setupViewModel() {
        // Use activity-scoped ViewModel to share state with other fragments
        viewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
    }

    private void setupClickListeners() {
        btnGetWeather.setOnClickListener(v -> {
            String city = editCity.getText().toString().trim();
            if (TextUtils.isEmpty(city)) {
                Toast.makeText(requireContext(), "Please enter a city", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.loadForecast(city);
            }
        });

        btnSaveCity.setOnClickListener(v -> {
            String city = editCity.getText().toString().trim();
            if (TextUtils.isEmpty(city)) {
                Toast.makeText(requireContext(), "Enter a city to save", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.addFavorite(city);
                Snackbar.make(v, city + " added to favorites", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", view -> viewModel.removeFavorite(city))
                        .show();
            }
        });

        listView.setOnItemClickListener((parent, v, position, id) -> {
            if (position < forecastList.size()) {
                ForecastDay item = forecastList.get(position);
                navigateToDetail(v, item);
            }
        });
    }

    private void navigateToDetail(View view, ForecastDay forecast) {
        // Using SafeArgs for type-safe navigation
        SearchFragmentDirections.ActionSearchToDetail action =
                SearchFragmentDirections.actionSearchToDetail(
                        forecast.getDate(),
                        forecast.getFormattedTemperature(),
                        forecast.getDescription() != null ? forecast.getDescription() : ""
                );
        Navigation.findNavController(view).navigate(action);
    }

    private void observeViewModel() {
        // Observe forecast state with proper UiState handling
        viewModel.getForecastState().observe(getViewLifecycleOwner(), this::handleForecastState);

        // Observe current city to update UI
        viewModel.getCurrentCity().observe(getViewLifecycleOwner(), city -> {
            if (city != null && !city.isEmpty()) {
                txtCityName.setText("5-day forecast for " + city);
            }
        });

        // Observe favorite status to update save button
        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            if (isFavorite != null && isFavorite) {
                btnSaveCity.setText("★ Saved to Favorites");
                btnSaveCity.setEnabled(false);
            } else {
                btnSaveCity.setText("Save City to Favorites");
                btnSaveCity.setEnabled(true);
            }
        });
    }

    /**
     * Handle forecast UiState - demonstrates proper state handling pattern.
     */
    private void handleForecastState(UiState<List<ForecastDay>> state) {
        if (state == null) {
            // Initial state - hide everything
            showInitialState();
            return;
        }

        if (state.isLoading()) {
            showLoadingState();
        } else if (state.isSuccess()) {
            List<ForecastDay> data = state.getDataOrNull();
            showSuccessState(data);
        } else if (state.isError()) {
            String errorMessage = state.getErrorMessageOrNull();
            showErrorState(errorMessage);
        }
    }

    private void showInitialState() {
        progressBar.setVisibility(View.GONE);
        txtCityName.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        if (cardError != null) {
            cardError.setVisibility(View.GONE);
        }
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        if (cardError != null) {
            cardError.setVisibility(View.GONE);
        }
    }

    private void showSuccessState(List<ForecastDay> data) {
        progressBar.setVisibility(View.GONE);

        forecastList.clear();
        if (data != null && !data.isEmpty()) {
            forecastList.addAll(data);
            txtCityName.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            if (cardError != null) {
                cardError.setVisibility(View.GONE);
            }
        } else {
            txtCityName.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    private void showErrorState(String message) {
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        txtCityName.setVisibility(View.GONE);

        if (cardError != null && txtError != null) {
            cardError.setVisibility(View.VISIBLE);
            txtError.setText(message != null ? message : "An error occurred");
        } else {
            Toast.makeText(requireContext(),
                    message != null ? message : "An error occurred",
                    Toast.LENGTH_LONG).show();
        }
    }
}
