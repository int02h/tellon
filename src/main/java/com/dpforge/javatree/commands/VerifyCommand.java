package com.dpforge.javatree.commands;

import com.dpforge.tellon.core.observer.ProjectObserver;
import com.dpforge.tellon.core.notifier.ChangesNotifier;

import java.io.PrintStream;
import java.util.List;

public class VerifyCommand extends Command {

    VerifyCommand() {
    }

    @Override
    public boolean parseArguments(String[] args) {
        //noinspection RedundantIfStatement
        if (args.length == 1 && "--help".equals(args[0])) {
            return false;
        }
        return true;
    }

    @Override
    public void printHelp(final PrintStream stream) {
        stream.println("usage: tellon verify");
        stream.println("It will print info about tellon's environment: registered project observers and notifiers");
    }

    @Override
    public void execute(final CommandContext context) throws CommandExecutionException {
        final List<ChangesNotifier> notifiers = context.getNotifiers();
        System.out.println("Notifiers");
        for (ChangesNotifier notifier : notifiers) {
            System.out.format("* %s%n  %s", notifier.getName(), notifier.getDescription());
            System.out.println();
        }

        System.out.println();

        final List<ProjectObserver> observers = context.getObservers();
        System.out.println("Projects observers");
        for (ProjectObserver observer : observers) {
            System.out.format("* %s%n  %s", observer.getName(), observer.getDescription());
            System.out.println();
        }
    }
}
