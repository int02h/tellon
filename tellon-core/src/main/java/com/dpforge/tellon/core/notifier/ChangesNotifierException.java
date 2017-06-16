package com.dpforge.tellon.core.notifier;

import java.io.IOException;

public class ChangesNotifierException extends Exception {
    public ChangesNotifierException() {

    }

    public ChangesNotifierException(IOException e) {
        super(e);
    }
}
