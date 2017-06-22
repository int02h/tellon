package com.dpforge.tellon.core.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.Collection;

public abstract class SourceCode {
    private SourceCode() {
    }

    abstract SourceCodeLines getContent();

    abstract CompilationUnit toCompilationUnit();

    public static SourceCode createFromContent(final String[] code) {
        return new ContentSourceCode(code);
    }

    public static SourceCode createFromContent(final Collection<String> code) {
        return new ContentSourceCode(code);
    }

    private static class ContentSourceCode extends SourceCode {
        private final SourceCodeLines code;

        private ContentSourceCode(final Collection<String> code) {
            this.code = SourceCodeLines.create(code);
        }

        private ContentSourceCode(final String[] code) {
            this.code = SourceCodeLines.create(code);
        }

        @Override
        SourceCodeLines getContent() {
            return code;
        }

        @Override
        CompilationUnit toCompilationUnit() {
            final StringBuilder builder = new StringBuilder();
            for (String line : code) {
                builder.append(line).append("\n");
            }
            return JavaParser.parse(builder.toString());
        }
    }
}
