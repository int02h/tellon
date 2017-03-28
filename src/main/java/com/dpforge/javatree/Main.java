package com.dpforge.javatree;

import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.ProjectWalkerException;
import com.dpforge.tellon.core.Tellon;
import com.dpforge.tellon.core.notifier.ChangesNotifier;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        final Arguments arguments = new Arguments();
        try {
            arguments.parse(args);
        } catch (ParseException e) {
            arguments.printHelp();
            return;
        }

        if (arguments.hasVerify()) {
            verify();
        } else {
            notifyChanges(arguments);
        }
    }

    private static void notifyChanges(final Arguments arguments) throws IOException {
        final List<ProjectWalker> walkers = Extensions.getInstance().getProjectWalkers();

        final ProjectWalker projectWalker;
        if (walkers.isEmpty()) {
            throw new IllegalArgumentException("No project walker found");
        }

        if (arguments.getProjectWalkerName() != null) {
            projectWalker = getProjectWalker(walkers, arguments.getProjectWalkerName());
            if (projectWalker == null) {
                throw new IllegalArgumentException(
                        "Could not find project walker with name '" + arguments.getProjectWalkerName() + "'");
            }
        } else {
            if (walkers.size() == 1) {
                projectWalker = walkers.get(0);
            } else {
                throw new IllegalArgumentException("More than one project walkers found. Choose one of them.");
            }
        }

        try {
            projectWalker.init(arguments.getProjectWalkerArgs());
        } catch (ProjectWalkerException e) {
            throw new IllegalArgumentException("Fail to initialize project walker", e);
        }

        final List<ChangesNotifier> notifiers = Extensions.getInstance().getNotifiers();
        if (notifiers.isEmpty()) {
            throw new IllegalArgumentException("No changes notifier found");
        }

        final Tellon tellon = new Tellon();
        tellon.addNotifiers(notifiers);
        tellon.process(projectWalker);
    }

    private static ProjectWalker getProjectWalker(final List<ProjectWalker> walkers, final String name) {
        for (ProjectWalker walker : walkers) {
            if (name.equals(walker.getName())) {
                return walker;
            }
        }
        return null;
    }

    private static void verify() {
        final List<ChangesNotifier> notifiers = Extensions.getInstance().getNotifiers();
        System.out.println("Notifiers");
        for (ChangesNotifier notifier : notifiers) {
            System.out.format("%s (%s:) - %s", notifier.getName(), notifier.getPrefix(), notifier.getDescription());
            System.out.println();
        }

        System.out.println();

        final List<ProjectWalker> walkers = Extensions.getInstance().getProjectWalkers();
        System.out.println("Projects walkers");
        for (ProjectWalker walker : walkers) {
            System.out.format("%s - %s", walker.getName(), walker.getDescription());
            System.out.println();
        }
    }
}
