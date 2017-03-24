package com.dpforge.tellon.core;

import com.dpforge.tellon.core.notifier.ProjectInfo;

public interface ProjectWalker {
    void init(String args);

    String getName();

    ProjectInfo getProjectInfo();

    boolean hasNext();

    ProjectItem next();
}
