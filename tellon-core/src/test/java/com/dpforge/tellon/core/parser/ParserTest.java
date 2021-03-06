package com.dpforge.tellon.core.parser;

import com.dpforge.tellon.core.parser.resolver.SingleWatcherResolver;
import com.dpforge.tellon.core.parser.resolver.WatcherResolver;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ParserTest {
    @Test
    public void annotatedClass() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "@NotifyChanges(\"someone\")",
                "class Foo {}"), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerClass() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"someone\") class Bar {}",
                "}"), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerStaticClass() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"someone\") static class Bar {}",
                "}"), BlockType.TYPE);
    }

    @Test
    public void annotatedInterface() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "@NotifyChanges(\"someone\")",
                "interface Foo {}"), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerInterface() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "interface Foo {",
                "    @NotifyChanges(\"someone\") interface Bar {}",
                "}"), BlockType.TYPE);
    }

    @Test
    public void annotatedAnnotation() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "@NotifyChanges(\"someone\")",
                "@interface Foo {}"), BlockType.ANNOTATION);
    }

    @Test
    public void annotatedInnerAnnotation() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "@interface Foo {",
                "    @NotifyChanges(\"someone\") @interface Bar {}",
                "}"), BlockType.ANNOTATION);
    }

    @Test
    public void annotatedClassMembers() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"ctor\")",
                "    Foo() {}",
                "    @NotifyChanges(\"method\")",
                "    void doIt() {}",
                "    @NotifyChanges(\"field\")",
                "    int value;",
                "}"), BlockType.CONSTRUCTOR, BlockType.METHOD, BlockType.FIELD);
    }

    @Test
    public void annotatedCompoundField() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"field\")",
                "    int a, b, c;",
                "    @NotifyChanges(\"field\")",
                "    String x, y, z;",
                "}"), BlockType.FIELD, BlockType.FIELD);
    }

    @Test
    public void annotatedInnerClassMembers() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    class Bar {",
                "        @NotifyChanges(\"ctor\")",
                "        Foo() {}",
                "        @NotifyChanges(\"method\")",
                "        void doIt() {}",
                "        @NotifyChanges(\"field\")",
                "        int value;",
                "    }",
                "}"), BlockType.CONSTRUCTOR, BlockType.METHOD, BlockType.FIELD);
    }

    @Test
    public void annotatedClassStaticMembers() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"test\")",
                "    static final int value = 123;",
                "    @NotifyChanges(\"test\")",
                "    static String doIt(int param) { return null; }",
                "}"), BlockType.FIELD, BlockType.METHOD);
    }

    @Test
    public void annotatedInterfaceMembers() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "interface Foo {",
                "    @NotifyChanges(\"test\")",
                "    void method1();",
                "    @NotifyChanges(\"test\")",
                "    void method2();",
                "}"), BlockType.METHOD, BlockType.METHOD);
    }

    @Test
    public void annotatedInterfaceStaticMembers() {
        assertBlocks(parse("package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "interface Foo {",
                "    @NotifyChanges(\"test\")",
                "    static void bar() {}",
                "}"), BlockType.METHOD);
    }

    @Test
    public void annotatedAnnotationMembers() {
        assertBlocks(parse(
                "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "@interface Annotation {",
                "    @NotifyChanges(\"test\")",
                "    int value() default -1;",
                "    @NotifyChanges(\"test\")",
                "    int[] data();",
                "}"), BlockType.ANNOTATION_MEMBER, BlockType.ANNOTATION_MEMBER);
    }

    @Test
    public void positionTest() {
        final ParsedSourceCode sourceCode = parse("package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"test\")",
                "    @NonNull",
                "    @Deprecated",
                "    void check() { }",
                "}");
        AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(4, block.getStartPosition().getColumn());
        assertEquals(3, block.getStartPosition().getLine());
        assertEquals(19, block.getEndPosition().getColumn());
        assertEquals(6, block.getEndPosition().getLine());
    }

    @Test
    public void noAnnotatedMembers() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    static final int TAG = 123;",
                "    int a;",
                "    String qwe, asd, zxc;",
                "    Foo() {}",
                "    Foo(int value) {}",
                "    void bar() {}",
                "    class Bar { int innerValue; }",
                "    static class StaticBar { String innerValue; }",
                "}");
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }

    @Test
    public void singleWatcher() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"some_watcher@example.com\")",
                "    int a;",
                "}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());

        AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(1, block.getWatchers().size());
        assertEquals("some_watcher@example.com", block.getWatchers().get(0));
    }

    @Test
    public void multipleWatchers() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges({\"watcher1@example.com\", \"watcher2@example.com\"})",
                "    int a;",
                "}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());

        AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(2, block.getWatchers().size());
        assertEquals("watcher1@example.com", block.getWatchers().get(0));
        assertEquals("watcher2@example.com", block.getWatchers().get(1));
    }

    @Test
    public void blockSourceCodeAsFragment() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class",
                "    Foo { @NotifyChanges(\"test\") int",
                "    value ;",
                "}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());

        final AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(Arrays.asList("@NotifyChanges(\"test\") int", "    value ;"),
                block.getSourceCode().asRaw());
        assertEquals(Arrays.asList("    Foo { @NotifyChanges(\"test\") int", "    value ;"),
                block.getSourceCode().asFragment());
    }

    @Test
    public void singleLineBlockSourceCode() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"test\") int sum;",
                "}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());

        AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(Collections.singletonList("@NotifyChanges(\"test\") int sum;"), block.getSourceCode().asRaw());
    }

    @Test
    public void watcherResolve() {
        final WatcherResolver watcherResolver = new SingleWatcherResolver() {
            @Override
            public String resolveLiteralSingle(String value) throws IOException {
                return "Resolved!";
            }

            @Override
            public String resolveReferenceSingle(String qualifiedName, String field) throws IOException {
                throw new IllegalStateException();
            }
        };
        final ParsedSourceCode parsed = new SourceCodeParser(watcherResolver).parse(SourceCode.createFromContent(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(\"test\") int value;",
                "}"));

        AnnotatedBlock block = parsed.getAnnotatedBlocks().get(0);
        assertEquals("Resolved!", block.getWatchers().get(0));
    }

    @Test(expected = NullPointerException.class)
    public void nullWatcherResolver() {
        new SourceCodeParser(null);
    }

    @Test
    public void referenceWatcher() {
        final WatcherResolver watcherResolver = new SingleWatcherResolver() {
            @Override
            public String resolveLiteralSingle(String value) throws IOException {
                throw new IllegalStateException();
            }

            @Override
            public String resolveReferenceSingle(String qualifiedName, String field) throws IOException {
                if ("com.watcher.Contacts".equals(qualifiedName) && "JOHNY".equals(field)) {
                    return "John Developer";
                }
                throw new IllegalArgumentException();
            }
        };
        final ParsedSourceCode parsed = new SourceCodeParser(watcherResolver).parse(SourceCode.createFromContent(
                "package com.test;",
                "import com.watcher.Contacts;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(Contacts.JOHNY) int value;",
                "}"));

        AnnotatedBlock block = parsed.getAnnotatedBlocks().get(0);
        assertEquals("John Developer", block.getWatchers().get(0));
    }

    @Test
    public void arrayReferenceWatcher() {
        final WatcherResolver watcherResolver = new SingleWatcherResolver() {
            @Override
            public String resolveLiteralSingle(String value) throws IOException {
                throw new IllegalStateException();
            }

            @Override
            public String resolveReferenceSingle(String qualifiedName, String field) throws IOException {
                if ("com.watcher.Contacts".equals(qualifiedName)) {
                    if ("JOHNY".equals(field)) {
                        return "John Developer";
                    }
                    if ("BOB".equals(field)) {
                        return "Bob Lead";
                    }
                }
                throw new IllegalArgumentException();
            }
        };
        final ParsedSourceCode parsed = new SourceCodeParser(watcherResolver).parse(SourceCode.createFromContent(
                "package com.test;",
                "import com.watcher.Contacts;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges({Contacts.JOHNY, Contacts.BOB}) int value;",
                "}"));

        AnnotatedBlock block = parsed.getAnnotatedBlocks().get(0);
        assertEquals("John Developer", block.getWatchers().get(0));
        assertEquals("Bob Lead", block.getWatchers().get(1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unsupportedWatcher() {
        parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(value = {\"test\"})",
                "    int value;",
                "}");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void noClassReferenceWatcher() {
        parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges(SOME_DEVELOPER)",
                "    int value;",
                "}");
    }

    @Test
    public void referenceWatcherException() {
        final WatcherResolver referenceWatcher = new SingleWatcherResolver() {
            @Override
            protected String resolveLiteralSingle(String value) throws IOException {
                throw new IOException("Test IO exception");
            }

            @Override
            protected String resolveReferenceSingle(String qualifiedName, String field) throws IOException {
                throw new IllegalStateException();
            }
        };
        try {
            new SourceCodeParser(referenceWatcher).parse(SourceCode.createFromContent(
                    "package com.test;",
                    "import com.dpforge.tellon.annotations.NotifyChanges;",
                    "class Foo {",
                    "    @NotifyChanges(\"test\")",
                    "    int value;",
                    "}"));
            fail("No exception thrown");
        } catch (Exception ex) {
            assertNotNull(ex.getCause());
            assertEquals("Test IO exception", ex.getCause().getMessage());
        }
    }

    @Test
    public void notImportedReferenceWatcher() {
        try {
            parse(
                    "package com.test;",
                    "import com.dpforge.tellon.annotations.NotifyChanges;",
                    "class Foo {",
                    "    @NotifyChanges(Contacts.SOME_DEVELOPER)",
                    "    int value;",
                    "}");
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Class 'Contacts' is not imported or imported in unsupported way", e.getMessage());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void emptyWatcherArgument() {
        parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges",
                "    int value;",
                "}");
    }

    @Test
    public void mixedWatchers() {
        final ParsedSourceCode parsed = new SourceCodeParser().parse(SourceCode.createFromContent(
                "package com.test;",
                "import com.watcher.Contacts;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "class Foo {",
                "    @NotifyChanges({\"test\", Contacts.TEST})",
                "    int value;",
                "}"));
        final AnnotatedBlock block = parsed.getAnnotatedBlocks().get(0);
        assertEquals("test", block.getWatchers().get(0));
        assertEquals("com.watcher.Contacts.TEST", block.getWatchers().get(1));
    }

    private static ParsedSourceCode parse(final String... code) {
        return new SourceCodeParser().parse(SourceCode.createFromContent(code));
    }

    private static void assertBlocks(final ParsedSourceCode sourceCode, final BlockType... blockTypes) {
        assertEquals(blockTypes.length, sourceCode.getAnnotatedBlocks().size());
        for (int i = 0; i < blockTypes.length; i++) {
            assertEquals(blockTypes[i], sourceCode.getAnnotatedBlocks().get(i).getType());
        }
    }
}