package com.pathpilot.ai;

import com.pathpilot.ai.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void databaseFailureReturnsSanitizedServiceUnavailableMessage() {
        var response = exceptionHandler.handleUnexpected(
                new RuntimeException("authentication wrapper",
                        new DataAccessResourceFailureException("database credentials must not be returned"))
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(
                "The database is unavailable. Check that MySQL is running and the configured database is accessible.",
                response.getBody().get("message")
        );
        assertFalse(response.getBody().get("message").contains("credentials"));
    }
}
