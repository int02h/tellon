package com.dpforge.tellon.core.walker;

import java.util.Map;

public interface ProjectWalker {
    void init(Map<String, String> args) throws ProjectWalkerException;

    String getName();

    String getDescription();

    ProjectInfo getProjectInfo();

    boolean hasNext();

    ProjectItem next();
}
