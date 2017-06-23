package com.dpforge.javatree.commands;

import com.dpforge.tellon.core.ProjectWalkerException;

public class CommandExecutionException extends Exception {
    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
