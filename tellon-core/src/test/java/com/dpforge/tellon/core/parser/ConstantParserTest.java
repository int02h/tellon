package com.dpforge.tellon.core.parser;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConstantParserTest {
    @Test
    public void singleLiteral() {
        final String[] code = {
                "class Foo {",
                "    static final String A = \"Hello\";",
                "}"
        };

        Map<String, List<String>> map = parse(code);
        assertEquals("Hello", map.get("A").get(0));
    }

    @Test
    public void multipleLiteralInitializer() {
        final String[] code = {
                "class Foo {",
                "    static final String[] A = {\"Hello\", \"World\"};",
                "}"
        };

        Map<String, List<String>> map = parse(code);
        assertEquals("Hello", map.get("A").get(0));
        assertEquals("World", map.get("A").get(1));
    }

    @Test
    public void multipleLiteralCreation() {
        final String[] code = {
                "class Foo {",
                "    static final String[] A = new String[] {\"Hello\", \"World\"};",
                "}"
        };

        Map<String, List<String>> map = parse(code);
        assertEquals("Hello", map.get("A").get(0));
        assertEquals("World", map.get("A").get(1));
    }

    @Test
    public void singleReference() {
        final String[] code = {
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = A;",
                "}"
        };

        Map<String, List<String>> map = parse(code);
        assertEquals("Hello", map.get("A").get(0));
        assertEquals("Hello", map.get("B").get(0));
    }

    @Test
    public void multipleReferenceInitializer() {
        final String[] code = {
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = \"World\";",
                "    static final String[] C = {A, B};",
                "}"
        };

        Map<String, List<String>> map = parse(code);
        assertEquals("Hello", map.get("C").get(0));
        assertEquals("World", map.get("C").get(1));
    }

    @Test
    public void multipleReferenceCreation() {
        final String[] code = {
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = \"World\";",
                "    static final String C[] = new String[] {A, B};",
                "}"
        };

        Map<String, List<String>> map = parse(code);
        assertEquals("Hello", map.get("C").get(0));
        assertEquals("World", map.get("C").get(1));
    }

    @Test
    public void nonStatic() {
        final String[] code = {
                "class Foo {",
                "    final String A = \"Hello\";",
                "}"
        };
        try {
            parse(code);
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Field not static", e.getMessage());
        }
    }

    @Test
    public void nonFinal() {
        final String[] code = {
                "class Foo {",
                "    static String A = \"Hello\";",
                "}"
        };
        try {
            parse(code);
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Field not final", e.getMessage());
        }
    }

    @Test
    public void wrongType() {
        final String[] code = {
                "class Foo {",
                "    static final Integer A = 1;",
                "}"
        };
        try {
            parse(code);
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Field must be of type String", e.getMessage());
        }
    }

    private static Map<String, List<String>> parse(final String[] code) {
        return new ConstantParser().parse(SourceCode.createFromContent(code));
    }
}