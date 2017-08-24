package com.dpforge.tellon.app.commands;

public class CommandExecutionException extends Exception {
    private final int errorCode;

    public CommandExecutionException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CommandExecutionException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
