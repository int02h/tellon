package com.dpforge.tellon.core.parser;

import java.util.List;

public class ParsedSourceCode {
    private final List<AnnotatedBlock> annotatedBlocks;

    ParsedSourceCode(List<AnnotatedBlock> annotatedBlocks) {
        this.annotatedBlocks = annotatedBlocks;
    }

    public List<AnnotatedBlock> getAnnotatedBlocks() {
        return annotatedBlocks;
    }
}
