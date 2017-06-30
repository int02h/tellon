package com.dpforge.tellon.core.notifier;

public interface ProjectNotifier {
    String getName();

    String getDescription();

    void init() throws ProjectNotifierException;

    void reportError(final String watcher, final String errorMessage);

    ChangesNotifier getChangesNotifier();
}
