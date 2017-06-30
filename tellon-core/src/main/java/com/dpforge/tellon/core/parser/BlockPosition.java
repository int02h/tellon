package com.dpforge.tellon.core.parser;

public class BlockPosition {

    // zero-based
    private final int line;

    // zero-based
    private final int column;

    private BlockPosition(int line, int column) {
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

    /**
     * Human-readable (1-based) line in source code
     */
    @SuppressWarnings("unused")
    public int getHumanLine() {
        return line + 1;
    }

    /**
     * Human-readable (1-based) column in source code
     */
    @SuppressWarnings("unused")
    public int getHumanColumn() {
        return column + 1;
    }

    @Override
    public String toString() {
        return "{line=" + line + ", column=" + column + "}";
    }

    @SuppressWarnings("unused")
    public static BlockPosition createZeroBased(int line, int column) {
        return new BlockPosition(line, column);
    }

    public static BlockPosition createHumanBased(int line, int column) {
        return new BlockPosition(line - 1, column - 1);
    }
}
