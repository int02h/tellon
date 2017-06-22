package com.dpforge.tellon.core.parser;

import com.github.javaparser.Position;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SourceCodeLinesTest {
    @Test
    public void createWithArray() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "}"
        });
        assertEquals(2, lines.size());
    }

    @Test
    public void createWithCollection() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(Arrays.asList(
                "class Foo {",
                "}"
        ));
        assertEquals(2, lines.size());
    }

    @Test
    public void getByIndex() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "}"
        });
        assertEquals("class Foo {", lines.get(0));
        assertEquals("}", lines.get(1));
    }

    @Test
    public void getByPosition() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "}"
        });
        final FilePosition position = FilePosition.create(new Position(2, 1));
        assertEquals("    String value;", lines.get(position));
    }

    @Test
    public void getWrong() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "}"
        });

        try {
            lines.get(-1);
            fail("No exception thrown");
        } catch (IndexOutOfBoundsException ignored) {
        }

        try {
            lines.get(10);
            fail("No exception thrown");
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void getExactSubsetOneLine() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "    final int index;",
                "}"
        });
        final FilePosition start = FilePosition.create(new Position(3, 9));
        final FilePosition end = FilePosition.create(new Position(3, 15));
        assertArrayEquals(new String[]{"l int i"}, lines.getExactSubset(start, end));
    }

    @Test
    public void getExactSubsetTwoLines() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "    final int index;",
                "}"
        });
        final FilePosition start = FilePosition.create(new Position(2, 7));
        final FilePosition end = FilePosition.create(new Position(3, 9));
        assertArrayEquals(new String[]{"ring value;", "    final"}, lines.getExactSubset(start, end));
    }

    @Test
    public void getExactSubsetMoreLines() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "    final int index;",
                "    // ololo",
                "}"
        });
        final FilePosition start = FilePosition.create(new Position(1, 11));
        final FilePosition end = FilePosition.create(new Position(4, 5));
        assertArrayEquals(new String[]{"{", "    String value;", "    final int index;", "    /"},
                lines.getExactSubset(start, end));
    }

    @Test
    public void getLineRange() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "    final int index;",
                "}"
        });
        final FilePosition start = FilePosition.create(new Position(2, 100));
        final FilePosition end = FilePosition.create(new Position(3, 100));
        assertArrayEquals(new String[]{"    String value;", "    final int index;"}, lines.getLineRange(start, end));
    }

    @Test
    public void iterator() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "}"
        });

        int index = 0;
        for (String line : lines) {
            switch (index++) {
                case 0:
                    assertEquals("class Foo {", line);
                    break;
                case 1:
                    assertEquals("    String value;", line);
                    break;
                case 2:
                    assertEquals("}", line);
                    break;
            }
        }
    }
}