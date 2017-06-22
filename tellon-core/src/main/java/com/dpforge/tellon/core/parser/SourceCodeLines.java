package com.dpforge.tellon.core.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class SourceCodeLines implements Iterable<String> {
    private final String[] lines;

    private SourceCodeLines(String[] lines) {
        this.lines = lines;
    }

    public String get(int index) {
        if (index < 0 || index >= lines.length) {
            throw new IndexOutOfBoundsException(String.format(
                    "Trying to get line with index %d but source contains only %d line(s)",
                    index,
                    lines.length
            ));
        }
        return lines[index];
    }

    public String get(FilePosition position) {
        return get(position.getLine());
    }

    public String[] getExactSubset(final FilePosition start, final FilePosition end) {
        if (start.getLine() == end.getLine()) {
            final String line = get(start);
            return new String[]{line.substring(start.getColumn(), end.getColumn() + 1)};
        }

        final String[] code = new String[end.getLine() - start.getLine() + 1];
        code[0] = get(start).substring(start.getColumn());
        if (code.length > 2) {
            System.arraycopy(lines, start.getLine() + 1, code, 1, code.length - 2);
        }
        code[code.length - 1] = get(end).substring(0, end.getColumn() + 1);
        return code;
    }

    public String[] getLineRange(final FilePosition start, final FilePosition end) {
        return Arrays.copyOfRange(lines, start.getLine(), end.getLine() + 1);
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return index + 1 < lines.length;
            }

            @Override
            public String next() {
                return lines[++index];
            }
        };
    }

    public static SourceCodeLines create(Collection<String> lines) {
        return new SourceCodeLines(lines.toArray(new String[lines.size()]));
    }

    public static SourceCodeLines create(String[] code) {
        return new SourceCodeLines(code);
    }
}
