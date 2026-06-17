package com.example.weatherforecastapp.domain.model;

/**
 * Domain model representing a favorite city saved by the user.
 */
public class FavoriteCity {
    private final long id;
    private final String cityName;
    private final long createdAt;

    public FavoriteCity(long id, String cityName, long createdAt) {
        this.id = id;
        this.cityName = cityName;
        this.createdAt = createdAt;
    }

    public FavoriteCity(String cityName) {
        this(0, cityName, System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteCity that = (FavoriteCity) o;
        return cityName != null ? cityName.equalsIgnoreCase(that.cityName) : that.cityName == null;
    }

    @Override
    public int hashCode() {
        return cityName != null ? cityName.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FavoriteCity{" +
                "id=" + id +
                ", cityName='" + cityName + '\'' +
                '}';
    }
}
