package com.dpforge.javatree;

import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.notifier.ChangesNotifier;

import java.util.*;

class Extensions {
    private final List<ProjectWalker> walkers = new ArrayList<>();
    private final List<ChangesNotifier> notifiers = new ArrayList<>();

    private static Extensions instance;

    static synchronized Extensions getInstance() {
        if (instance == null) {
            instance = new Extensions();
            instance.init();
        }
        return instance;
    }

    private void init() {
        for (ProjectWalker walker : ServiceLoader.load(ProjectWalker.class)) {
            walkers.add(walker);
        }

        for (ChangesNotifier notifier : ServiceLoader.load(ChangesNotifier.class)) {
            notifiers.add(notifier);
        }
    }

    public List<ProjectWalker> getProjectWalkers() {
        return walkers;
    }

    public List<ChangesNotifier> getNotifiers() {
        return notifiers;
    }
}
