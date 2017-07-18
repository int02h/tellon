package com.dpforge.tellon.app;

import com.dpforge.tellon.app.commands.Command;
import com.dpforge.tellon.app.commands.CommandContext;
import com.dpforge.tellon.app.commands.CommandExecutionException;
import com.dpforge.tellon.app.commands.CommandFactory;
import com.dpforge.tellon.core.notifier.ProjectNotifier;
import com.dpforge.tellon.core.observer.ProjectObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

public class Main {

    public static void main(String[] args) throws IOException {
        final String commandName;
        if (args.length == 0) {
            commandName = "help";
        } else {
            commandName = args[0];
        }

        args = Arrays.copyOfRange(args, 1, args.length);

        try {
            executeCommand(commandName, args);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            System.exit(Errors.RUNTIME_FAIL);
        }
    }

    private static void executeCommand(final String commandName, final String[] args) {
        final Command command = CommandFactory.create(commandName);
        if (command.parseArguments(args)) {
            final CommandContext commandContext = new CommandContext.Builder()
                    .observers(getObservers())
                    .notifiers(getNotifiers())
                    .log(System.out).build();
            try {
                command.execute(commandContext);
            } catch (CommandExecutionException e) {
                printErrorWithCauses(e);
                System.exit(e.getErrorCode());
            }
        } else {
            command.printHelp(System.out);
            System.exit(Errors.BAD_ARGS);
        }
    }

    private static void printErrorWithCauses(Throwable error) {
        while (error != null) {
            System.err.println(error.getMessage());
            error = error.getCause();
        }
    }

    private static List<ProjectObserver> getObservers() {
        final List<ProjectObserver> observers = new ArrayList<>();
        for (ProjectObserver observer : ServiceLoader.load(ProjectObserver.class)) {
            observers.add(observer);
        }
        return observers;
    }

    private static List<ProjectNotifier> getNotifiers() {
        final List<ProjectNotifier> notifiers = new ArrayList<>();
        for (ProjectNotifier notifier : ServiceLoader.load(ProjectNotifier.class)) {
            notifiers.add(notifier);
        }
        return notifiers;
    }
}
