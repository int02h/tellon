package com.dpforge.tellon.app.commands;

import com.dpforge.tellon.app.Errors;
import com.dpforge.tellon.core.Tellon;
import com.dpforge.tellon.core.notifier.ProjectNotifierException;
import com.dpforge.tellon.core.notifier.ProjectNotifier;
import com.dpforge.tellon.core.observer.ProjectObserver;
import com.dpforge.tellon.core.observer.ProjectObserverException;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        final Tellon tellon = new Tellon();
        final List<ProjectNotifier> notifiers = initNotifiers(context, tellon);

        try {
            tellon.process(initObserver(context));
        } catch (IOException e) {
            reportError(e, notifiers);
            throw new CommandExecutionException(Errors.EXECUTION_FAIL, "Fail to notify", e);
        } catch (CommandExecutionException e) {
            reportError(e, notifiers);
            throw e;
        }
    }

    private void reportError(final Throwable t, final List<ProjectNotifier> notifiers) {
        final List<String> masterWatchers = arguments.getMasterWatchers();
        if (masterWatchers.isEmpty()) {
            return;
        }

        final StringWriter out = new StringWriter();
        final PrintWriter writer = new PrintWriter(out);
        writer.println("Some error has occurred during Tellon execution");
        writer.println();
        t.printStackTrace(writer);

        reportError(out.toString(), notifiers);
    }

    private void reportError(final String message, final List<ProjectNotifier> notifiers) {
        if (message.isEmpty()) {
            return;
        }

        final List<String> masterWatchers = arguments.getMasterWatchers();
        if (masterWatchers.isEmpty()) {
            return;
        }

        for (ProjectNotifier notifier : notifiers) {
            notifier.reportError(masterWatchers, message);
        }
    }

    private List<ProjectNotifier> initNotifiers(final CommandContext context, final Tellon tellon) throws CommandExecutionException {
        final List<ProjectNotifier> notifiers = context.getNotifiers();
        if (notifiers.isEmpty()) {
            throw new CommandExecutionException(Errors.BAD_CONFIG, "No project notifiers found");
        }

        final Set<String> enabled = new HashSet<>(arguments.getEnabledNotifiers());
        final List<ProjectNotifier> initialized = new ArrayList<>();
        final Map<ProjectNotifier, ProjectNotifierException> failed = new HashMap<>();

        for (ProjectNotifier notifier : notifiers) {
            boolean needInit = enabled.isEmpty() || enabled.contains(notifier.getName());

            if (needInit) {
                try {
                    notifier.init();
                    initialized.add(notifier);
                    tellon.addNotifier(notifier.getChangesNotifier());
                } catch (ProjectNotifierException e) {
                    failed.put(notifier, e);
                }
                enabled.remove(notifier.getName());
            }
        }

        if (initialized.isEmpty()) {
            throw new CommandExecutionException(Errors.INIT_FAIL, "Neither of project notifiers has been initialized");
        }

        if (!failed.isEmpty()) {
            reportError(getFailedNotifiersMessage(failed), initialized);
            throw new CommandExecutionException(Errors.INIT_FAIL, "Project notifiers has not been initialized: " + getNames(failed));
        }

        if (!enabled.isEmpty()) {
            reportError(getNotFoundNotifiersMessage(enabled), initialized);
            throw new CommandExecutionException(Errors.INIT_FAIL, "Project notifiers has not been found: " + getNames(enabled));
        }

        return initialized;
    }

    private ProjectObserver initObserver(final CommandContext context) throws CommandExecutionException {
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

        return projectObserver;
    }

    private static ProjectObserver getProjectObserver(final List<ProjectObserver> observers, final String name) {
        for (ProjectObserver observer : observers) {
            if (name.equals(observer.getName())) {
                return observer;
            }
        }
        return null;
    }

    private static String getFailedNotifiersMessage(final Map<ProjectNotifier, ProjectNotifierException> failed) {
        final StringWriter out = new StringWriter();
        final PrintWriter writer = new PrintWriter(out);
        writer.println("Following project notifiers has not been initialized:");
        writer.println();
        for (Map.Entry<ProjectNotifier, ProjectNotifierException> entry : failed.entrySet()) {
            writer.println(entry.getKey().getName());
            entry.getValue().printStackTrace(writer);
            writer.println();
        }
        return out.toString();
    }

    private static String getNotFoundNotifiersMessage(final Set<String> names) {
        final StringWriter out = new StringWriter();
        final PrintWriter writer = new PrintWriter(out);
        writer.println("Following project notifiers has not been found:");
        for (String name : names) {
            writer.print("- ");
            writer.println(name);
        }
        return out.toString();
    }

    private static String getNames(final Map<ProjectNotifier, ProjectNotifierException> names) {
        final StringBuilder builder = new StringBuilder();
        for (ProjectNotifier notifier : names.keySet()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(notifier.getName());
        }
        return builder.toString();
    }

    private String getNames(final Set<String> names) {
        final StringBuilder builder = new StringBuilder();
        for (String notifier : names) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(notifier);
        }
        return builder.toString();
    }
}
