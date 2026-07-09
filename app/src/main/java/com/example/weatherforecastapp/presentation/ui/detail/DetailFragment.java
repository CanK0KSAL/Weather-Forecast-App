package com.example.weatherforecastapp.presentation.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherforecastapp.R;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for displaying detailed weather information for a specific day.
 * Receives data via SafeArgs navigation arguments.
 */
@AndroidEntryPoint
public class DetailFragment extends Fragment {

    private TextView txtDate;
    private TextView txtTemp;
    private TextView txtDesc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadArguments();
    }

    private void initViews(View view) {
        txtDate = view.findViewById(R.id.txtDate);
        txtTemp = view.findViewById(R.id.txtTemp);
        txtDesc = view.findViewById(R.id.txtDesc);
    }

    private void loadArguments() {
        // Get arguments using SafeArgs
        DetailFragmentArgs args = DetailFragmentArgs.fromBundle(requireArguments());

        String date = args.getDate();
        String temp = args.getTemp();
        String description = args.getDescription();

        // Display data
        txtDate.setText(formatDate(date));
        txtTemp.setText(temp);

        // Capitalize description
        if (description != null && !description.isEmpty()) {
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
        }
        txtDesc.setText(description);
    }

    /**
     * Format date for display (e.g., "2024-01-15" -> "January 15, 2024")
     */
    private String formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return "";
        }

        // Simple formatting - could be enhanced with DateTimeFormatter
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);

                String[] months = {"", "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"};

                if (month >= 1 && month <= 12) {
                    return months[month] + " " + day + ", " + year;
                }
            }
        } catch (Exception e) {
            // Fall back to original format
        }

        return date;
    }
}
