package com.example.weatherforecastapp.data.remote;

import com.example.weatherforecastapp.data.remote.dto.CurrentWeatherResponse;
import com.example.weatherforecastapp.data.remote.model.ForecastResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies Retrofit WeatherApiService request shaping and JSON parsing against
 * MockWebServer only — no real OpenWeatherMap network calls.
 */
public class WeatherApiServiceTest {

    private static final String FAKE_API_KEY = "test-api-key-not-real";

    private MockWebServer server;
    private WeatherApiService apiService;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        apiService = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService.class);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void getCurrentWeather_parsesSuccessBodyAndSendsExpectedQueryParams() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{"
                        + "\"name\":\"London\","
                        + "\"dt\":1700000000,"
                        + "\"sys\":{\"country\":\"GB\"},"
                        + "\"main\":{\"temp\":12.5,\"feels_like\":11.0,\"humidity\":70},"
                        + "\"weather\":[{\"description\":\"light rain\",\"icon\":\"10d\"}],"
                        + "\"wind\":{\"speed\":4.1}"
                        + "}"));

        Response<CurrentWeatherResponse> response =
                apiService.getCurrentWeather("London", "metric", FAKE_API_KEY).execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("London", response.body().cityName);
        assertEquals("GB", response.body().sys.country);
        assertEquals(12.5, response.body().main.temp, 0.001);
        assertEquals("light rain", response.body().weather.get(0).description);

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertTrue(request.getPath().startsWith("/weather?"));
        assertTrue(request.getPath().contains("q=London"));
        assertTrue(request.getPath().contains("units=metric"));
        assertTrue(request.getPath().contains("appid=" + FAKE_API_KEY));
    }

    @Test
    public void getCurrentWeather_propagatesHttpErrorWithoutThrowing() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"cod\":\"404\",\"message\":\"city not found\"}"));

        Response<CurrentWeatherResponse> response =
                apiService.getCurrentWeather("UnknownCity", "metric", FAKE_API_KEY).execute();

        assertEquals(404, response.code());
        assertTrue(!response.isSuccessful());
    }

    @Test
    public void getFiveDayForecast_parsesListItems() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{"
                        + "\"list\":["
                        + "{\"dt_txt\":\"2026-03-01 12:00:00\",\"main\":{\"temp\":10.0},"
                        + "\"weather\":[{\"description\":\"clouds\"}]},"
                        + "{\"dt_txt\":\"2026-03-01 15:00:00\",\"main\":{\"temp\":12.0},"
                        + "\"weather\":[{\"description\":\"clear sky\"}]}"
                        + "]"
                        + "}"));

        Response<ForecastResponse> response =
                apiService.getFiveDayForecast("Paris", "metric", FAKE_API_KEY).execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(2, response.body().list.size());
        assertEquals("2026-03-01 12:00:00", response.body().list.get(0).dtTxt);
        assertEquals(10.0, response.body().list.get(0).main.temp, 0.001);

        RecordedRequest request = server.takeRequest();
        assertTrue(request.getPath().startsWith("/forecast?"));
        assertTrue(request.getPath().contains("q=Paris"));
        assertTrue(request.getPath().contains("appid=" + FAKE_API_KEY));
    }
}
