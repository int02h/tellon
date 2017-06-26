package com.dpforge.javatree;

import com.dpforge.javatree.commands.Command;
import com.dpforge.javatree.commands.CommandContext;
import com.dpforge.javatree.commands.CommandExecutionException;
import com.dpforge.javatree.commands.CommandFactory;
import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.notifier.ChangesNotifier;

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
                    .walkers(getWalkers())
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

    private static List<ProjectWalker> getWalkers() {
        final List<ProjectWalker> walkers = new ArrayList<>();
        for (ProjectWalker walker : ServiceLoader.load(ProjectWalker.class)) {
            walkers.add(walker);
        }
        return walkers;
    }

    private static List<ChangesNotifier> getNotifiers() {
        final List<ChangesNotifier> notifiers = new ArrayList<>();
        for (ChangesNotifier notifier : ServiceLoader.load(ChangesNotifier.class)) {
            notifiers.add(notifier);
        }
        return notifiers;
    }
}
