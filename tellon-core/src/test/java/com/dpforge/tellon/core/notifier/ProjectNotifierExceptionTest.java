package com.dpforge.tellon.core.notifier;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/*
 These tests may look useless and ridiculous.
 But it is necessary for 100% code coverage.
 */
public class ProjectNotifierExceptionTest {
    @Test
    public void notData() {
        final ProjectNotifierException e = new ProjectNotifierException();
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    public void withMessage() {
        final ProjectNotifierException e = new ProjectNotifierException("Test error message");
        assertEquals("Test error message", e.getMessage());
    }

    @Test
    public void withCause() {
        final IOException cause = new IOException("Oops");
        final ProjectNotifierException e = new ProjectNotifierException(cause);
        assertEquals(cause, e.getCause());
    }

    @Test
    public void withMessageAndCause() {
        final IOException cause = new IOException("Oops");
        final ProjectNotifierException e = new ProjectNotifierException("Test error message", cause);
        assertEquals("Test error message", e.getMessage());
        assertEquals(cause, e.getCause());
    }
}