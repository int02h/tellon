package com.dpforge.tellon.core.observer;

public class ProjectObserverException extends Exception {
    public ProjectObserverException(String message) {
        super(message);
    }

    public ProjectObserverException(Exception e) {
        super(e);
    }
}
