package com.example.weatherforecastapp.presentation.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.weatherforecastapp.R;
import com.example.weatherforecastapp.domain.model.FavoriteCity;
import com.example.weatherforecastapp.presentation.viewmodel.WeatherViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for displaying and managing favorite cities.
 * Data comes from Room database via Repository and ViewModel.
 */
@AndroidEntryPoint
public class FavoritesFragment extends Fragment {

    private ListView listView;
    private TextView txtEmpty;
    private TextView txtSubtitle;
    private ProgressBar progressBar;
    private View layoutEmpty;

    private ArrayAdapter<String> adapter;
    private final ArrayList<String> cityNames = new ArrayList<>();
    private List<FavoriteCity> favoriteCities = new ArrayList<>();

    private WeatherViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupAdapter();
        setupViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        listView = view.findViewById(R.id.listFavorites);
        txtEmpty = view.findViewById(R.id.txtEmpty);
        txtSubtitle = view.findViewById(R.id.txtSubtitle);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void setupAdapter() {
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                cityNames
        );
        listView.setAdapter(adapter);
    }

    private void setupViewModel() {
        // Share ViewModel with other fragments for consistency
        viewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
    }

    private void setupClickListeners() {
        // Click to load weather for that city
        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position < favoriteCities.size()) {
                FavoriteCity city = favoriteCities.get(position);

                // Navigate to search with city name using SafeArgs
                FavoritesFragmentDirections.ActionFavoritesToSearch action =
                        FavoritesFragmentDirections.actionFavoritesToSearch();
                action.setCityName(city.getCityName());

                Navigation.findNavController(itemView).navigate(action);
            }
        });

        // Long click to delete
        listView.setOnItemLongClickListener((parent, itemView, position, id) -> {
            if (position < favoriteCities.size()) {
                FavoriteCity city = favoriteCities.get(position);
                showDeleteConfirmation(itemView, city);
                return true;
            }
            return false;
        });
    }

    private void showDeleteConfirmation(View view, FavoriteCity city) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove Favorite")
                .setMessage("Remove " + city.getCityName() + " from favorites?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Remove", (dialog, which) -> {
                    viewModel.removeFavorite(city);
                    Snackbar.make(view, city.getCityName() + " removed", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> viewModel.addFavorite(city.getCityName()))
                            .show();
                })
                .show();
    }

    private void observeViewModel() {
        viewModel.getFavorites().observe(getViewLifecycleOwner(), this::updateFavoritesList);
    }

    private void updateFavoritesList(List<FavoriteCity> favorites) {
        // Hide progress bar if showing
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        cityNames.clear();
        favoriteCities.clear();

        if (favorites != null && !favorites.isEmpty()) {
            favoriteCities.addAll(favorites);
            for (FavoriteCity city : favorites) {
                if (city != null && city.getCityName() != null) {
                    cityNames.add(city.getCityName());
                }
            }

            listView.setVisibility(View.VISIBLE);
            if (txtSubtitle != null) {
                txtSubtitle.setVisibility(View.VISIBLE);
            }
            if (layoutEmpty != null) {
                layoutEmpty.setVisibility(View.GONE);
            }
        } else {
            listView.setVisibility(View.GONE);
            if (txtSubtitle != null) {
                txtSubtitle.setVisibility(View.GONE);
            }
            if (layoutEmpty != null) {
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
