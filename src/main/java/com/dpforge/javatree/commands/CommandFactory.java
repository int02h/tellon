package com.dpforge.javatree.commands;

public class CommandFactory {
    public static Command create(final String name) {
        switch (name) {
            case "verify":
                return new VerifyCommand();
            case "notify":
                return new NotifyCommand();
            case "help":
            default:
                return new HelpCommand();
        }
    }
}
