package com.dpforge.tellon.core.parser;

public class BlockSourceCode {
    private final String raw;
    private final String fragment;

    BlockSourceCode(String raw, String fragment) {
        this.raw = raw;
        this.fragment = fragment;
    }

    public String asRaw() {
        return raw;
    }

    public String asFragment() {
        return fragment;
    }
}
