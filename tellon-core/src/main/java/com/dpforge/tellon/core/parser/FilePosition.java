package com.dpforge.tellon.core.parser;

import com.github.javaparser.Position;

public class FilePosition {
    private final int line;
    private final int column;

    private FilePosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    /**
     * Zero-based line in source code
     */
    public int getLine() {
        return line;
    }

    /**
     * Zero-based column in source code
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "{line=" + line + ", column=" + column + "}";
    }

    static FilePosition create(Position position) {
        return new FilePosition(position.line - 1, position.column - 1);
    }
}
