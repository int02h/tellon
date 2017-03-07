package com.dpforge.tellon.app.config;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private final List<ProjectConfig> projects = new ArrayList<>();

    void addProject(final ProjectConfig projectConfig) {
        projects.add(projectConfig);
    }

    public List<ProjectConfig> getProjects() {
        return new ArrayList<>(projects);
    }
}
