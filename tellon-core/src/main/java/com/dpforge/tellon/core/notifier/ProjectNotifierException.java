package com.dpforge.tellon.core.notifier;

import java.io.IOException;

public class ProjectNotifierException extends Exception {
    public ProjectNotifierException() {

    }

    public ProjectNotifierException(IOException e) {
        super(e);
    }
}
