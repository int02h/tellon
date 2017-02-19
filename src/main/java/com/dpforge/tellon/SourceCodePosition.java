package com.dpforge.tellon;

import com.github.javaparser.Position;

public class SourceCodePosition {
    private final int line;
    private final int column;

    private SourceCodePosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Zero-base line in source code
     */
    public int getLine() {
        return line;
    }

    /**
     * Zero-base column in source code
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "{column=" + column + ", line=" + line + "}";
    }

    static SourceCodePosition create(Position position) {
        return new SourceCodePosition(position.line - 1, position.column - 1);
    }
}
