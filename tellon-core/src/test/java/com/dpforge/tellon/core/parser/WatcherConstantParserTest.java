package com.dpforge.tellon.core.parser;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WatcherConstantParserTest {
    @Test
    public void singleLiteral() {
        Map<String, List<String>> map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "}");
        assertEquals("Hello", map.get("A").get(0));
    }

    @Test
    public void multipleLiteralInitializer() {
        Map<String, List<String>> map = parse(
                "class Foo {",
                "    static final String[] A = {\"Hello\", \"World\"};",
                "}");
        assertEquals("Hello", map.get("A").get(0));
        assertEquals("World", map.get("A").get(1));
    }

    @Test
    public void multipleLiteralCreation() {
        Map<String, List<String>> map = parse(
                "class Foo {",
                "    static final String[] A = new String[] {\"Hello\", \"World\"};",
                "}");
        assertEquals("Hello", map.get("A").get(0));
        assertEquals("World", map.get("A").get(1));
    }

    @Test
    public void singleReference() {
        Map<String, List<String>> map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = A;",
                "}");
        assertEquals("Hello", map.get("A").get(0));
        assertEquals("Hello", map.get("B").get(0));
    }

    @Test
    public void referenceToMultiple() {
        Map<String, List<String>> map = parse(
                "class Foo {",
                "    static final String A = {\"Hello\", \"World\"};",
                "    static final String B = A;",
                "}");
        assertEquals("Hello", map.get("A").get(0));
        assertEquals("World", map.get("A").get(1));
        assertEquals("Hello", map.get("B").get(0));
        assertEquals("World", map.get("B").get(1));
    }

    @Test
    public void multipleReferenceInitializer() {
        Map<String, List<String>> map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = \"World\";",
                "    static final String[] C = {A, B};",
                "}");
        assertEquals("Hello", map.get("C").get(0));
        assertEquals("World", map.get("C").get(1));
    }

    @Test
    public void multipleReferenceCreation() {
        Map<String, List<String>> map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = \"World\";",
                "    static final String C[] = new String[] {A, B};",
                "}");
        assertEquals("Hello", map.get("C").get(0));
        assertEquals("World", map.get("C").get(1));
    }

    @Test
    public void nonStatic() {
        try {
            parse("class Foo {",
                    "    final String A = \"Hello\";",
                    "}");
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Field not static", e.getMessage());
        }
    }

    @Test
    public void nonFinal() {
        try {
            parse("class Foo {",
                    "    static String A = \"Hello\";",
                    "}");
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Field not final", e.getMessage());
        }
    }

    @Test
    public void wrongType() {
        try {
            parse("class Foo {",
                    "    static final Integer A = 1;",
                    "}");
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Field must be of type String", e.getMessage());
        }
    }

    private static Map<String, List<String>> parse(final String... code) {
        return new WatcherConstantParser().parse(SourceCode.createFromContent(code));
    }
}