package com.dpforge.tellon.core.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;

public abstract class SourceCode {
    private SourceCode() {
    }

    abstract CompilationUnit toCompilationUnit() throws IOException;

    public static SourceCode createFromFile(final File file) {
        return new FileSourceCode(file);
    }

    public static SourceCode createFromContent(final String content) {
        return new ContentSourceCode(content);
    }

    private static class FileSourceCode extends SourceCode {
        private final File file;

        FileSourceCode(File file) {
            this.file = file;
        }

        @Override
        CompilationUnit toCompilationUnit() throws IOException {
            return JavaParser.parse(file);
        }
    }

    private static class ContentSourceCode extends SourceCode {
        private final String content;

        private ContentSourceCode(String content) {
            this.content = content;
        }

        @Override
        CompilationUnit toCompilationUnit() throws IOException {
            return JavaParser.parse(content);
        }
    }
}
