package com.dpforge.tellon.core.parser;

import java.util.*;

public class SourceCodeLines implements Iterable<String> {
    private final List<String> lines;

    private SourceCodeLines(List<String> lines) {
        this.lines = lines;
    }

    public int size() {
        return lines.size();
    }

    public String get(int index) {
        if (index < 0 || index >= lines.size()) {
            throw new IndexOutOfBoundsException(String.format(
                    "Trying to get line with index %d but source contains only %d line(s)",
                    index,
                    lines.size()
            ));
        }
        return lines.get(index);
    }

    public String get(BlockPosition position) {
        return get(position.getLine());
    }

    public List<String> getExactRange(final BlockPosition start, final BlockPosition end) {
        if (start.getLine() == end.getLine()) {
            final String line = get(start);
            return Collections.singletonList(line.substring(start.getColumn(), end.getColumn() + 1));
        }

        final int rangeLength = end.getLine() - start.getLine() + 1;
        final List<String> code = new ArrayList<>(rangeLength);
        code.add(get(start).substring(start.getColumn()));
        if (rangeLength > 2) {
            code.addAll(lines.subList(start.getLine() + 1, end.getLine()));
        }
        code.add(get(end).substring(0, end.getColumn() + 1));
        return code;
    }

    public List<String> getLineRange(final BlockPosition start, final BlockPosition end) {
        return lines.subList(start.getLine(), end.getLine() + 1);
    }

    @Override
    public Iterator<String> iterator() {
        return lines.iterator();
    }

    public static SourceCodeLines create(Collection<String> lines) {
        return new SourceCodeLines(new ArrayList<>(lines));
    }

    public static SourceCodeLines create(String[] code) {
        return new SourceCodeLines(Arrays.asList(code));
    }
}
