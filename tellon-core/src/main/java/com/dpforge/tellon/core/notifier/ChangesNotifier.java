package com.dpforge.tellon.core.notifier;

import com.dpforge.tellon.core.Changes;
import com.dpforge.tellon.core.ProjectItem;

public interface ChangesNotifier {
    String getName();

    String getDescription();

    void onStartProject(final ProjectInfo projectInfo);

    void onFinishedProject();

    void notifyChanges(final ProjectItem item, final Changes changes);

    void notifyItemAdded(final ProjectItem item);

    void notifyItemDeleted(final ProjectItem item);
}
