package com.example.weatherforecastapp.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weatherforecastapp.domain.model.ForecastDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying forecast days in a ListView.
 * Uses domain model ForecastDay directly.
 */
public class ForecastAdapter extends ArrayAdapter<ForecastDay> {

    private final LayoutInflater inflater;

    public ForecastAdapter(@NonNull Context context, @NonNull List<ForecastDay> forecast) {
        super(context, 0, forecast);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            holder = new ViewHolder();
            holder.text1 = convertView.findViewById(android.R.id.text1);
            holder.text2 = convertView.findViewById(android.R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ForecastDay forecast = getItem(position);
        if (forecast != null) {
            holder.text1.setText(forecast.getDate() + " - " + forecast.getFormattedTemperature());

            String description = forecast.getDescription();
            if (description != null && !description.isEmpty()) {
                // Capitalize first letter
                description = description.substring(0, 1).toUpperCase() + description.substring(1);
            }
            holder.text2.setText(description);
        }

        return convertView;
    }

    /**
     * Update adapter data.
     */
    public void updateData(List<ForecastDay> newData) {
        clear();
        if (newData != null) {
            addAll(newData);
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView text1;
        TextView text2;
    }
}
