package com.dpforge.tellon.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class DiffBuilderTest {
    @Test
    public void swappedFields() {
        final String src1 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"a\")" +
                "    int a;" +
                "    @NotifyChanges(\"b\")" +
                "    String b;" +
                "}");

        final String src2 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"b\")" +
                "    String b;" +
                "    @NotifyChanges(\"a\")" +
                "    int a;" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertTrue(diff.isEmpty());
    }

    @Test
    public void swappedAndUpdatedFields() {
        final String src1 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"a\")" +
                "    int a;" +
                "    @NotifyChanges(\"b\")" +
                "    String b;" +
                "}");

        final String src2 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"b\")" +
                "    String b;" +
                "    @NotifyChanges(\"a\")" +
                "    Integer a;" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertFalse(diff.isEmpty());
        assertFalse(diff.hasDeleted());
        assertFalse(diff.hasAdded());
        assertTrue(diff.hasUpdated());
    }

    @Test
    public void swappedAndRenamedFields() {
        final String src1 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"a\")" +
                "    int a;" +
                "    @NotifyChanges(\"b\")" +
                "    String b;" +
                "}");

        final String src2 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"b\")" +
                "    String b;" +
                "    @NotifyChanges(\"a\")" +
                "    Integer aaa;" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertFalse(diff.isEmpty());
        assertTrue(diff.hasDeleted());
        assertTrue(diff.hasAdded());
        assertFalse(diff.hasUpdated());
    }

    @Test
    public void swappedClassContent() {
        final String src1 = createSourceCode("" +
                "@NotifyChanges(\"all\")" +
                "class Foo {" +
                "    int a;" +
                "    String b;" +
                "}");

        final String src2 = createSourceCode("" +
                "@NotifyChanges(\"all\")" +
                "class Foo {" +
                "    String b;" +
                "    int a;" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertFalse(diff.isEmpty());
        assertTrue(diff.hasUpdated());
    }

    @Test
    public void deletedField() {
        final String src1 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"a\")" +
                "    int a;" +
                "}");

        final String src2 = createSourceCode("" +
                "class Foo {" +
                "    String b;" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertFalse(diff.isEmpty());
        assertTrue(diff.hasDeleted());
    }

    @Test
    public void addedField() {
        final String src1 = createSourceCode("" +
                "class Foo {" +
                "    String b;" +
                "}");

        final String src2 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"a\")" +
                "    int a;" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertFalse(diff.isEmpty());
        assertTrue(diff.hasAdded());
    }

    @Test
    public void updatedField() {
        final String src1 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"a\")" +
                "    int a;" +
                "}");

        final String src2 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"a\")" +
                "    Integer a;" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertFalse(diff.isEmpty());
        assertTrue(diff.hasUpdated());
    }

    @Test
    public void sameName() {
        final String src1 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"field\")" +
                "    int a;" +
                "}");
        final String src2 = createSourceCode("" +
                "class Foo {" +
                "    @NotifyChanges(\"field\")" +
                "    int a() { return 0; }" +
                "}");

        final Diff diff = new DiffBuilder().build(src1, src2);
        assertFalse(diff.hasUpdated());
        assertTrue(diff.hasDeleted());
        assertTrue(diff.hasAdded());
    }

    private static String createSourceCode(final String clazz) {
        return "package com.test; " +
                "import com.dpforge.tellon.annotations.NotifyChanges; " +
                clazz;
    }
}