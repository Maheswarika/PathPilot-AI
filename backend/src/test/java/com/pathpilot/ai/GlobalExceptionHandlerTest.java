package com.pathpilot.ai;

import com.pathpilot.ai.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler =
            new GlobalExceptionHandler();

    @Test
    void databaseFailureReturnsSanitizedServiceUnavailableMessage() {

        RuntimeException exception = new RuntimeException(
                "authentication wrapper",
                new DataAccessResourceFailureException(
                        "database credentials must not be returned"
                )
        );

        ResponseEntity<Map<String, String>> response =
                exceptionHandler.handleUnexpected(exception);

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "The database is unavailable. Check that MySQL is running and the configured database is accessible.",
                response.getBody().get("message")
        );

        assertFalse(
                response.getBody().get("message").contains("credentials")
        );
    }
}