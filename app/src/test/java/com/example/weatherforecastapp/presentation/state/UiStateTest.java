package com.example.weatherforecastapp.presentation.state;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies UiState factory methods and type checks for Loading, Success, and Error.
 */
public class UiStateTest {

    @Test
    public void loading_isOnlyLoading() {
        UiState<String> state = UiState.loading();
        assertTrue(state.isLoading());
        assertFalse(state.isSuccess());
        assertFalse(state.isError());
        assertNull(state.getDataOrNull());
        assertNull(state.getErrorMessageOrNull());
    }

    @Test
    public void success_exposesData() {
        UiState<Integer> state = UiState.success(42);
        assertTrue(state.isSuccess());
        assertFalse(state.isLoading());
        assertFalse(state.isError());
        assertEquals(Integer.valueOf(42), state.getDataOrNull());
        assertNull(state.getErrorMessageOrNull());
    }

    @Test
    public void error_exposesMessageAndOptionalThrowable() {
        RuntimeException cause = new RuntimeException("boom");
        UiState<String> state = UiState.error("failed", cause);

        assertTrue(state.isError());
        assertFalse(state.isLoading());
        assertFalse(state.isSuccess());
        assertNull(state.getDataOrNull());
        assertEquals("failed", state.getErrorMessageOrNull());
        assertEquals(cause, ((UiState.Error<String>) state).getThrowable());
    }
}
