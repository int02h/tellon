package com.dpforge.tellon.core;

public class Revision {
    private final String version;
    private final String author;

    private Revision(final Builder builder) {
        this.version = builder.version;
        this.author = builder.author;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public static class Builder {
        private final String version;
        private String author;

        public Builder(String version) {
            this.version = version;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Revision build() {
            return new Revision(this);
        }
    }
}
