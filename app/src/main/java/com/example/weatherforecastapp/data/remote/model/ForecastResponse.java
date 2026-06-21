package com.example.weatherforecastapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    public List<ListItem> list;

    public static class ListItem {
        @SerializedName("dt_txt")
        public String dtTxt;

        @SerializedName("main")
        public Main main;

        @SerializedName("weather")
        public List<Weather> weather;
    }

    public static class Main {
        @SerializedName("temp")
        public double temp;
    }

    public static class Weather {
        @SerializedName("description")
        public String description;
    }
}
