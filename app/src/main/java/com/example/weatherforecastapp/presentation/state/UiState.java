package com.example.weatherforecastapp.presentation.state;

/**
 * Generic UI State wrapper for handling Loading, Success, and Error states.
 * This pattern ensures consistent state handling across all screens.
 *
 * @param <T> The type of data in the success state
 */
public abstract class UiState<T> {

    private UiState() {
        // Sealed-like pattern in Java
    }

    public boolean isLoading() {
        return this instanceof Loading;
    }

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isError() {
        return this instanceof Error;
    }

    /**
     * Get data if this is a Success state, null otherwise
     */
    public T getDataOrNull() {
        if (this instanceof Success) {
            return ((Success<T>) this).getData();
        }
        return null;
    }

    /**
     * Get error message if this is an Error state, null otherwise
     */
    public String getErrorMessageOrNull() {
        if (this instanceof Error) {
            return ((Error<T>) this).getMessage();
        }
        return null;
    }

    // ==================== Static Factory Methods ====================

    public static <T> UiState<T> loading() {
        return new Loading<>();
    }

    public static <T> UiState<T> success(T data) {
        return new Success<>(data);
    }

    public static <T> UiState<T> error(String message) {
        return new Error<>(message, null);
    }

    public static <T> UiState<T> error(String message, Throwable throwable) {
        return new Error<>(message, throwable);
    }

    // ==================== State Classes ====================

    /**
     * Loading state - data is being fetched
     */
    public static final class Loading<T> extends UiState<T> {
        public Loading() {
        }
    }

    /**
     * Success state - data has been loaded successfully
     */
    public static final class Success<T> extends UiState<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    /**
     * Error state - an error occurred while fetching data
     */
    public static final class Error<T> extends UiState<T> {
        private final String message;
        private final Throwable throwable;

        public Error(String message, Throwable throwable) {
            this.message = message;
            this.throwable = throwable;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}
