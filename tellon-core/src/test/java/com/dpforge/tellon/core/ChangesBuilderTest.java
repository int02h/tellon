package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.SourceCode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangesBuilderTest {
    @Test
    public void swappedFields() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "}"});

        final SourceCode src2 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertTrue(changes.isEmpty());
    }

    @Test
    public void swappedAndUpdatedFields() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "}"});

        final SourceCode src2 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "    @NotifyChanges(\"a\")",
                "    Integer a;",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertFalse(changes.hasDeleted());
        assertFalse(changes.hasAdded());
        assertTrue(changes.hasUpdated());
    }

    @Test
    public void swappedAndRenamedFields() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "}"});

        final SourceCode src2 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"b\")",
                "    String b;",
                "    @NotifyChanges(\"a\")",
                "    Integer aaa;",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasDeleted());
        assertTrue(changes.hasAdded());
        assertFalse(changes.hasUpdated());
    }

    @Test
    public void swappedClassContent() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "@NotifyChanges(\"all\")",
                "class Foo {",
                "    int a;",
                "    String b;",
                "}"});

        final SourceCode src2 = createSourceCode(new String[]{
                "@NotifyChanges(\"all\")",
                "class Foo {",
                "    String b;",
                "    int a;",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasUpdated());
    }

    @Test
    public void deletedField() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}"});

        final SourceCode src2 = createSourceCode(new String[]{
                "class Foo {",
                "    String b;",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasDeleted());
    }

    @Test
    public void addedField() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "class Foo {",
                "    String b;",
                "}"});

        final SourceCode src2 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasAdded());
    }

    @Test
    public void updatedField() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    int a;",
                "}"});

        final SourceCode src2 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"a\")",
                "    Integer a;",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.isEmpty());
        assertTrue(changes.hasUpdated());
    }

    @Test
    public void sameName() throws Exception {
        final SourceCode src1 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"field\")",
                "    int a;",
                "}"});
        final SourceCode src2 = createSourceCode(new String[]{
                "class Foo {",
                "    @NotifyChanges(\"field\")",
                "    int a() { return 0; }",
                "}"});

        final Changes changes = buildChanges(src1, src2);
        assertFalse(changes.hasUpdated());
        assertTrue(changes.hasDeleted());
        assertTrue(changes.hasAdded());
    }

    private static SourceCode createSourceCode(final String[] clazz) {
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