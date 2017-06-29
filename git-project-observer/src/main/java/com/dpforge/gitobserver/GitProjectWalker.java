package com.dpforge.gitobserver;

import com.dpforge.tellon.core.observer.ProjectItem;
import com.dpforge.tellon.core.observer.ProjectWalker;

import java.util.List;

class GitProjectWalker implements ProjectWalker {

    private final List<ProjectItem> items;

    private int index = -1;

    public GitProjectWalker(List<ProjectItem> items) {
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return index + 1 < items.size();
    }

    @Override
    public ProjectItem next() {
        return items.get(++index);
    }
}
