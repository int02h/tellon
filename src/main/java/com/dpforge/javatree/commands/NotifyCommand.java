package com.dpforge.javatree.commands;

import com.dpforge.javatree.Errors;
import com.dpforge.tellon.core.Tellon;
import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ChangesNotifierException;
import com.dpforge.tellon.core.observer.ProjectObserver;
import com.dpforge.tellon.core.observer.ProjectObserverException;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class NotifyCommand extends Command {
    private final NotifyArguments arguments = new NotifyArguments();

    NotifyCommand() {
    }

    @Override
    public boolean parseArguments(String[] args) {
        try {
            arguments.parse(args);
            return true;
        } catch (ParseException ignored) {
            return false;
        }
    }

    @Override
    public void printHelp(final PrintStream stream) {
        arguments.printHelp(stream);
    }

    @Override
    public void execute(final CommandContext context) throws CommandExecutionException {
        final List<ProjectObserver> observers = context.getObservers();

        final ProjectObserver projectObserver;
        if (observers.isEmpty()) {
            throw new CommandExecutionException(Errors.BAD_CONFIG, "No project observer found");
        }

        if (arguments.getProjectObserverName() != null) {
            projectObserver = getProjectObserver(observers, arguments.getProjectObserverName());
            if (projectObserver == null) {
                throw new CommandExecutionException(Errors.BAD_CONFIG,
                        "Could not find project observer with name '" + arguments.getProjectObserverName() + "'");
            }
        } else {
            if (observers.size() == 1) {
                projectObserver = observers.get(0);
            } else {
                throw new CommandExecutionException(Errors.BAD_CONFIG,
                        "More than one project observers found. Choose one of them.");
            }
        }

        try {
            projectObserver.init(arguments.getProjectObserverArgs());
        } catch (ProjectObserverException e) {
            throw new CommandExecutionException(Errors.INIT_FAIL, "Fail to initialize project observer", e);
        }

        final List<ChangesNotifier> notifiers = context.getNotifiers();
        if (notifiers.isEmpty()) {
            throw new CommandExecutionException(Errors.BAD_CONFIG, "No changes notifier found");
        }

        for (ChangesNotifier notifier : notifiers) {
            try {
                notifier.init();
            } catch (ChangesNotifierException e) {
                throw new CommandExecutionException(Errors.INIT_FAIL,
                        "Fail to initialize notifier " + notifier.getName(), e);
            }
        }

        final Tellon tellon = new Tellon();
        tellon.addNotifiers(notifiers);
        try {
            tellon.process(projectObserver);
        } catch (IOException e) {
            throw new CommandExecutionException(Errors.EXECUTION_FAIL, "Fail to notify", e);
        }
    }

    private static ProjectObserver getProjectObserver(final List<ProjectObserver> observers, final String name) {
        for (ProjectObserver observer : observers) {
            if (name.equals(observer.getName())) {
                return observer;
            }
        }
        return null;
    }
}
