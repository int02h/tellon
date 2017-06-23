package com.dpforge.javatree.commands;

import java.io.PrintStream;

public abstract class Command {
    public abstract boolean parseArguments(final String[] args);

    public abstract void printHelp(final PrintStream stream);

    public abstract void execute(final CommandContext context) throws CommandExecutionException;
}
