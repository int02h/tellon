package com.dpforge.javatree.commands;

import com.dpforge.javatree.Errors;
import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.ProjectWalkerException;
import com.dpforge.tellon.core.Tellon;
import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ChangesNotifierException;
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
        final List<ProjectWalker> walkers = context.getWalkers();

        final ProjectWalker projectWalker;
        if (walkers.isEmpty()) {
            throw new CommandExecutionException(Errors.BAD_CONFIG, "No project walker found");
        }

        if (arguments.getProjectWalkerName() != null) {
            projectWalker = getProjectWalker(walkers, arguments.getProjectWalkerName());
            if (projectWalker == null) {
                throw new CommandExecutionException(Errors.BAD_CONFIG,
                        "Could not find project walker with name '" + arguments.getProjectWalkerName() + "'");
            }
        } else {
            if (walkers.size() == 1) {
                projectWalker = walkers.get(0);
            } else {
                throw new CommandExecutionException(Errors.BAD_CONFIG,
                        "More than one project walkers found. Choose one of them.");
            }
        }

        try {
            projectWalker.init(arguments.getProjectWalkerArgs());
        } catch (ProjectWalkerException e) {
            throw new CommandExecutionException(Errors.INIT_FAIL, "Fail to initialize project walker", e);
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
            tellon.process(projectWalker);
        } catch (IOException e) {
            throw new CommandExecutionException(Errors.EXECUTION_FAIL, "Fail to notify", e);
        }
    }

    private static ProjectWalker getProjectWalker(final List<ProjectWalker> walkers, final String name) {
        for (ProjectWalker walker : walkers) {
            if (name.equals(walker.getName())) {
                return walker;
            }
        }
        return null;
    }
}
