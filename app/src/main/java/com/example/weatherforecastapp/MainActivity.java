package com.example.weatherforecastapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Main Activity hosting the navigation graph.
 * Uses BottomNavigationView for switching between Search and Favorites.
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigation();
    }

    private void setupNavigation() {
        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Setup bottom navigation
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                NavigationUI.setupWithNavController(bottomNav, navController);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
