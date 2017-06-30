package com.dpforge.tellon.core.parser;

import java.util.Collections;
import java.util.List;

public class BlockSourceCode {
    private final List<String> raw;
    private final List<String> fragment;

    BlockSourceCode(List<String> raw, List<String> fragment) {
        this.raw = raw;
        this.fragment = fragment;
    }

    public List<String> asRaw() {
        return Collections.unmodifiableList(raw);
    }

    public List<String> asFragment() {
        return Collections.unmodifiableList(fragment);
    }
}
