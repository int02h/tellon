package com.dpforge.tellon.core;

import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.walker.ProjectInfo;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.walker.ProjectItem;
import com.dpforge.tellon.core.walker.ProjectObserver;
import com.dpforge.tellon.core.walker.ProjectWalker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Tellon {
    private final List<ChangesNotifier> notifiers = new ArrayList<>();

    public Tellon addNotifier(final ChangesNotifier notifier) {
        notifiers.add(notifier);
        return this;
    }

    public Tellon addNotifiers(final Collection<ChangesNotifier> notifiers) {
        this.notifiers.addAll(notifiers);
        return this;
    }

    public void process(final ProjectObserver observer) throws IOException {
        final ChangesBuilder changesBuilder = new ChangesBuilder();
        final ProjectWalker walker = observer.createWalker();

        onStartProject(observer.getProjectInfo());
        while (walker.hasNext()) {
            final ProjectItem item = walker.next();
            final boolean hasActual = item.hasActual();
            final boolean hasPrev = item.hasPrevious();
            if (hasActual && hasPrev) {
                final SourceCode actual = item.getActual();
                final SourceCode prev = item.getPrevious();
                final Changes changes = changesBuilder.build(prev, actual);
                notifyChanges(item, changes);
            } else if (hasActual) {
                notifyItemAdded(item, changesBuilder.buildInserted(item.getActual()));
            } else if (hasPrev) {
                notifyItemDeleted(item, changesBuilder.buildDeleted(item.getPrevious()));
            }
        }
        onFinishedProject();
    }

    void onStartProject(final ProjectInfo projectInfo) {
        for (ChangesNotifier notifier : notifiers) {
            notifier.onStartProject(projectInfo);
        }
    }

    void onFinishedProject() {
        for (ChangesNotifier notifier : notifiers) {
            notifier.onFinishedProject();
        }
    }

    private void notifyChanges(final ProjectItem item, final Changes changes) {
        if (changes.isEmpty()) {
            return;
        }
        for (ChangesNotifier notifier : notifiers) {
            notifier.notifyChanges(item, changes);
        }
    }

    private void notifyItemAdded(final ProjectItem item, final Changes changes) {
        if (changes.isEmpty()) {
            return;
        }
        for (ChangesNotifier notifier : notifiers) {
            notifier.notifyItemAdded(item, changes);
        }
    }

    private void notifyItemDeleted(final ProjectItem item, final Changes changes) {
        if (changes.isEmpty()) {
            return;
        }
        for (ChangesNotifier notifier : notifiers) {
            notifier.notifyItemDeleted(item, changes);
        }
    }
}
