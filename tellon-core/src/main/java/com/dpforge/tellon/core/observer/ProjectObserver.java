package com.dpforge.tellon.core.observer;

import java.util.Map;

public interface ProjectObserver {

    void init(Map<String, String> args) throws ProjectObserverException;

    String getName();

    String getDescription();

    ProjectInfo getProjectInfo();

    ProjectWalker createWalker();
}
