package com.dpforge.tellon;

import java.io.File;
import java.util.List;

public class SourceCodeFile {
    private final File file;
    private final List<AnnotatedBlock> annotatedBlocks;

    SourceCodeFile(File file, List<AnnotatedBlock> annotatedBlocks) {
        this.file = file;
        this.annotatedBlocks = annotatedBlocks;
    }

    public File getFile() {
        return file;
    }

    public List<AnnotatedBlock> getAnnotatedBlocks() {
        return annotatedBlocks;
    }
}
