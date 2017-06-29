package com.dpforge.tellon.core.walker;

public class ProjectWalkerException extends Exception {
    public ProjectWalkerException(String message) {
        super(message);
    }

    public ProjectWalkerException(Exception e) {
        super(e);
    }
}
