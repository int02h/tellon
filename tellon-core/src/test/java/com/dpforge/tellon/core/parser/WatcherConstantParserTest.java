package com.dpforge.tellon.core.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WatcherConstantParserTest {
    @Test
    public void singleLiteral() {
        final WatcherMap map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "}");
        assertEquals("Hello", map.get("A", 0));
    }

    @Test
    public void multipleLiteralInitializer() {
        final WatcherMap map = parse(
                "class Foo {",
                "    static final String[] A = {\"Hello\", \"World\"};",
                "}");
        assertEquals("Hello", map.get("A", 0));
        assertEquals("World", map.get("A", 1));
    }

    @Test
    public void multipleLiteralCreation() {
        final WatcherMap map = parse(
                "class Foo {",
                "    static final String[] A = new String[] {\"Hello\", \"World\"};",
                "}");
        assertEquals("Hello", map.get("A", 0));
        assertEquals("World", map.get("A", 1));
    }

    @Test
    public void singleReference() {
        final WatcherMap map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = A;",
                "}");
        assertEquals("Hello", map.get("A", 0));
        assertEquals("Hello", map.get("B", 0));
    }

    @Test
    public void referenceToMultiple() {
        final WatcherMap map = parse(
                "class Foo {",
                "    static final String A = {\"Hello\", \"World\"};",
                "    static final String B = A;",
                "}");
        assertEquals("Hello", map.get("A", 0));
        assertEquals("World", map.get("A", 1));
        assertEquals("Hello", map.get("B", 0));
        assertEquals("World", map.get("B", 1));
    }

    @Test
    public void multipleReferenceInitializer() {
        final WatcherMap map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = \"World\";",
                "    static final String[] C = {A, B};",
                "}");
        assertEquals("Hello", map.get("C", 0));
        assertEquals("World", map.get("C", 1));
    }

    @Test
    public void multipleReferenceCreation() {
        final WatcherMap map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = \"World\";",
                "    static final String C[] = new String[] {A, B};",
                "}");
        assertEquals("Hello", map.get("C", 0));
        assertEquals("World", map.get("C", 1));
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

    @Test
    public void nonInitializedField() {
        try {
            parse("class Foo {",
                    "   static final String ABC;",
                    "}");
            fail("No exception thrown");
        } catch (RuntimeException e) {
            assertEquals("Field 'ABC' has no initializer", e.getMessage());
        }
    }

    @Test
    public void doubleInitialization() {
        try {
            // it is impossible in the correct code, but Tellon is able to work with broken code
            parse("class Foo {",
                    "   static final String ABC = \"Hello\";",
                    "   static final String ABC = \"World\";",
                    "}");
            fail("No exception thrown");
        } catch (RuntimeException e) {
            assertEquals("Field 'ABC' initialized more than once", e.getMessage());
        }
    }

    @Test
    public void runtimeInitializer() {
        try {
            parse("class Foo {",
                    "   static final String ABC = getAbcValue();",
                    "}");
            fail("No exception thrown");
        } catch (RuntimeException e) {
            assertEquals("Field 'ABC' must be initialized with string literal or string literal array", e.getMessage());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void runtimeArrayExpression() {
        parse("class Foo {",
                "   static final String ABC = { getAbcValue() };",
                "}");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void runtimeArrayCreation() {
        parse("class Foo {",
                "   static final String ABC = new String[] { getAbcValue() };",
                "}");
    }

    @Test
    public void arrayCreationWithoutInitializer() {
        try {
            parse("class Foo {",
                    "   static final String ABC = new String[0];",
                    "}");
            fail("No exception thrown");
        } catch (RuntimeException e) {
            assertEquals("Array creation without initializer: ABC", e.getMessage());
        }
    }

    @Test
    public void referenceToNonDeclared() {
        try {
            parse("class Foo {",
                    "   static final String ABC = NON_DECLARED;",
                    "}");
            fail("No exception thrown");
        } catch (RuntimeException e) {
            assertEquals("Reference to non-declared name: NON_DECLARED", e.getMessage());
        }
    }

    private static WatcherMap parse(final String... code) {
        return new WatcherConstantParser().parse(SourceCode.createFromContent(code));
    }
}