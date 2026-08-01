package com.sms.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceNotFoundExceptionTest {

    @Test
    void constructorWithMessage_setsMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Student not found");

        assertEquals("Student not found", exception.getMessage());
    }

    @Test
    void constructorWithResourceDetails_formatsMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Student", "id", 10L);

        assertTrue(exception.getMessage().contains("Student"));
        assertTrue(exception.getMessage().contains("id"));
        assertTrue(exception.getMessage().contains("10"));
    }
}
