package com.dpforge.tellon.core.observer;

@SuppressWarnings("unused")
public class ProjectObserverException extends Exception {
    public ProjectObserverException() {
        super();
    }

    public ProjectObserverException(String message) {
        super(message);
    }

    public ProjectObserverException(Throwable cause) {
        super(cause);
    }

    public ProjectObserverException(String message, Throwable cause) {
        super(message, cause);
    }
}
