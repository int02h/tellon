package com.dpforge.tellon.core.notifier;

import com.dpforge.tellon.core.Changes;

public interface ChangesNotifier {
    String getName();

    String getDescription();

    void onStartProject(final ProjectInfo projectInfo);

    void onFinishedProject();

    void notifyChanges(final Changes changes);
}
