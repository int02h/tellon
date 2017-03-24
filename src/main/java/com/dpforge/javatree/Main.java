package com.dpforge.javatree;

import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.Tellon;
import com.dpforge.tellon.core.notifier.ChangesNotifier;

import java.io.IOException;
import java.util.ServiceLoader;

public class Main {

    public static void main(String[] args) throws IOException {
        ProjectWalker projectWalker = null;
        for (ProjectWalker walker : ServiceLoader.load(ProjectWalker.class)) {
            if (projectWalker == null) {
                projectWalker = walker;
            } else {
                throw new IllegalArgumentException("Too many project walkers");
            }
        }

        if (projectWalker == null) {
            throw new IllegalArgumentException("No project walker found");
        }
        projectWalker.init("");

        final Tellon tellon = new Tellon();

        boolean noNotifiers = true;
        for (ChangesNotifier notifier : ServiceLoader.load(ChangesNotifier.class)) {
            tellon.addNotifier(notifier);
            noNotifiers = false;
        }

        if (noNotifiers) {
            throw new IllegalArgumentException("No changes notifier found");
        }

        tellon.process(projectWalker);
    }
}
