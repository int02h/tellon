package com.dpforge.tellon.core.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.Arrays;

public abstract class SourceCode {
    private SourceCode() {
    }

    abstract String[] getContent();

    abstract CompilationUnit toCompilationUnit();

    public static SourceCode createFromContent(final String content) {
        return new ContentSourceCode(content);
    }

    private static class ContentSourceCode extends SourceCode {
        private final String code;
        private final String[] lines;

        private ContentSourceCode(String code) {
            this.code = code;
            this.lines = code.split("\n");
        }

        @Override
        public String[] getContent() {
            return Arrays.copyOf(lines, lines.length);
        }

        @Override
        CompilationUnit toCompilationUnit() {
            return JavaParser.parse(code);
        }
    }
}
