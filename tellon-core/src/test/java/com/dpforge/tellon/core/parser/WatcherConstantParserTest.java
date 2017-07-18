package com.dpforge.tellon.core.parser;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WatcherConstantParserTest {
    @Test
    public void singleLiteral() {
        final Map<String, String> map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "}");
        assertEquals("Hello", map.get("A"));
    }

    @Test
    public void multipleLiteral() {
        try {
            parse(
                    "class Foo {",
                    "    static final String[] A = {\"Hello\", \"World\"};",
                    "}");
            fail();
        } catch (Exception e) {
            assertEquals("Field must be constant string not array", e.getMessage());
        }
    }

    @Test
    public void singleReference() {
        final Map<String, String> map = parse(
                "class Foo {",
                "    static final String A = \"Hello\";",
                "    static final String B = A;",
                "}");
        assertEquals("Hello", map.get("A"));
        assertEquals("Hello", map.get("B"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void referenceToAnother() {
        parse(
                "class Foo {",
                "    static final String B = Bar.A;",
                "}");
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

    private static Map<String, String> parse(final String... code) {
        return new WatcherConstantParser().parse(SourceCode.createFromContent(code));
    }
}