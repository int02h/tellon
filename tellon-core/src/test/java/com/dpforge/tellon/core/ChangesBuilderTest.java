package com.dpforge.tellon.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChangesBuilderTest {
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertTrue(changes.isEmpty());
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertFalse(changes.isEmpty());
        assertFalse(changes.hasDeleted());
        assertFalse(changes.hasAdded());
        assertTrue(changes.hasUpdated());
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasDeleted());
        assertTrue(changes.hasAdded());
        assertFalse(changes.hasUpdated());
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasUpdated());
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasDeleted());
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasAdded());
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasUpdated());
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

        final Changes changes = new ChangesBuilder().build(src1, src2);
        assertFalse(changes.hasUpdated());
        assertTrue(changes.hasDeleted());
        assertTrue(changes.hasAdded());
    }

    private static String createSourceCode(final String clazz) {
        return "package com.test; " +
                "import com.dpforge.tellon.annotations.NotifyChanges; " +
                clazz;
    }
}