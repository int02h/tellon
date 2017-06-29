package com.dpforge.tellon.core.observer;

public interface ProjectWalker {

    boolean hasNext();

    ProjectItem next();
}
