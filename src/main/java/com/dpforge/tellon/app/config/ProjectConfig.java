package com.dpforge.tellon.app.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectConfig {
    private final String name;
    private final String path;
    private final String workDir;
    private final List<String> contacts;

    private ProjectConfig(Builder builder) {
        name = builder.name;
        path = builder.path;
        workDir = builder.workDir;
        contacts = builder.contacts;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getWorkDir() {
        return workDir;
    }

    public List<String> getContacts() {
        return new ArrayList<>(contacts);
    }

    static class Builder {
        private String name;
        private String path;
        private String workDir;
        private List<String> contacts;

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder path(String path) {
            this.path = path;
            return this;
        }

        Builder workDir(String workDir) {
            this.workDir = workDir;
            return this;
        }

        Builder masterContacts(List<String> contacts) {
            this.contacts = contacts;
            return this;
        }

        ProjectConfig build() {
            if (contacts == null) {
                contacts = Collections.emptyList();
            }
            return new ProjectConfig(this);
        }
    }
}
