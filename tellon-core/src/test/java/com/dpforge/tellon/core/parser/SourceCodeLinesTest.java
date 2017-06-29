package com.dpforge.tellon.core.parser;

import com.dpforge.tellon.core.BlockPosition;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

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
        final BlockPosition position = BlockPosition.createHumanBased(2, 1);
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
        final BlockPosition start = BlockPosition.createHumanBased(3, 9);
        final BlockPosition end = BlockPosition.createHumanBased(3, 15);
        assertArrayEquals(new String[]{"l int i"}, lines.getExactRange(start, end));
    }

    @Test
    public void getExactSubsetTwoLines() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "    final int index;",
                "}"
        });
        final BlockPosition start = BlockPosition.createHumanBased(2, 7);
        final BlockPosition end = BlockPosition.createHumanBased(3, 9);
        assertArrayEquals(new String[]{"ring value;", "    final"}, lines.getExactRange(start, end));
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
        final BlockPosition start = BlockPosition.createHumanBased(1, 11);
        final BlockPosition end = BlockPosition.createHumanBased(4, 5);
        assertArrayEquals(new String[]{"{", "    String value;", "    final int index;", "    /"},
                lines.getExactRange(start, end));
    }

    @Test
    public void getLineRange() throws Exception {
        final SourceCodeLines lines = SourceCodeLines.create(new String[]{
                "class Foo {",
                "    String value;",
                "    final int index;",
                "}"
        });
        final BlockPosition start = BlockPosition.createHumanBased(2, 100);
        final BlockPosition end = BlockPosition.createHumanBased(3, 100);
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