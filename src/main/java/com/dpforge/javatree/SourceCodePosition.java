package com.dpforge.javatree;

import com.github.javaparser.Position;

class SourceCodePosition {
    private final int line;
    private final int column;

    private SourceCodePosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "{column=" + column + ", line=" + line + "}";
    }

    static SourceCodePosition create(Position position) {
        return new SourceCodePosition(position.line, position.column);
    }
}
