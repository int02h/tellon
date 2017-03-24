package com.dpforge.tellon.app.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectConfig {
    private final String name;
    private final String path;
    private final List<String> masterContacts;

    private ProjectConfig(Builder builder) {
        name = builder.name;
        path = builder.path;
        masterContacts = builder.masterContacts;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public List<String> getMasterContacts() {
        return new ArrayList<>(masterContacts);
    }

    static class Builder {
        private String name;
        private String path;
        private List<String> masterContacts;

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder path(String path) {
            this.path = path;
            return this;
        }

        Builder masterContacts(List<String> masterContacts) {
            this.masterContacts = masterContacts;
            return this;
        }

        ProjectConfig build() {
            if (masterContacts == null) {
                masterContacts = Collections.emptyList();
            }
            return new ProjectConfig(this);
        }
    }
}
