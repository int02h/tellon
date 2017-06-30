package com.dpforge.tellon.core.notifier;

@SuppressWarnings("unused")
public class ProjectNotifierException extends Exception {
    public ProjectNotifierException() {
        super();
    }

    public ProjectNotifierException(String message) {
        super(message);
    }

    public ProjectNotifierException(Throwable cause) {
        super(cause);
    }

    public ProjectNotifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
