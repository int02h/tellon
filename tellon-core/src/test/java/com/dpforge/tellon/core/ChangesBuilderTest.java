package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.parser.resolver.SingleWatcherResolver;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ChangesBuilderTest {
    @Test
    public void swappedFields() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertTrue(changes.isEmpty());
    }

    @Test
    public void swappedAndUpdatedFields() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "    @NotifyChanges(\"a\")",
                "    Integer a;",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertFalse(changes.hasDeleted());
        assertFalse(changes.hasAdded());
        assertTrue(changes.hasUpdated());
    }

    @Test
    public void swappedAndRenamedFields() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "    @NotifyChanges(\"a\")",
                "    Integer aaa;",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasDeleted());
        assertTrue(changes.hasAdded());
        assertFalse(changes.hasUpdated());
    }

    @Test
    public void swappedClassContent() throws Exception {
        final SourceCode src1 = createSourceCode(
                "@NotifyChanges(\"all\")",
                "class Foo {",
                "    int a;",
                "    String b;",
                "}");

        final SourceCode src2 = createSourceCode(
                "@NotifyChanges(\"all\")",
                "class Foo {",
                "    String b;",
                "    int a;",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasUpdated());
    }

    @Test
    public void deletedField() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    String b;",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasDeleted());
    }

    @Test
    public void addedField() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    String b;",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasAdded());
    }

    @Test
    public void updatedField() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    Integer a;",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasUpdated());
    }

    @Test
    public void sameName() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"field\")",
                "    int a;",
                "}");
        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"field\")",
                "    int a() { return 0; }",
                "}");

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.hasUpdated());
        assertTrue(changes.hasDeleted());
        assertTrue(changes.hasAdded());
    }

    @Test
    public void javaDoc() throws Exception {
        final SourceCode src1 = createSourceCode(
                "interface Foo {",
                "    /**",
                "    /* This method does awesome things",
                "     */",
                "    @NotifyChanges(\"javadoc\")",
                "    abstract int foo();",
                "}");
        final SourceCode src2 = createSourceCode(
                "interface Foo {",
                "    /**",
                "    /* This method does awesome things (sometimes)",
                "     */",
                "    @NotifyChanges(\"javadoc\")",
                "    abstract int foo();",
                "}");
        final Changes changes = buildChanges(src1, src2);
        assertTrue(changes.hasUpdated());
        assertFalse(changes.hasDeleted());
        assertFalse(changes.hasAdded());
    }

    @Test
    public void insideLineComment() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"javadoc\")",
                "    void doSomeWork() {",
                "        int a = 123; // magic number",
                "        work(a);",
                "    }",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"javadoc\")",
                "    void doSomeWork() {",
                "        int a = 123; // constant from the doc",
                "        work(a);",
                "    }",
                "}");
        final Changes changes = buildChanges(src1, src2);
        assertTrue(changes.hasUpdated());
        assertFalse(changes.hasDeleted());
        assertFalse(changes.hasAdded());
    }

    @Test
    public void insideBlockComment() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"javadoc\")",
                "    void doSomeWork() {",
                "        int a = 123;",
                "        /*",
                "            This method does some work",
                "         */",
                "        work(a);",
                "    }",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"javadoc\")",
                "    void doSomeWork() {",
                "        int a = 123;",
                "        /*",
                "            This method does some work",
                "            Sometimes...",
                "         */",
                "        work(a);",
                "    }",
                "}");
        final Changes changes = buildChanges(src1, src2);
        assertTrue(changes.hasUpdated());
        assertFalse(changes.hasDeleted());
        assertFalse(changes.hasAdded());
    }

    @Test
    public void watcherResolver() throws Exception {
        final SourceCode src1 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"lower-case\")",
                "    int a;",
                "}");

        final SourceCode src2 = createSourceCode(
                "class Foo {",
                "    @NotifyChanges(\"lower-case\")",
                "    Integer a;",
                "}");
        final Changes changes = new ChangesBuilder(new SingleWatcherResolver() {
            @Override
            public String resolveLiteralSingle(String value) throws IOException {
                return value.toUpperCase();
            }

            @Override
            public String resolveReferenceSingle(String qualifiedName, String field) throws IOException {
                throw new IllegalStateException();
            }
        }).build(src1, src2);

        assertTrue(changes.hasUpdated());
        assertFalse(changes.hasDeleted());
        assertFalse(changes.hasAdded());

        assertEquals(1, changes.getUpdated().size());
        assertEquals("LOWER-CASE", changes.getUpdated().get(0).getOldBlock().getWatchers().get(0));
        assertEquals("LOWER-CASE", changes.getUpdated().get(0).getNewBlock().getWatchers().get(0));
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullWatcherResolver() {
        new ChangesBuilder(null);
    }

    private static SourceCode createSourceCode(final String... clazz) {
        final String[] code = new String[clazz.length + 2];
        code[0] = "package com.test;";
        code[1] = "import com.dpforge.tellon.annotations.NotifyChanges;";
        System.arraycopy(clazz, 0, code, 2, clazz.length);
        return SourceCode.createFromContent(code);
    }

    private static Changes buildChanges(final SourceCode src1, final SourceCode src2) throws IOException {
        return new ChangesBuilder().build(src1, src2);
    }
}