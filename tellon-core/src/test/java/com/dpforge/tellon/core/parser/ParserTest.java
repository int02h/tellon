package com.dpforge.tellon.core.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {
    @Test
    public void annotatedClass() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\")" +
                "class Foo {}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerClass() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"someone\") class Bar {}" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerStaticClass() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"someone\") static class Bar {}" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInterface() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\")" +
                "interface Foo {}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerInterface() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "interface Foo {" +
                "    @NotifyChanges(\"someone\") interface Bar {}" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedAnnotation() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\")" +
                "@interface Foo {}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.ANNOTATION);
    }

    @Test
    public void annotatedInnerAnnotation() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@interface Foo {" +
                "    @NotifyChanges(\"someone\") @interface Bar {}" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.ANNOTATION);
    }

    @Test
    public void annotatedClassMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"ctor\")" +
                "    Foo() {}" +
                "    @NotifyChanges(\"method\")" +
                "    void doIt() {}" +
                "    @NotifyChanges(\"field\")" +
                "    int value;" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.CONSTRUCTOR, BlockType.METHOD, BlockType.FIELD);
    }

    @Test
    public void annotatedCompoundField() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"field\")" +
                "    int a, b, c;" +
                "    @NotifyChanges(\"field\")" +
                "    String x, y, z;" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.FIELD, BlockType.FIELD);
    }

    @Test
    public void annotatedInnerClassMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    class Bar {" +
                "        @NotifyChanges(\"ctor\")" +
                "        Foo() {}" +
                "        @NotifyChanges(\"method\")" +
                "        void doIt() {}" +
                "        @NotifyChanges(\"field\")" +
                "        int value;" +
                "    }" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.CONSTRUCTOR, BlockType.METHOD, BlockType.FIELD);
    }

    @Test
    public void annotatedClassStaticMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"test\")" +
                "    static final int value = 123;" +
                "    @NotifyChanges(\"test\")" +
                "    static String doIt(int param) { return null; }" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.FIELD, BlockType.METHOD);
    }

    @Test
    public void annotatedInterfaceMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "interface Foo {" +
                "    @NotifyChanges(\"test\")" +
                "    void method1();" +
                "    @NotifyChanges(\"test\")" +
                "    void method2();" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.METHOD, BlockType.METHOD);
    }

    @Test
    public void annotatedInterfaceStaticMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "interface Foo {" +
                "    @NotifyChanges(\"test\")" +
                "    static void bar() {}" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.METHOD);
    }

    @Test
    public void annotatedAnnotationMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@interface Annotation {" +
                "    @NotifyChanges(\"test\")" +
                "    int value() default -1;" +
                "    @NotifyChanges(\"test\")" +
                "    int[] data();" +
                "}";
        assertBlocks(new SourceCodeParser().parse(code), BlockType.ANNOTATION_MEMBER, BlockType.ANNOTATION_MEMBER);
    }

    @Test
    public void positionTest() {
        final String code = "package com.test;\n" +
                "import com.dpforge.tellon.annotations.NotifyChanges;\n" +
                "class Foo {\n" +
                "    @NotifyChanges(\"test\")\n" +
                "    @NonNull\n" +
                "    @Deprecated\n" +
                "    void check() { }\n" +
                "}";
        ParsedSourceCode sourceCode = new SourceCodeParser().parse(code);
        AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(4, block.getStartPosition().getColumn());
        assertEquals(3, block.getStartPosition().getLine());
        assertEquals(19, block.getEndPosition().getColumn());
        assertEquals(6, block.getEndPosition().getLine());
    }

    @Test
    public void noAnnotatedMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    static final int TAG = 123;" +
                "    int a;" +
                "    String qwe, asd, zxc;" +
                "    Foo() {}" +
                "    Foo(int value) {}" +
                "    void bar() {}" +
                "    class Bar { int innerValue; }" +
                "    static class StaticBar { String innerValue; }" +
                "}";
        ParsedSourceCode sourceCode = new SourceCodeParser().parse(code);
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }

    @Test
    public void singleWatcher() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"some_watcher@example.com\")" +
                "    int a;" +
                "}";
        ParsedSourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());

        AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(1, block.getWatchers().size());
        assertEquals("some_watcher@example.com", block.getWatchers().get(0));
    }

    @Test
    public void multipleWatchers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges({\"watcher1@example.com\", \"watcher2@example.com\"})" +
                "    int a;" +
                "}";
        ParsedSourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());

        AnnotatedBlock block = sourceCode.getAnnotatedBlocks().get(0);
        assertEquals(2, block.getWatchers().size());
        assertEquals("watcher1@example.com", block.getWatchers().get(0));
        assertEquals("watcher2@example.com", block.getWatchers().get(1));
    }

    private static void assertBlocks(final ParsedSourceCode sourceCode, final BlockType... blockTypes) {
        assertEquals(blockTypes.length, sourceCode.getAnnotatedBlocks().size());
        for (int i = 0; i < blockTypes.length; i++) {
            assertEquals(blockTypes[i], sourceCode.getAnnotatedBlocks().get(i).getType());
        }
    }
}