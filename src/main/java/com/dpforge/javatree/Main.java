package com.dpforge.javatree;

import com.dpforge.tellon.core.ProjectWalker;
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
            process(arguments);
        } catch (ParseException e) {
            arguments.printHelp();
        }
    }

    private static void process(final Arguments arguments) throws IOException {
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

        projectWalker.init(arguments.getProjectWalkerArgs());

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
}
