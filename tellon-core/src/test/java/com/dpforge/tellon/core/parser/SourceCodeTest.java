package com.dpforge.tellon.core.parser;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SourceCodeTest {
    @Test
    public void createFromContentArray() throws Exception {
        final SourceCode sourceCode = SourceCode.createFromContent(new String[]{
                "class Foo {",
                "    int a;",
                "    String b;",
                "}"
        });
        assertEquals(4, sourceCode.getContent().size());
    }

    @Test
    public void createFromContentCollection() throws Exception {
        final SourceCode sourceCode = SourceCode.createFromContent(Arrays.asList(
                "class Foo {",
                "    int a;",
                "}"));
        assertEquals(3, sourceCode.getContent().size());
    }

}