package com.dpforge.tellon.core.observer;

public class ProjectInfo {
    private final String name;

    private ProjectInfo(Builder builder) {
        name = builder.name;
    }

    public String getName() {
        return name;
    }

    public static class Builder {
        private String name;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public ProjectInfo build() {
            return new ProjectInfo(this);
        }
    }
}
