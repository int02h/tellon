package com.dpforge.tellon;

import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest {
    @Test
    public void annotatedClass() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\")" +
                "class Foo {}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerClass() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"someone\") class Bar {}" +
                "}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerStaticClass() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "class Foo {" +
                "    @NotifyChanges(\"someone\") static class Bar {}" +
                "}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInterface() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\")" +
                "interface Foo {}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedInnerInterface() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "interface Foo {" +
                "    @NotifyChanges(\"someone\") interface Bar {}" +
                "}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.TYPE);
    }

    @Test
    public void annotatedAnnotation() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\")" +
                "@interface Foo {}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.ANNOTATION);
    }

    @Test
    public void annotatedInnerAnnotation() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@interface Foo {" +
                "    @NotifyChanges(\"someone\") @interface Bar {}" +
                "}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.ANNOTATION);
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
        assertBlocks(SourceCodeParser.parse(code), BlockType.CONSTRUCTOR, BlockType.METHOD, BlockType.FIELD);
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
        assertBlocks(SourceCodeParser.parse(code), BlockType.FIELD, BlockType.FIELD);
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
        assertBlocks(SourceCodeParser.parse(code), BlockType.CONSTRUCTOR, BlockType.METHOD, BlockType.FIELD);
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
        assertBlocks(SourceCodeParser.parse(code), BlockType.FIELD, BlockType.METHOD);
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
        assertBlocks(SourceCodeParser.parse(code), BlockType.METHOD, BlockType.METHOD);
    }

    @Test
    public void annotatedInterfaceStaticMembers() {
        final String code = "package com.test; import com.dpforge.tellon.annotations.NotifyChanges;" +
                "interface Foo {" +
                "    @NotifyChanges(\"test\")" +
                "    static void bar() {}" +
                "}";
        assertBlocks(SourceCodeParser.parse(code), BlockType.METHOD);
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
        assertBlocks(SourceCodeParser.parse(code), BlockType.ANNOTATION_MEMBER, BlockType.ANNOTATION_MEMBER);
    }

    private static void assertBlocks(final SourceCode sourceCode, final BlockType... blockTypes) {
        assertEquals(blockTypes.length, sourceCode.getAnnotatedBlocks().size());
        for (int i = 0; i < blockTypes.length; i++) {
            assertEquals(blockTypes[i], sourceCode.getAnnotatedBlocks().get(i).getType());
        }
    }
}