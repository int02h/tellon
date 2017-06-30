package com.dpforge.tellon.core.notifier;

public interface ProjectNotifier {
    String getName();

    String getDescription();

    void init() throws ChangesNotifierException;

    void reportError(final String watcher, final String errorMessage);

    ChangesNotifier getChangesNotifier();
}
