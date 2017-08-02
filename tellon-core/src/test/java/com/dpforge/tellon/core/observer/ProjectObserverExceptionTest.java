package com.dpforge.tellon.core.observer;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/*
 These tests may look useless and ridiculous.
 But it is necessary for 100% code coverage.
 */
public class ProjectObserverExceptionTest {
    @Test
    public void notData() {
        final ProjectObserverException e = new ProjectObserverException();
        assertNull(e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    public void withMessage() {
        final ProjectObserverException e = new ProjectObserverException("Test error message");
        assertEquals("Test error message", e.getMessage());
    }

    @Test
    public void withCause() {
        final IOException cause = new IOException("Oops");
        final ProjectObserverException e = new ProjectObserverException(cause);
        assertEquals(cause, e.getCause());
    }

    @Test
    public void withMessageAndCause() {
        final IOException cause = new IOException("Oops");
        final ProjectObserverException e = new ProjectObserverException("Test error message", cause);
        assertEquals("Test error message", e.getMessage());
        assertEquals(cause, e.getCause());
    }
}