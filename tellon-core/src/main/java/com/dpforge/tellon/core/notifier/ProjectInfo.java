package com.dpforge.tellon.core.notifier;

public class ProjectInfo {
    private final String name;
    private final String path;

    public ProjectInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
