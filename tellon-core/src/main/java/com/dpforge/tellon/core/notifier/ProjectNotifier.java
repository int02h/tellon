package com.dpforge.tellon.core.notifier;

import java.util.Collection;

public interface ProjectNotifier {
    String getName();

    String getDescription();

    void init() throws ProjectNotifierException;

    void reportError(final Collection<String> watchers, final String errorMessage);

    ChangesNotifier getChangesNotifier();
}
