# Weather Forecast App

Android weather application written in **Java**, using MVVM, LiveData, Repository pattern, Retrofit, Room, Hilt, and WorkManager with the OpenWeatherMap API.

## Features

- Search current weather and a multi-day forecast for a city
- Aggregate OpenWeatherMap 3-hour forecast entries into daily summaries
- Save and manage favorite cities with Room
- Periodic background sync of favorite-city weather via WorkManager (every 6 hours when connected), with a low-priority notification on success
- Material 3 DayNight theme, Navigation Component with Safe Args, loading and error UI states

## Requirements

- Android Studio compatible with Android Gradle Plugin 8.10.0
- JDK 17 or newer (required for the Android build toolchain)
- Android SDK with `compileSdk` / `targetSdk` 35
- Min SDK 24
- An [OpenWeatherMap](https://openweathermap.org/api) API key
## Setup

1. Clone the repository and open it in Android Studio.
2. Ensure `local.properties` exists at the project root (Android Studio creates it with `sdk.dir`).
3. Add your API key to `local.properties` (this file is git-ignored):

   ```properties
   sdk.dir=/path/to/Android/Sdk
   OPENWEATHER_API_KEY=YOUR_OPENWEATHERMAP_API_KEY
   ```

4. Sync Gradle, then run the `app` configuration on an emulator or device (API 24+).

### API key configuration

Gradle reads `OPENWEATHER_API_KEY` from `local.properties` and exposes it as `BuildConfig.OPENWEATHER_API_KEY`. The repository uses that field for OpenWeatherMap requests.

If the key is missing or empty, weather requests fail with a clear configuration error instead of calling the API.

**Security note:** Embedding an API key in a client APK (including via `BuildConfig`) is not absolute secret protection. The value can still be extracted from a built app. Prefer restricted keys, server-side proxies for production, and never commit real keys to Git. OkHttp debug logging redacts `appid` query parameters so credentials are not printed in Logcat.

## Build and test

From the project root:

```bash
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

On Windows (PowerShell or cmd):

```bat
gradlew.bat assembleDebug
gradlew.bat testDebugUnitTest
```

Instrumented tests under `androidTest` require a device or emulator and are not covered by the unit-test command above.

## Architecture

Layered MVVM with a Repository abstraction:

| Layer | Role |
|-------|------|
| **Presentation** | Fragments, `WeatherViewModel`, `UiState`, adapters |
| **Domain** | `CurrentWeather`, `ForecastDay`, `FavoriteCity`, `WeatherMapper` |
| **Data** | Retrofit DTOs/API, Room entities/DAO, `WeatherRepositoryImpl` |
| **DI** | Hilt modules (`NetworkModule`, `DatabaseModule`, `RepositoryModule`) |
| **Work** | `WeatherSyncWorker` (`@HiltWorker`), `WorkManagerScheduler` |

`WeatherApp` is `@HiltAndroidApp`, implements `Configuration.Provider` with `HiltWorkerFactory`, and schedules periodic sync on startup. The default WorkManager App Startup initializer is disabled in the manifest so Hilt’s worker factory is used.

### Package layout

```
com.example.weatherforecastapp/
  data/
    local/          # Room
    remote/         # WeatherApiService, dto/, model/
    repository/
  domain/
    model/
    mapper/
  presentation/
    state/          # UiState
    viewmodel/
    ui/             # search, detail, favorites
    adapter/
  di/
  work/
  WeatherApp.java
  MainActivity.java
```

## Tech stack

| Area | Choice |
|------|--------|
| Language | Java 11 |
| DI | Hilt 2.51.1 |
| Navigation | Navigation Component 2.8.4 + Safe Args |
| Database | Room 2.6.1 |
| Network | Retrofit 2.9.0, OkHttp, Gson |
| Background | WorkManager 2.9.1 + Hilt Work |
| UI | Material 1.12.0, ViewModel / LiveData |

Versions are defined in `gradle/libs.versions.toml`.

## Testing

JVM unit tests (`testDebugUnitTest`) cover device-free logic:

- `WeatherMapper` — DTO/entity mapping and forecast day aggregation
- Domain models and `UiState` — formatting, equality, state factories
- `WeatherApiService` via MockWebServer — request query params and JSON parsing (no real API calls)
- `WeatherViewModel` with a fake repository — empty-input errors and LiveData mediation of loading/success/error
- OkHttp logging helpers — `appid` redaction; debug BASIC vs release NONE
- WorkManager/Hilt static checks — `WeatherApp` implements `Configuration.Provider`

**Not covered by JVM tests (need a device/emulator or separate CI):** Room favorites persistence, UI/navigation, periodic WorkManager scheduling, and notification delivery. A Robolectric attempt for Room/Worker failed on this Windows environment (`UnsatisfiedLinkError` loading Conscrypt native libraries), so those tests were not kept.

Replace `YOUR_OPENWEATHERMAP_API_KEY` with a key from your own OpenWeatherMap account. Do not commit real keys.

## License

Educational project for an Android development (Java) course.
