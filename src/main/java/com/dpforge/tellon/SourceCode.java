package com.dpforge.tellon;

import java.io.File;
import java.util.List;

public class SourceCode {
    private final List<AnnotatedBlock> annotatedBlocks;

    SourceCode(List<AnnotatedBlock> annotatedBlocks) {
        this.annotatedBlocks = annotatedBlocks;
    }

    public List<AnnotatedBlock> getAnnotatedBlocks() {
        return annotatedBlocks;
    }
}
