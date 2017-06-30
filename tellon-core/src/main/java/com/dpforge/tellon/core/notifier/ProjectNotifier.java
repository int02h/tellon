package com.dpforge.tellon.core.notifier;

public interface ProjectNotifier {
    String getName();

    String getDescription();

    void init() throws ChangesNotifierException;

    ChangesNotifier getChangesNotifier();
}
