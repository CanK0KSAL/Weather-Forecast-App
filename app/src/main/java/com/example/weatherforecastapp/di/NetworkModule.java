package com.example.weatherforecastapp.di;

import android.util.Log;

import com.example.weatherforecastapp.BuildConfig;
import com.example.weatherforecastapp.data.remote.WeatherApiService;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Hilt module providing network-related dependencies.
 * Provides OkHttp, Retrofit, and API service instances.
 */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String OKHTTP_LOG_TAG = "OkHttp";
    private static final Pattern APP_ID_QUERY_PATTERN =
            Pattern.compile("([?&]appid=)[^&\\s]*", Pattern.CASE_INSENSITIVE);

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        // Redact appid from logged request URLs so credentials are not printed.
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message ->
                Log.d(OKHTTP_LOG_TAG, redactAppId(message)));
        interceptor.setLevel(loggingLevel(BuildConfig.DEBUG));
        return interceptor;
    }

    /**
     * Debug builds may log request lines (with appid redacted).
     * Release builds disable HTTP request logging entirely.
     */
    static HttpLoggingInterceptor.Level loggingLevel(boolean debug) {
        return debug
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE;
    }

    static String redactAppId(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        return APP_ID_QUERY_PATTERN.matcher(message).replaceAll("$1***");
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public WeatherApiService provideWeatherApiService(Retrofit retrofit) {
        return retrofit.create(WeatherApiService.class);
    }
}
