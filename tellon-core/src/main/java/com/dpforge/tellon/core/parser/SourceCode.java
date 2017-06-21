package com.dpforge.tellon.core.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.Arrays;

public abstract class SourceCode {
    private SourceCode() {
    }

    public abstract String[] getContent();

    public abstract String[] getContentFragment(int startLine, int endLine);

    public String[] getContentFragment(final AnnotatedBlock block) {
        if (block.getContainingSourceCode() != this) {
            throw new IllegalArgumentException("Block does not belong to this source code");
        }
        return getContentFragment(block.getStartPosition().getLine(), block.getEndPosition().getLine());
    }

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
        public String[] getContentFragment(int startLine, int endLine) {
            final String[] fragment = new String[endLine - startLine + 1];
            System.arraycopy(lines, startLine, fragment, 0, fragment.length);
            return fragment;
        }

        @Override
        CompilationUnit toCompilationUnit() {
            return JavaParser.parse(code);
        }
    }
}
